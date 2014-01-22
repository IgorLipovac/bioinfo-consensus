
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class Alignment {

	private Map<Integer, Read> layoutMap;
	private int layoutID;
	public boolean hasQualities;
	
	public int getLayoutID() {
		return layoutID;
	}


	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}

	/**
	 * Creating a new alignment
	 * @param newLayoutMap Map containing all reads
	 */
	
	public Alignment(Map<Integer, Read> newLayoutMap) {
		this.layoutMap = newLayoutMap;
		Integer firstKey = (Integer) newLayoutMap.keySet().toArray()[0];
		
		if (newLayoutMap.get(firstKey).usingQualities) {
			this.hasQualities = true;
		} else {
			this.hasQualities = false;
		}
	}
	
	
	public Read get(Integer index) {
		return this.layoutMap.get(index);
	}
	
	public Set<Integer> keySet() {
		return this.layoutMap.keySet();
	}
	
	public Collection<Read> values() {
		return this.layoutMap.values();
	}
	
	/**
	 * Method for detaching a read from alignment
	 * @param index Index of the read to be detached.
	 * @return The detached read
	 */
	public Read detachFromAlignmentOnIndex(Integer index) {
		Read detached = this.layoutMap.get(index);
		this.layoutMap.remove(index);
		resetOffsets();
		return detached;
	}
	
	/**
	 * Puts a new read into the alignment
	 * @param sequence Sequence to be added.
	 */
	public void insertSequenceIntoAlignment(Read sequence) {
		this.layoutMap.put(sequence.getId(), sequence);
		resetOffsets();
	}
	
	/**
	 * Resets offsets in layout due to read movement
	 */
	
	private void resetOffsets () {
		int minOffset = 0;
		for (Read read : layoutMap.values()) {
			if (read.getOffset() < minOffset) {
				minOffset = read.getOffset();
			}
		}
		if (minOffset != 0) {
			for (Read read : layoutMap.values()) {
				read.setOffset(read.getOffset() - minOffset);
			}
		}
		
	}
}
