import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;



public class Reader {
	
	public static String readsFilePath;
	
	public static void setReadsFilePath (String path) {
		readsFilePath = path;
	}
	
	public static char[] GetReadFromFasta(int index)
	{
		String sequence = null;
		BufferedReader br = null;
		try
		{		
			br = new BufferedReader(new FileReader(readsFilePath));
			int currentLine=0;
			while(currentLine!=(index-1)*2)
			{
				br.readLine();
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
		return sequence.toCharArray();
	}

	
	public static Alignment GetLayout(String layoutPath)
	{
		Map<Integer, Read> layouts = new HashMap<Integer, Read>();
		BufferedReader br = null;
		try{
			String sCurrentLine;			
			br = new BufferedReader(new FileReader(layoutPath));
			while((sCurrentLine=br.readLine())!=null)
			{
				if(sCurrentLine.contains("TLE"))
				{
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
					Read temp = new Read(index, startInd, endInd, offset);
					layouts.put(Integer.parseInt(splitted[1]), temp);
				}
				else
				{
					continue;
				}
				
			}
			br.close();
		}
		catch(Exception e)
		{
			System.out.print(e.getMessage());
		}
		Alignment lays = new Alignment(layouts);
		return lays;
	}
}
