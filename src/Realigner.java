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
		//dashFunction(consensus);
		return consensus;
	}
	
	private static void scoreConsensus (Consensus consensus, Alignment layoutMap) {
		consensus.consensusScore = 0.f;
		int numOfCols = getNumberOfColumns(layoutMap);

		for (int col = 0; col < numOfCols; col++) {	
			char[] column = getColumn(layoutMap,col);
			consensus.consensusScore += getColumnScore(column, consensus.symbols.get(col));
		}
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
		double f1score = getConsensusScoreWithFunction1(detachedSeq, consensus);
		double f2score = getConsensusScoreWithFunction2(detachedSeq, subalignment);
		
		double weightedScore = 0.5 * f1score + 0.5 * f2score;
		return weightedScore;
			
	}
	
	// get consensus score between subalignment and detached sequence
	private static double getConsensusScoreWithFunction1(Read sequence, Consensus consensus) {
		double score = 0;
		// takes into account that S + c(endgap) scores 0;
		for (int col = sequence.offset; col < sequence.offset + sequence.length; col++) {
			if (col >= 0 && col < consensus.symbols.size()) {
				Metasymbol sym = consensus.symbols.get(col);
				char c = sequence.sequence.get(col-sequence.offset);
				if (!sym.symbols.contains(c)) {
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
	
	
	
	private static Read globalAlignment(Read seqA, Consensus seqB, double eps, Consensus newConsensus) {
		Read mSeqA = seqA;
        Consensus mSeqB = new Consensus();
        int[][] mD;
        int mScore;
        String mAlignmentSeqA = "";
        String mAlignmentSeqB = "";
        
        int e = (int) (eps/2);
        int start = mSeqA.offset - e;
        int end = e + mSeqA.offset + mSeqA.length;
        

        for (int i = start; i < end;i++){
        	mSeqB.symbols.add(seqB.symbols.get(i));
        }
                      
        mD = new int[mSeqA.length + 1][mSeqB.symbols.size() + 1];
        
        
        for (int i = 0; i <= mSeqA.length; i++) {
        	for (int j = 0; j <= mSeqB.symbols.size(); j++) {
        		if (i == 0) {
        			if (j <= 2*e) {
        				mD[i][j] = 0;//-Math.abs(j - e);
        			}
        			else {
        				mD[i][j] = Integer.MIN_VALUE;
        			}
        		} else if (i <= e) {
        			if (j <= e)  {
        				if (j >= i) {
        					mD[i][j] = j - i - e;
        				} else {
        					mD[i][j] = Integer.MIN_VALUE;
        				}
        			}
        		}

        	}
        }
        
        for (int i = 1; i <= mSeqA.length; i++) {
        	for (int j = 1; j <= mSeqB.symbols.size(); j++) {
        		if (j >= i && j <= i + 2*e) {
	        		int weight = -1;
	        		if (mSeqB.symbols.get(j-1).symbols.contains(mSeqA.sequence.get(i - 1))) {
	        			weight = 0;
	    			} else {
	    				weight = -1;
	    			}
	        		
	        		int scoreDiag = mD[i-1][j-1] + weight;
	        		int scoreLeft = mD[i][j-1];
	        		if (scoreLeft != Integer.MIN_VALUE) 
	        			scoreLeft = mD[i][j-1] - 1;
	        		int scoreUp = Integer.MIN_VALUE;
//	        		if (scoreUp != Integer.MIN_VALUE) 
//	        			scoreUp = mD[i-1][j] - 1;
	        		mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
        		} else {
        			mD[i][j] = Integer.MIN_VALUE;
        		}
        	}
        }

        
        int i = mSeqA.length;
        int j = mSeqB.symbols.size();
       	int maxVal = Integer.MIN_VALUE;
       	for (int index = mSeqB.symbols.size(); index > mSeqB.symbols.size() - 2*e; index --) {
    	   if (mD[i][index] > maxVal ) {
    		   maxVal = mD[i][index];
    		   j = index;
    	   }
       	}
        mScore = mD[i][j];
        while (i != 0 ) {     
        	int weight = -1;
        	if (mSeqB.symbols.get(j - 1).symbols.contains(mSeqA.sequence.get(i - 1))) {
        		weight = 0;
        	} else {
        		weight = -1;
        	}
            	
        	if (mD[i][j] == mD[i-1][j-1] + weight) {                      
        		mAlignmentSeqA += mSeqA.sequence.get(i-1);
        		Metasymbol sym =  mSeqB.symbols.get(j-1);
        		newConsensus.symbols.add(sym);
        		mAlignmentSeqB += sym.symbols.get(0);
//        		if (sym.symbols.size() > 1) { 
//        			mAlignmentSeqB +=')';
//        			}
//        		for (Character c : sym.symbols) {
//        			mAlignmentSeqB += c;
//        			}
//        		if (sym.symbols.size() > 1) { 
//        			mAlignmentSeqB +='(';
//        			}
        		i--;
        		j--;    
        		continue;
        	} else if (mD[i][j] == mD[i][j-1] - 1) {
    			mAlignmentSeqA += "-";
    			Metasymbol sym =  mSeqB.symbols.get(j-1);
        		newConsensus.symbols.add(sym);
        		mAlignmentSeqB += sym.symbols.get(0);
//        		if (sym.symbols.size() > 1) { 
//        			mAlignmentSeqB +=')';
//        			}
//        		for (Character c : sym.symbols) {
//        			mAlignmentSeqB += c;
//        			}
//        		if (sym.symbols.size() > 1) { 
//        			mAlignmentSeqB +='(';
//        			}
    			j--;
    			continue;
        	} else {
    			mAlignmentSeqA += mSeqA.sequence.get(i-1);
    			Metasymbol sym = new Metasymbol();
    			sym.symbols.add('-');
    			mAlignmentSeqB += '-';
    			newConsensus.symbols.add(sym);
    			i--;
                continue;
            }
        }
             
        mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
        mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
        
        System.out.println("Score: " + mScore);
        System.out.println("Sequence A: " + mAlignmentSeqA);
        System.out.println("Sequence B: " + mAlignmentSeqB);
        System.out.println(j);
        
        Read toBeReturned = new Read(seqA.readIndex, seqA.startIndex , mAlignmentSeqA.length(), seqA.offset + j - e, mAlignmentSeqA);
        return toBeReturned;
	}
	
	
	public static void reAlign(Alignment layoutMap, Integer index, double epsilonPrecision) {
		Consensus consensus = getConsensus(layoutMap);
		double initialScore = consensus.consensusScore;
		Read sequence = layoutMap.detachFromAlignmentOnIndex(index);
		consensus = getConsensus(layoutMap);
		Consensus newConsensus = new Consensus();
		double score = consensus.consensusScore;
		dashFunction(sequence);
		dashFunction(consensus);
		Read newRead =  globalAlignment(sequence, consensus,sequence.length * epsilonPrecision, newConsensus);
		
		double scoreF1 = getConsensusScoreWithFunction1(newRead, consensus);
		double scoreF2 = getConsensusScoreWithFunction2(newRead, layoutMap);
		
//		layoutMap.insertSequenceIntoAlignment(newRead);
//		newConsensus = getConsensus(layoutMap);
		//scoreConsensus(newConsensus, layoutMap);
		score += 0.5 *scoreF1 + 0.5*scoreF2;
		
		if (score >= initialScore) {
			System.out.println("Stop");
		}
		
	}

	
	
}
