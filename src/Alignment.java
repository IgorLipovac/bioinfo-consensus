
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class Alignment {

	private Map<Integer, Read> layoutMap;
	private int layoutID;
	
	public int getLayoutID() {
		return layoutID;
	}


	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}


	public Alignment(Map<Integer, Read> newLayoutMap) {
		this.layoutMap = newLayoutMap;
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
	
	
	public Read detachFromAlignmentOnIndex(Integer index) {
		Read detached = this.layoutMap.get(index);
		this.layoutMap.remove(index);
		resetOffsets();
		return detached;
	}
	
	public void insertSequenceIntoAlignment(Read sequence) {
		this.layoutMap.put(sequence.getId(), sequence);
		resetOffsets();
	}
	
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
