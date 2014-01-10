
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class main {

	public static void main(String[] args) {
		
		Reader.setReadsFilePath(args[0]);
		Map<Integer, Layout> test = Reader.GetLayout( args[1]);
		List<Integer> keys = new ArrayList<Integer>(test.keySet());

		for (Integer key: keys) {
			Layout l=test.get(key);
		    System.out.println(key + ": " + l.offset +","+l.startIndex+","+l.endIndex);
		    
		}
		
		Realigner.getConsensus(test);
		
	}

}
