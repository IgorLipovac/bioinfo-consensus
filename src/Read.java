

public class Read {
	
	public int readIndex;
	public int offset;
	public int startIndex;
	public int endIndex;
	public int length;
	public char sequence[];
	
	public int getReadIndex() {
		return readIndex;
	}

	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public char[] getSequence() {
		return sequence;
	}

	public void setSequence(char[] sequence) {
		this.sequence = sequence;
	}

	
	public Read(int index, int startInd, int endInd, int offset2) {
		this.readIndex = index;
		this.offset = offset2;
		this.startIndex = startInd;
		this.endIndex = endInd;
		this.length = Math.abs(endInd - startInd);
		this.sequence = Reader.GetReadFromFasta(index);
	}
}
