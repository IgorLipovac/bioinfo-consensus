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
	
	public static void setReadsFilePath (String path) {
		readsFilePath = path;
	}

	
	
	private static Read GetReadFromFasta(int index, int startInd, int endInd, int offset)
	{
		ArrayList<Character> qua = new ArrayList<Character>();
		String sequence = null;
		BufferedReader br = null;
		try
		{		
			br = new BufferedReader(new FileReader(readsFilePath));
			String line = br.readLine();
			if (line.startsWith(">")){
				String[] splitt = line.split("_L");
				if (splitt.length < 2){
					int currentLine=0;
					while(currentLine!=(index-1)*2)
					{
						br.readLine();
						currentLine++;
					}
					sequence = br.readLine();
					br.close();
				}  else {
					while(line != null){
						String part = line.split("_L")[0];
						String splitted = part.split("_",2)[1];
						Long number = getNumber(splitted);
						if (number == index) {
							sequence = br.readLine();
							br.close();
							break;
						}
						br.readLine();
						line = br.readLine();
					}				
				}			
			} else if (line.startsWith("@")){
				String[] splitt = line.split("_L");
				if (splitt.length < 2){
					int currentLine=0;
					while(currentLine!=(index-1)*4)
					{
						br.readLine();
						br.readLine();
						br.readLine();
						currentLine += 3;
					}
					sequence = br.readLine();
					String plus = br.readLine();
					if(!plus.startsWith("+")){
						System.out.println("File isn't fastq");
						System.exit(-1);
					}
					String qual = br.readLine();
					char[] qualArray = qual.toCharArray();
					for (char c : qualArray){
						qua.add(c);
					}
					br.close();
				} else {
					while(line != null){
						String part = line.split("_L")[0];
						String splitted = part.split("_",2)[1];
						Long number = getNumber(splitted);
						if (number == index) {
							sequence = br.readLine();
							String plus = br.readLine();
							if(!plus.startsWith("+")){
								System.out.println("File isn't fastq");
								System.exit(-1);
							}
							String qual = br.readLine();
							char[] qualArray = qual.toCharArray();
							for (char c : qualArray){
								qua.add(c);
							}
							br.close();
							break;
						}
						br.readLine();
						br.readLine();
						br.readLine();
						line = br.readLine();
					}
				}
				
			} else {
				System.out.println("Wrong input file");
				System.exit(-1);
			}		
		}
		catch(Exception e)
		{
			System.out.print(e.getMessage());
		}
		
		char[] seqArray = sequence.toCharArray();
		ArrayList<Character> cList = new ArrayList<Character>();
		for(char c : seqArray) {
		    cList.add(c);
		}
		
		Read read = new Read(index, startInd, endInd, offset, cList, qua);
		return read;
	}

	
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
					Read temp =  GetReadFromFasta(index, startInd, endInd, offset);
					
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
		return layoutList;
	}
	
	private static long getNumber(String toBeFormatted) {
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
		
		return Long.parseLong(sb.toString());
	}
}
