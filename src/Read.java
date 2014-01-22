import java.util.ArrayList;
import java.util.Arrays;



public class Read {
	
	private int id;
	private int offset;
	private int startIndex;
	private int endIndex;
	private int length;
	private int layoutOffset;
	public ArrayList<Character> sequence;
	public ArrayList<Character> quality;
	public boolean usingQualities;
	
	/**
	 * Create a new read with his index, the start and end index of symbols, offset, sequence and quality
	 * @param index Index of the read.
	 * @param startInd Starting index of the sequence.
	 * @param endInd End index of the sequence.
	 * @param offset2 Offset of the read.
	 * @param seq The sequence of the read
	 * @param qua Quality of every symbol in the sequence
	 */
	public Read(int index, int startInd, int endInd, int offset2, ArrayList<Character> seq, ArrayList<Character> qua) {
		this.setId(index);
		this.setOffset(offset2);
		this.setStartIndex(startInd);
		this.setEndIndex(endInd);
		this.setLayoutOffset(offset2);
		this.setLength(Math.abs(endInd - startInd));
		this.sequence = seq;
		if (qua.size()  == seq.size()) {
			this.usingQualities = true;
		} else {
			this.usingQualities = false;
		}
		this.quality = qua;
	}
	
	/**
	 * Create a new read with his index, the start and end index of symbols, offset and sequence.
	 * @param index Index of the read.
	 * @param startInd Starting index of the sequence.
	 * @param endInd End index of the sequence.
	 * @param layoutOffset Offset of the read.
	 * @param seq The sequence of the read
	 */
	public Read(int index, int startInd, int endInd, int offset2, int layoutOffset, String seq) {
		this.setId(index);
		this.setOffset(offset2);
		this.setLayoutOffset(layoutOffset);
		this.setStartIndex(startInd);
		this.setEndIndex(endInd);
		this.setLength(Math.abs(endInd - startInd));
		char[] seqArray = seq.toCharArray();
		this.sequence = new ArrayList<Character>();
		for (int i = 0; i < seqArray.length; i++) {
			this.sequence.add(seqArray[i]);
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getLayoutOffset() {
		return layoutOffset;
	}

	public void setLayoutOffset(int layoutOffset) {
		this.layoutOffset = layoutOffset;
	}
	/**
	 * Insert a gap in the read.
	 * @param index Index of the position where the gap should be inserted.
	 */
	public void insertGapAt(int index) {
		this.setEndIndex(this.getEndIndex() + 1);
		this.setLength(this.getLength() + 1);
		this.sequence.add(index, '-');
	}
		
}


