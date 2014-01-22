import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class Reader {
	
	private static Map<Integer,String> allReads;
	private static Map<Integer,String> allQuals;
	
	
	/**
	 * Method that reads a fasta/fastq file and reads all the reads in the file.
	 * @param readsFilePath Path to the file containing the reads.
	 */
	public static void GetAllReads (String readsFilePath) {
		allReads = new HashMap<Integer, String>();
		allQuals = new HashMap<Integer, String>();
		BufferedReader br = null;
		try
		{		
			br = new BufferedReader(new FileReader(readsFilePath));
			//FASTA
			if (readsFilePath.endsWith(".fasta") || readsFilePath.endsWith(".fa") || readsFilePath.endsWith(".fna")) {
				String line = br.readLine();
				Integer indexOrder = 0;
				Integer index = indexOrder;
				String sequence = "";
				String qua = "";
				while (line!=null) {
					if (line.startsWith(">")){
						if (sequence!="") {
							allReads.put(index, sequence);
							allQuals.put(index, qua);
							sequence = "";
							qua = "";
							indexOrder++;
						}
						String[] splitt = line.split("_L");
						if (splitt.length == 2){
							String part = line.split("_L")[0];
							String splitted = part.split("_",2)[1];
							Integer number = getNumber(splitted);
							index = number;
						} else {
							index = indexOrder;
						}
						
					} else {
						sequence += line;
					}
					line = br.readLine();
					
				}
				if (sequence!="") {
					allReads.put(index, sequence);
					allQuals.put(index, qua);
					sequence = "";
					qua = "";
					indexOrder++;
				}
			}
			else if (readsFilePath.endsWith(".fq") || readsFilePath.endsWith(".fastq") || readsFilePath.endsWith(".fnq")){
				String line = br.readLine();
				Integer indexOrder = 0;
				Integer index = indexOrder;
				String sequence = "";
				String qua = "";
				while (line!=null) {
					if (line.startsWith("@")){
						if (sequence!="") {
							allReads.put(index, sequence);
							allQuals.put(index, qua);
							sequence = "";
							qua ="";
							indexOrder++;
						}
						String[] splitt = line.split("_L");
						if (splitt.length == 2){
							String part = line.split("_L")[0];
							String splitted = part.split("_",2)[1];
							Integer number = getNumber(splitted);
							index = number;
						} else {
							index = indexOrder;
						}
						sequence = br.readLine();
						String plus = br.readLine();
						if(!plus.startsWith("+")){
							System.out.println("File isn't fastq");
							System.exit(-1);
						}
						qua = br.readLine();
						line = br.readLine();
					}
					if (sequence!="") {
						allReads.put(index, sequence);
						allQuals.put(index, qua);
						sequence = "";
						qua ="";
						indexOrder++;
					}
				}
			
			} else {
				System.out.println("Wrong input file or format of input file.");
				System.exit(-1);
			}	
		} 
		catch(Exception e)
		{
			System.out.print(e.getMessage());
		}
	}
	
	/**
	 * Method that reads a file containing all the layouts and creates a list of Alignments.
	 * @param layoutPath Path to the file to be read.
	 * @return List of alignments found in the file.
	 */
	public static List<Alignment> GetLayout(String layoutPath) {
		List <Alignment> layoutList = new LinkedList<Alignment>();
		BufferedReader br = null;
		
		try{
			String sCurrentLine;			
			br = new BufferedReader(new FileReader(layoutPath));
			Map<Integer, Read> layouts = new HashMap<Integer, Read>();
			int laysID = 1;
			while((sCurrentLine=br.readLine())!=null)
			{
				if (sCurrentLine.contains("LAY")) {
					if (!layouts.isEmpty()) {
						Alignment lays = new Alignment(layouts);
						lays.setLayoutID(laysID);
						laysID++;
						layoutList.add(lays);
						layouts = new HashMap<Integer, Read>();
					}
					
				} else if(sCurrentLine.contains("TLE")) {
					sCurrentLine = br.readLine();
					String[] splitted = sCurrentLine.split("[:,]");
					int startInd = Integer.parseInt(splitted[1]);
					int endInd = Integer.parseInt(splitted[2]);
					// reverse orientation
					boolean isReverse = false;
					if (startInd > endInd) {
						int temp = startInd;
						startInd = endInd;
						endInd = temp;
						isReverse = true;
					}
					sCurrentLine = br.readLine();
					splitted = sCurrentLine.split("[:]");
					int offset = Integer.parseInt(splitted[1]);
					sCurrentLine = br.readLine();
					splitted = sCurrentLine.split("[:]");
					int index = Integer.parseInt(splitted[1]);
					String sequence = allReads.get(index);
					String qual = allQuals.get(index);
					
					char[] seqArray = sequence.toCharArray();
					
					ArrayList<Character> cList = new ArrayList<Character>();
					for(char c : seqArray) {
						if (c =='A' || c=='C' || c=='G' || c=='T' || c=='-' || c=='N'){
							cList.add(c);
						} else {
							System.out.println("Wrong input file or format of input file. Character mismatch! Expected ACTG-");
							System.exit(-1);
						}
					}
					
					char[] qualArray = qual.toCharArray();
					ArrayList<Character> qList = new ArrayList<Character>();
					for(char c : qualArray) {
					    qList.add(c);
					}
					
					if (isReverse) {
						Collections.reverse(qList);
						Collections.reverse(cList);
					}
					
					Read temp =  new Read(index, startInd, endInd, offset, cList, qList);
					
					layouts.put(Integer.parseInt(splitted[1]), temp);
				}				
			}
			if (!layouts.isEmpty()) {
				Alignment lays = new Alignment(layouts);
				lays.setLayoutID(laysID);
				layoutList.add(lays);
				layouts = new HashMap<Integer, Read>();
			}
			br.close();
		}
		catch(Exception e)
		{
			System.out.print("Something went wrong - there is a possibility if layout file is wrong. Possible duplicate Reads in same LAY.");
		}
		
		allQuals.clear();
		allReads.clear();
		return layoutList;
	}
	
	private static Integer getNumber(String toBeFormatted) {
		StringBuilder sb = new StringBuilder();
		char[] characters = toBeFormatted.toCharArray();
		
		for (int i = 0; i < characters.length; i++){
			if (characters[i] == '_') continue;
			else if (Character.isDigit(characters[i])){
				sb.append(characters[i]);
			} else{
				System.out.println(characters[i]);
				System.out.println("Invalid format for number");
				System.exit(-1);
			}
		}
		
		return Integer.parseInt(sb.toString());
	}
}
