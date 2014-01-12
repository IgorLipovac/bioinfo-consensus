import java.util.ArrayList;
import java.util.Arrays;



public class Read {
	
	public int readIndex;
	public int offset;
	public int startIndex;
	public int endIndex;
	public int length;
	public ArrayList<Character> sequence;
	
	// Initializing constructor
	public Read(int index, int startInd, int endInd, int offset2) {
		this.readIndex = index;
		this.offset = offset2;
		this.startIndex = startInd;
		this.endIndex = endInd;
		this.length = Math.abs(endInd - startInd);
		this.sequence = (ArrayList<Character>) Reader.GetReadFromFasta(index);
	}
	
	// usual
	public Read(int index, int startInd, int endInd, int offset2, String seq) {
		this.readIndex = index;
		this.offset = offset2;
		this.startIndex = startInd;
		this.endIndex = endInd;
		this.length = Math.abs(endInd - startInd);
		char[] seqArray = seq.toCharArray();
		this.sequence = new ArrayList<Character>();
		for (int i = 0; i < seqArray.length; i++) {
			this.sequence.add(seqArray[i]);
		}
	}
	
	public void insertGapAt(int index) {
		this.endIndex++;
		this.length++;
		this.sequence.add(index, '-');
	}
		
}


