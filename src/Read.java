public class Read {
	
	public char[] sequence;
	public int startIndex;
	public int endIndex;
	public int length;
	public int readNumber;
	
	

	public Read(int index, int length2, int startIndex2, int endIndex2, String sequence2) {
		this.readNumber = index;
		this.length = length2;
		this.startIndex = startIndex2;
		this.endIndex = endIndex2;
		this.sequence = sequence2.toCharArray();	
	}
	
	

}
