import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Realigner {

	
	public static Consensus getConsensus (Alignment layoutMap) {
		int numOfCols = getNumberOfColumns(layoutMap);
		
		Consensus consensus = new Consensus();
		
		for (int col = 0; col < numOfCols; col++) {	
			char[] column = getColumn(layoutMap,col);
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
	
	
	private static double getConsensusScoreWeighted (Read detachedSeq, Consensus consensus, Alignment subalignment) {
		double weightedScore = 0.5 * getConsensusScoreWithFunction1(detachedSeq, consensus) + 0.5 * getConsensusScoreWithFunction2(detachedSeq, subalignment);
		return weightedScore;
			
	}
	
	// get consensus score between subalignment and detached sequence
	private static double getConsensusScoreWithFunction1(Read sequence, Consensus consensus) {
		double score = consensus.consensusScore;
		// takes into account that S + c(endgap) scores 0;
		for (int col = sequence.offset; col < sequence.offset + sequence.length; col++) {
			if (col >= 0 && col < consensus.symbols.size()) {
				if (!consensus.symbols.get(col).symbols.contains(sequence.sequence[col])) {
					score+=1;
				}
			}
			
		}
		return score;
	}
	
	// gets consensus score with a function that takes weighted approach between detached sequence
	// and subalignment layout - NOT CONSENSUS!
	private static double getConsensusScoreWithFunction2(Read sequence, Alignment layoutMap) {
		double score = 0;
		// takes into account that S + c(endgap) scores 0;
		for (int col = sequence.offset; col < sequence.offset + sequence.length; col++) {
			char[] column = getColumn(layoutMap, col);
			double columnScore = 0;
			if (column.length > 0) {
				columnScore = getColumnScore(column, sequence.sequence[col]) / column.length;
			}
			score += columnScore;
		}
		return score;
	}
	
	
	private static char[] getColumn (Alignment layoutMap, int index) {
		List<Read> reads = new ArrayList<Read>();
		for (Read lay : layoutMap.values()) {
			if (lay.offset <= index && index < lay.offset + lay.length) {
				reads.add(lay);
			}
		}
		
		char[] column = new char[reads.size()];
		for (int i = 0; i < reads.size(); i++) {
			Read read = reads.get(i);
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
	
	private static double getColumnScore (char[] column, char sym) {
		double score = 0.f;
		for (char c : column) {
			if (sym != c) {
				score += 1;
			}
		}
		return score;
	}
	
	
	
	
	private static int getNumberOfColumns(Alignment layoutMap) {
		List<Integer> keys = new ArrayList<Integer>(layoutMap.keySet());
		Collections.sort(keys);
		for (int i : keys) {
			System.out.println(i);
		}
		int columnsNum = 0;
		for(int key : keys) {
			Read current = layoutMap.get(key);
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
	
	
	
	private static void globalAlignment(String sequence, Alignment layoutMap ) {
		
	}
	
	

	
	
}
