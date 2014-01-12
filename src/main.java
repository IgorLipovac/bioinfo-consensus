
import java.util.ArrayList;
import java.util.List;

public class main {

	public static void main(String[] args) {
		
		Reader.setReadsFilePath(args[0]);
		Alignment test = Reader.GetLayout( args[1]);
		//Realigner.getConsensus(test);
		Realigner.reAlign(test, 0.1);
	}

}
