import java.util.ArrayList;



public class Read {
	
	public int readIndex;
	public int offset;
	public int startIndex;
	public int endIndex;
	public int length;
	public ArrayList<Character> sequence;
	
	
	public Read(int index, int startInd, int endInd, int offset2) {
		this.readIndex = index;
		this.offset = offset2;
		this.startIndex = startInd;
		this.endIndex = endInd;
		this.length = Math.abs(endInd - startInd);
		this.sequence = (ArrayList<Character>) Reader.GetReadFromFasta(index);
	}
	
	
	public void insertGapAt(int index) {
		this.endIndex++;
		this.length++;
		this.sequence.add(index, '-');
	}
		
}


