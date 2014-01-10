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
		dashFunction(consensus);
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
		
//		if (sym.symbols.size() == 0) { // in case our consensus has been divided in two parts after detaching some sequence from alignment
//			sym.symbols.add('-');
//		}
		
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
				if (!consensus.symbols.get(col).symbols.contains(sequence.sequence.get(col-sequence.offset))) {
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
				columnScore = getColumnScore(column, sequence.sequence.get(col - sequence.offset)) / column.length;
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
			char c = read.sequence.get(index - read.offset);		
			column[i] = c;
		}
		return column;
	}
	
	// compares column and metasymbol
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
	

	
	// removes dashes if needed
	private static void dashFunction(Consensus consensus) {
		List <Integer> indexesToRemove = new ArrayList<Integer>();
		for (int col = 0; col < consensus.symbols.size(); col++) {
			Metasymbol symbol = consensus.symbols.get(col);
			if (symbol.symbols.contains('-') && symbol.symbols.size() == 1) {
				indexesToRemove.add(col);
			}
		}
		
		for (int i = 0; i < indexesToRemove.size(); i++) {
			consensus.symbols.remove(indexesToRemove.get(i));
		}
	}
	
	
	private static void dashFunction(Read read) {
		List <Integer> indexesToRemove = new ArrayList<Integer>();
		for (int col = 0; col < read.sequence.size(); col++) {
			char symbol = read.sequence.get(col);
			if (symbol == '-') {
				read.length--;
				read.endIndex--;
				indexesToRemove.add(col);
			}
		}
		
		for (int i = 0; i < indexesToRemove.size(); i++) {
			read.sequence.remove(indexesToRemove.get(i));
		}
	}
	
	
	
	private static void globalAlignment(Read seqA, Consensus seqB ) {
		Read mSeqA;
        Consensus mSeqB;
        int[][] mD;
        int mScore;
        String mAlignmentSeqA = "";
        String mAlignmentSeqB = "";
            mSeqA = seqA;
            mSeqB = seqB;
            
            
            mD = new int[mSeqA.length + 1][mSeqB.symbols.size() + 1];
            for (int i = 0; i <= mSeqA.length; i++) {
                    for (int j = 0; j <= mSeqB.symbols.size(); j++) {
                            if (i == 0) {
                                    mD[i][j] = -j;
                            } else if (j == 0) {
                                    mD[i][j] = -i;
                            } else {
                                    mD[i][j] = 0;
                            }
                    }
            }

            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.symbols.size(); j++) {
                    	int weight = -1;
                        if (mSeqB.symbols.get(j-1).symbols.contains(mSeqA.sequence.get(i - 1))) {
    	                        weight = 1;
    	                } else {
    	                        weight = -1;
    	                }
                            int scoreDiag = mD[i-1][j-1] + weight;
                            int scoreLeft = mD[i][j-1] - 1;
                            int scoreUp = mD[i-1][j] - 1;
                            mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
                    }
            }

            int i = mSeqA.length;
            int j = mSeqB.symbols.size();
            mScore = mD[i][j];
            while (i > 0 && j > 0) {     
            	int weight = -1;
            	 if (mSeqB.symbols.get(j-1).symbols.contains(mSeqA.sequence.get(i - 1))) {
                        weight = 1;
                } else {
                        weight = -1;
                }
            	
                    if (mD[i][j] == mD[i-1][j-1] + weight) {                          
                            mAlignmentSeqA += mSeqA.sequence.get(i-1);
                            ArrayList <Character> syms = (ArrayList<Character>) mSeqB.symbols.get(j-1).symbols;
                            if (syms.size() > 1) { 
                                mAlignmentSeqB +='|';
                                }
                            for (Character c : syms) {
                            	mAlignmentSeqB += mSeqB.symbols.get(j-1).symbols.get(0);
                            }
                            i--;
                            j--;    
                            if (syms.size() > 1) { 
                                mAlignmentSeqB +='|';
                                }
                            continue;
                    } else if (mD[i][j] == mD[i][j-1] - 1) {
                            mAlignmentSeqA += "-";
                            ArrayList <Character> syms = (ArrayList<Character>) mSeqB.symbols.get(j-1).symbols;
                            if (syms.size() > 1) { 
                            mAlignmentSeqB +='|';
                            }
                            for (Character c : syms) {
                            	mAlignmentSeqB += mSeqB.symbols.get(j-1).symbols.get(0);
                            }
                            j--;
                            if (syms.size() > 1) { 
                                mAlignmentSeqB +='|';
                                }
                            continue;
                    } else {
                            mAlignmentSeqA += mSeqA.sequence.get(i-1);
                            mAlignmentSeqB += "-";
                            i--;
                            continue;
                    }
            }
            mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
            mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();

            System.out.println("Score: " + mScore);
            System.out.println("Sequence A: " + mAlignmentSeqA);
            System.out.println("Sequence B: " + mAlignmentSeqB);
            System.out.println();
	}
	
	
	public static void reAlign(Alignment layoutMap, Integer index, double epsilonPrecision) {
		Read sequence = layoutMap.detachFromAlignmentOnIndex(index);
		Consensus consensus = getConsensus(layoutMap);
		Consensus subConsensus = new Consensus();
		long length = Math.round(epsilonPrecision*sequence.length*2 + sequence.length);
		for (int pos = sequence.offset; pos < sequence.offset+length; pos++) {
			subConsensus.symbols.add(consensus.symbols.get(pos));
		}
		@SuppressWarnings("unused")
		double initialScore = getConsensusScoreWeighted(sequence, subConsensus, layoutMap);
		
		globalAlignment(sequence, subConsensus);
	}

	
	
}
