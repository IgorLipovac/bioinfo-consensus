
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class Alignment {

	private Map<Integer, Read> layoutMap;
	
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
		return null;
	}
	
	public void insertSequenceIntoAlignment(Read sequence) {
	
	}
	
	
}
