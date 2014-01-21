import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class Reader {
	
	private static String readsFilePath;
	private static Map<Integer,String> allReads;
	private static Map<Integer,String> allQuals;
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
		System.out.print('s');
	}
	
	// This method reads fasta/fastq file and creates read objects
	

	
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
						System.out.println(layoutList.size());
						layouts = new HashMap<Integer, Read>();
					}
					
				} else if(sCurrentLine.contains("TLE")) {
					sCurrentLine = br.readLine();
					String[] splitted = sCurrentLine.split("[:,]");
					int startInd = Integer.parseInt(splitted[1]);
					int endInd = Integer.parseInt(splitted[2]);
					// reverse orientation
					if (startInd > endInd) {
						int temp = startInd;
						startInd = endInd;
						endInd = temp;
					}
					sCurrentLine = br.readLine();
					splitted = sCurrentLine.split("[:]");
					int offset = Integer.parseInt(splitted[1]);
					sCurrentLine = br.readLine();
					splitted = sCurrentLine.split("[:]");
					int index = Integer.parseInt(splitted[1]);
					String sequence = allReads.get(index);
					String qual = allReads.get(index);
					
					char[] seqArray = sequence.toCharArray();
					ArrayList<Character> cList = new ArrayList<Character>();
					for(char c : seqArray) {
					    cList.add(c);
					}
					
					char[] qualArray = qual.toCharArray();
					ArrayList<Character> qList = new ArrayList<Character>();
					for(char c : qualArray) {
					    cList.add(c);
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
			System.out.print(e.getMessage());
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
