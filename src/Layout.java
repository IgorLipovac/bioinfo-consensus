

public class Layout {
	
	public int readIndex;
	public int offset;
	public int startIndex;
	public int endIndex;
	public int length;
	public char sequence[];
	
	public Layout(int index, int startInd, int endInd, int offset2) {
		this.readIndex = index;
		this.offset = offset2;
		this.startIndex = startInd;
		this.endIndex = endInd;
		this.length = Math.abs(endInd - startInd);
		this.sequence = Reader.GetReadFromFasta(index);
	}
	
}
