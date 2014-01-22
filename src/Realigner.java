import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Realigner {

	/**
	 * Creates consensus and scores it using scoring functions
	 * @param layoutMap map of reads 
	 * @return Consensus object
	 */
	public static Consensus getConsensus (Alignment layoutMap) {
		int numOfCols = getNumberOfColumns(layoutMap);
		Consensus consensus = new Consensus(layoutMap.getLayoutID());
		double f1score = 0.0;
		double f2score = 0.0;
		for (int col = 0; col < numOfCols; col++) {	
			char[] column = getColumn(layoutMap,col);
			Metasymbol consensusSymbol;
			if (layoutMap.hasQualities) {
				char[] qualityColumn = getQualityColumn(layoutMap, col);
				consensusSymbol = getConsensusMetasymbolWithQuality(column, qualityColumn);
			} else {
				consensusSymbol = getConsensusMetasymbol(column);
			}
			consensus.getSymbols().add(consensusSymbol);
			f1score += getColumnScore(column, consensusSymbol);
			if (column.length > 0) {
				f2score += getColumnScore(column, consensusSymbol) / column.length;
			}
		}
		consensus.setConsensusScore(0.5 * f1score + 0.5 * f2score);
		return consensus;
	}
	/**
	 *  pairwise consensus score function (not used)
	 */
	private static void scoreConsensus (Consensus consensus, Alignment layoutMap) {
		consensus.setConsensusScore(0.f);
		int numOfCols = getNumberOfColumns(layoutMap);

		for (int col = 0; col < numOfCols; col++) {	
			char[] column = getColumn(layoutMap,col);
			consensus.setConsensusScore(consensus.getConsensusScore() + getColumnScore(column, consensus.getSymbols().get(col)));
		}
	}
	
	/**
	 *  Method that computes consensus bases from column
	 */
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
	
	/**
	 *  Method that computes consensus bases from column using qualities of bases
	 */
	private static Metasymbol getConsensusMetasymbolWithQuality(char[] column, char[] qualityColumn) {
		Metasymbol sym = new Metasymbol();
		
	    long[] freqs = new long[5];
	    long[] quals = new long[5];
	    Arrays.fill(freqs, 0);
	    Arrays.fill(quals, 0);
	    for (int i = 0; i < column.length; i++) {
	    	if (column[i] == 'A') { 
	    		freqs[0]++;
	    		quals[0] += qualityColumn[i];
	    	}
	    	
	    	if (column[i]  == 'C') { 
	    		freqs[1]++;
	    		quals[1] += qualityColumn[i];
	    	}
	    	if (column[i]  == 'G') { 
	    		freqs[2]++;
	    		quals[2] += qualityColumn[i];
	    	}
	    	if (column[i]  == 'T') { 
	    		freqs[3]++;
	    		quals[3] += qualityColumn[i];
	    	}
	    	if (column[i]  == '-') { 
	    		freqs[4]++;
	    		quals[4] += qualityColumn[i];
	    	}
	    	
	    }
	    

		long max = freqs[0];
		long maxQuality = 0;
		for (int counter = 1; counter < freqs.length; counter++)
		{
		     if (freqs[counter] > max)
		     {
		      max = freqs[counter];
		     }
		}
		
		if (freqs[0] == max) { 
			sym.symbols.add('A');
			if (quals[0] > maxQuality) {
				maxQuality = quals[0];
			}
		}
		if (freqs[1] == max) { 
			sym.symbols.add('C');
			if (quals[1] > maxQuality) {
				maxQuality = quals[1];
			}
		}
		if (freqs[2] == max) {
			sym.symbols.add('G');
			if (quals[2] > maxQuality) {
				maxQuality = quals[2];
			}
		}
		if (freqs[3] == max) { 
			sym.symbols.add('T');
			if (quals[3] > maxQuality) {
				maxQuality = quals[3];
			}
		}
		if (freqs[4] == max) {
			sym.symbols.add('-');
			if (quals[4] > maxQuality) {
				maxQuality = quals[4];
			}
		}
		
		if (sym.symbols.size()>1) {
			List<Character> toRemove = new ArrayList<Character>();
			for (int counter = 0; counter < sym.symbols.size(); counter++) {
				char c = sym.symbols.get(counter);
				if (c=='A') {
					if (quals[0] < maxQuality) {
						toRemove.add(c);
					}
				}
				if (c=='C') {
					if (quals[1] < maxQuality) {
						toRemove.add(c);
					}
				}
				if (c=='G') {
					if (quals[2] < maxQuality) {
						toRemove.add(c);
					}
				}
				if (c=='T') {
					if (quals[3] < maxQuality) {
						toRemove.add(c);
					}
				}
				if (c=='-') {
					if (quals[4] < maxQuality) {
						toRemove.add(c);
					}
				}
			}
			sym.symbols.removeAll(toRemove);
		}
		if (sym.symbols.size() == 0) {
			System.out.println();
		}
		return sym;
	}
	
	/**
	 *  pairwise consensus score - weighted combination of two scoring functions
	 */
	private static double getConsensusScoreWeighted (Read detachedSeq, Consensus consensus, Alignment subalignment) {
		double f1score = getConsensusScoreWithFunction1(detachedSeq, consensus);
		double f2score = getConsensusScoreWithFunction2(detachedSeq, subalignment);
		double weightedScore = 0.5 * f1score + 0.5 * f2score;
		return weightedScore;
	}
	
	/**
	 *  pairwise consensus score - weighted combination of two scoring functions
	 */
	private static double getConsensusScoreWithFunction1(Read sequence, Consensus consensus) {
		double score = 0;
		// takes into account that S + c(endgap) scores 0;
		for (int col = sequence.getOffset(); col < sequence.getOffset() + sequence.getLength(); col++) {
			if (col >= 0 && col < consensus.getSymbols().size()) {
				Metasymbol sym = consensus.getSymbols().get(col);
				char c = sequence.sequence.get(col-sequence.getOffset());
				if (!sym.symbols.contains(c)) {
					score+=1;
				}
			}
			
		}
		return score;
	}
	
	/**
	 *  pairwise consensus score - second function, returns double
	 */
	private static double getConsensusScoreWithFunction2(Read sequence, Alignment layoutMap) {
		double score = 0;
		// takes into account that S + c(endgap) scores 0;
		for (int col = sequence.getOffset(); col < sequence.getOffset() + sequence.getLength(); col++) {
			char[] column = getColumn(layoutMap, col);
			char c = sequence.sequence.get(col - sequence.getOffset());
			double columnScore = 0;
			if (column.length > 0) {
				columnScore = getColumnScore(column, c) / column.length;
			}
			score += columnScore;
		}
		return score;
	}
	
	
	private static char[] getColumn (Alignment layoutMap, int index) {
		List<Read> reads = new ArrayList<Read>();
		for (Read lay : layoutMap.values()) {
			if (lay.getOffset() <= index && index < lay.getOffset() + lay.getLength()) {
				reads.add(lay);
			}
		}
		
		char[] column = new char[reads.size()];
		for (int i = 0; i < reads.size(); i++) {
			Read read = reads.get(i);
			char c = read.sequence.get(index - read.getOffset());		
			column[i] = c;
		}
		return column;
	}
	
	private static char[] getQualityColumn (Alignment layoutMap, int index) {
		List<Read> reads = new ArrayList<Read>();
		for (Read lay : layoutMap.values()) {
			if (lay.getOffset() <= index && index < lay.getOffset() + lay.getLength()) {
				reads.add(lay);
			}
		}
		
		char[] column = new char[reads.size()];
		for (int i = 0; i < reads.size(); i++) {
			Read read = reads.get(i);
			char c = read.quality.get(index - read.getOffset());		
			
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
			if (current.getOffset() <= columnsNum) {
				minusOffset = current.getOffset();
			} else {
				minusOffset += (current.getOffset() - columnsNum);
			}
			int newLength = minusOffset + current.getLength();
			if (newLength > columnsNum) {
				columnsNum = newLength;
			}
		}
		return columnsNum;
	}
	

	
	/**
	 * Removes dashes from consensus
	 */
	private static void dashFunction(Consensus consensus) {
		List <Integer> indexesToRemove = new ArrayList<Integer>();
		for (int col = 0; col < consensus.getSymbols().size(); col++) {
			Metasymbol symbol = consensus.getSymbols().get(col);
			if (symbol.symbols.contains('-') && symbol.symbols.size() == 1) {
				indexesToRemove.add(col);
			}
		}
		
		for (int i = 0; i < indexesToRemove.size(); i++) {
			consensus.getSymbols().remove(indexesToRemove.get(i));
		}
	}
	
	/**
	 *  Removes dashes from read
	 */
	private static void dashFunction(Read read) {
		List <Integer> indexesToRemove = new ArrayList<Integer>();
		for (int col = 0; col < read.sequence.size(); col++) {
			char symbol = read.sequence.get(col);
			if (symbol == '-') {
				read.setLength(read.getLength() - 1);
				read.setEndIndex(read.getEndIndex() - 1);
				indexesToRemove.add(col);
			}
		}
		
		for (int i = 0; i < indexesToRemove.size(); i++) {
			read.sequence.remove(indexesToRemove.get(i));
		}
	}
	
	
	/**
	 *  Alignment function  Needleman Wunsch algorithm
	 *  @param seqA  detached sequence
	 *  @param seqB  consensus sequence
	 *  @param eps  eps
	 */
	private static double getAlignment(Read seqA, Consensus seqB, double eps) {
	        Consensus mSeqB = new Consensus();
	        int[][] mD;
	        int mScore;
	        String mAlignmentSeqA = "";
	
	        int e = (int) (eps/2);
	        int start = seqA.getLayoutOffset() - e;
	        
	        int end = e + seqA.getLayoutOffset() + seqA.getLength();
	
	        for (int i = start; i < end;i++){
	        	if (i < 0) {
	        		mSeqB.addDashInFront();
	        	} else if (i >= seqB.getSymbols().size()) {
	        		mSeqB.addDashToBack();
	        	} else {
	        		mSeqB.getSymbols().add(seqB.getSymbols().get(i));
	        	}
	        }
	                      
	        mD = new int[seqA.getLength() + 1][mSeqB.getSymbols().size() + 1];
	        
	        
	        for (int i = 0; i <= seqA.getLength(); i++) {
	        	for (int j = 0; j <= mSeqB.getSymbols().size(); j++) {
	        		if (i == 0) {
	        			if (j <= 2*e) {
	        				mD[i][j] = -Math.abs(j - e);
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
	        
	        for (int i = 1; i <= seqA.getLength(); i++) {
	        	for (int j = 1; j <= mSeqB.getSymbols().size(); j++) {
	        		if (j >= i && j <= i + 2*e) {
		        		int weight = -1;
		        		Metasymbol sym = mSeqB.getSymbols().get(j-1);
		        		if (sym.symbols.contains(seqA.sequence.get(i - 1)) || (sym.symbols.contains('-') && sym.symbols.size()==1)) {
		        			weight = 0;
		    			} else {
		    				weight = -1;
		    			}
		        		
		        		int scoreDiag = mD[i-1][j-1] + weight;
		        		int scoreLeft = mD[i][j-1];
		        		if (scoreLeft != Integer.MIN_VALUE) 
		        			scoreLeft = mD[i][j-1] - 1;
//		        		int scoreUp =mD[i-1][j];
//		        		if (scoreUp != Integer.MIN_VALUE) 
//		        			scoreUp = mD[i-1][j] -1;
		        		mD[i][j] = Math.max(scoreDiag, scoreLeft);//, scoreUp);
	        		} else {
	        			mD[i][j] = Integer.MIN_VALUE;
	        		}
	        	}
	        }
	
	        
	        int i = seqA.getLength();
	        int j = mSeqB.getSymbols().size();
	       	int maxVal = Integer.MIN_VALUE;
	       	int size = mSeqB.getSymbols().size();
	       	
	       	for (int index = size; (index > size - 2*e) && (index > 1) ; index --) {
	    	   if (mD[i][index] > maxVal ) {
	    		   maxVal = mD[i][index];
	    		   j = index;
	    	   }
	       	}
	        mScore = mD[i][j];
	        if (mScore == Integer.MIN_VALUE){
	        	mScore = 0;
	        }
	        while (i > 0 && j > 0) {     
	        	int weight = -1;
	        	Metasymbol sym = mSeqB.getSymbols().get(j-1);
        		if (sym.symbols.contains(seqA.sequence.get(i - 1)) || (sym.symbols.contains('-') && sym.symbols.size()==1)) {
	        		weight = 0;
	        	} else {
	        		weight = -1;
	        	}
	        	if (mD[i][j] == mD[i-1][j-1] + weight) {                      
	        		mAlignmentSeqA += seqA.sequence.get(i-1);
	        		i--;
	        		j--;    
	        		continue;
	        	} else if (mD[i][j] == mD[i][j-1] - 1) {
	    			mAlignmentSeqA += "-";
	    			j--;
	    			continue;
	        	} else {
	        		mAlignmentSeqA += seqA.sequence.get(i-1);
	    			i--;
	                continue;
	            }
	        }           
	        mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();     
	        seqA.setLength(mAlignmentSeqA.length());
	        seqA.setOffset(seqA.getOffset() + j - e);
			char[] seqArray =  mAlignmentSeqA.toCharArray();
			seqA.sequence = new ArrayList<Character>();
			for (int k = 0; k < seqArray.length; k++) {
				seqA.sequence.add(seqArray[k]);
				if (seqArray[k] == '-') {
					seqA.quality.add('!'); // ADD Lowest quality score!
				}
			}
	  
	        return Math.abs(mScore);
	}
	
	/**
	 *  ReAligner
	 *  @param  layoutMap map of reads
	 *  @param  epsilonPrecision - predicted error of read layout, in percentage (0.0 to 1.0)
	 *  @param  numOfIterations max number of iterations
	 */
	
	public static Consensus reAlign(Alignment layoutMap, double epsilonPrecision, int numOfIterations) {
		Consensus consensus = getConsensus(layoutMap);
		double initialScore = consensus.getConsensusScore();
		boolean shouldContinue = true;
		int iteration = 1;
		int length = layoutMap.keySet().size();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		for (Integer i : layoutMap.keySet()) {
			keys.add(i);
		}
		double minimalScore = initialScore;
		Consensus bestConsensus = consensus;
		
		while(shouldContinue) {
			System.out.println("Iterating...");

			for (int k = 0; k < length; k++) {
				Read sequence = layoutMap.detachFromAlignmentOnIndex(keys.get(k));
				dashFunction(sequence);
				dashFunction(consensus);
				consensus = getConsensus(layoutMap);
				//double deltaConsensusScore =  
				getAlignment(sequence, consensus, sequence.getLength()* epsilonPrecision);

				//score = consensus.consensusScore + deltaConsensusScore + 0.5*getConsensusScoreWithFunction2(sequence, layoutMap);
				
				layoutMap.insertSequenceIntoAlignment(sequence);
				consensus = getConsensus(layoutMap);	
				if (consensus.getConsensusScore() < minimalScore) {
					bestConsensus = consensus;	
					minimalScore = consensus.getConsensusScore();
				}
				//System.out.println(k);
				//System.out.println(bestConsensus.consensusScore);
			}

			if (bestConsensus.getConsensusScore() >= initialScore || iteration == numOfIterations) {
				shouldContinue = false;				
			}
			
			System.out.println("After "+iteration+" iterations score is: "+ bestConsensus.getConsensusScore() +"   previous score : "+initialScore );
			initialScore = bestConsensus.getConsensusScore();
			iteration++;
		}
		
		return bestConsensus;
		
	}

	
	
}
