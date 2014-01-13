
import java.util.ArrayList;
import java.util.List;

public class main {

	public static void main(String[] args) {
		
		Reader.setReadsFilePath(args[0]);
		List<Alignment> test = Reader.GetLayout( args[1]);
		//Realigner.getConsensus(test);
		Consensus cons = Realigner.reAlign(test.get(0), 0.1);
		Writer.printUngappedConsensusToFile("consensus123.txt",cons);
	}

}
