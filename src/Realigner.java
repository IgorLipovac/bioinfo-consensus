import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Realigner {

	
	public static Consensus getConsensus (Map<Integer, Layout> layoutMap) {
		int numOfCols = getNumberOfColumns(layoutMap);
		
		Consensus consensus = new Consensus();
		
		for (int col = 0; col < numOfCols; col++) {
			List<Layout> reads = new ArrayList<Layout>();
			for (Layout lay : layoutMap.values()) {
				if (lay.offset <= col && col < lay.offset + lay.length) {
					reads.add(lay);
				}
			}
			char[] column = getColumn(reads,col);
			
			Metasymbol consensusSymbol = getConsensusMetasymbol(column);
			consensus.symbols.add(consensusSymbol);
			consensus.consensusScore += getColumnScore(column, consensusSymbol);
			

		}
		
		return consensus;
	}
	
	private static Metasymbol getConsensusMetasymbol(char[] column) {
		Metasymbol sym = new Metasymbol();
		
		List<Character> listC = new ArrayList<Character>();
	    for (char c : column) {
	        listC.add(c);
	    }
	    List<Integer> freqs = new ArrayList<Integer>();
		freqs.add(Collections.frequency(listC, 'A'));
		freqs.add(Collections.frequency(listC, 'C'));
		freqs.add(Collections.frequency(listC, 'G'));
		freqs.add(Collections.frequency(listC, 'T'));
		freqs.add(Collections.frequency(listC, '-'));
		
		Integer max = Collections.max(freqs);
		
		if (freqs.get(0) == max) sym.symbols.add('A');
		if (freqs.get(1) == max) sym.symbols.add('C');
		if (freqs.get(2) == max) sym.symbols.add('G');
		if (freqs.get(3) == max) sym.symbols.add('T');
		if (freqs.get(4) == max) sym.symbols.add('-');
		
		
		return sym;
	}
	
	
	private static char[] getColumn (List<Layout> reads, int index) {
		char[] column = new char[reads.size()];
		for (int i = 0; i < reads.size(); i++) {
			Layout read = reads.get(i);
			char c = read.sequence[index - read.offset];		
			column[i] = c;
		}
		return column;
	}
	
	private static double getColumnScore (char[] column, Metasymbol sym) {
		double score = 0.f;
		for (char c : column) {
			if (!sym.symbols.contains(c)) {
				score+=1;
			}
		}
		return score;
	}
	
	
	
	private static int getNumberOfColumns(Map<Integer, Layout> layoutMap) {
		List<Integer> keys = new ArrayList<Integer>(layoutMap.keySet());
		Collections.sort(keys);
		for (int i : keys) {
			System.out.println(i);
		}
		int columnsNum = 0;
		for(int key : keys) {
			Layout current = layoutMap.get(key);
			int minusOffset = columnsNum;
			if (current.offset <= columnsNum) {
				minusOffset -= current.offset;
			} else {
				minusOffset += (current.offset - columnsNum);
			}
			int newLength = minusOffset + current.length;
			if (newLength > columnsNum) {
				columnsNum = newLength;
			}
		}
		return columnsNum;
	}
	
	
	private static void globalAlignment(String sequence, Map<Integer, Layout> layoutMap ) {
		
	}
	
	

	
	
}
