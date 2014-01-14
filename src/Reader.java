import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class Reader {
	
	private static String readsFilePath;
	
	public static void setReadsFilePath (String path) {
		readsFilePath = path;
	}
	// should be refactored due to Fasta format specifics!
	public static List<Character> GetReadFromFasta(int index)
	{
		String sequence = null;
		BufferedReader br = null;
		try
		{		
			br = new BufferedReader(new FileReader(readsFilePath));
			int currentLine=0;
			while(currentLine!=(index-1)*2)
			{
				String line = br.readLine();
				currentLine++;
			}
			br.readLine();
			sequence = br.readLine();
			br.close();
			
		}
		catch(Exception e)
		{
			System.out.print(e.getMessage());
		}
		
		char[] seqArray = sequence.toCharArray();
		List<Character> cList = new ArrayList<Character>();
		for(char c : seqArray) {
		    cList.add(c);
		}
		
		return cList;
	}

	
	public static List<Alignment> GetLayout(String layoutPath)
	{
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
					ArrayList<Character> sequence = (ArrayList<Character>) GetReadFromFasta(index);
					Read temp = new Read(index, startInd, endInd, offset, sequence);
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
}
