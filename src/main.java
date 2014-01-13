
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class main {

	public static void main(String[] args) {
		
		FileReader frFile;
		try {
			frFile = new FileReader(args[0]);
			frFile.close();
		} catch (IOException e) {
			System.out.println("Wrong input arguments! First argument is FASTA file containing the reads.");
			return;
		}
		
		try {
			frFile = new FileReader(args[1]);
			frFile.close();
		} catch (IOException e) {
			System.out.println("Wrong input arguments! Second argument is .afg file containing the layout information.");
			return;
		}
		
		
		Reader.setReadsFilePath(args[0]);
		List<Alignment> test = Reader.GetLayout( args[1]);
		
		
		double epsilon = 0.1;
		int iterationNumber = 10;
	    

	    for (int i = 2;  i < args.length; i++) {
	        switch (args[i].charAt(0)) {
		        case '-':
		            if (args[i].length() < 3)
		                throw new IllegalArgumentException("Not a valid argument: "+args[i]);
		            if (args[i].charAt(1) == 'e') {
		            	String eps = args[i].substring(2);
		            	try {
		            	epsilon = Double.parseDouble(eps);
		            	}catch(NumberFormatException e) {
		            		System.out.println("eps argument not valid! Example: -e0.1");
		            	}
		            }
		            if (args[i].charAt(1) == 'i') {
		            	String it = args[i].substring(2);
		            	try {
		            	iterationNumber = Integer.parseInt(it);
		            	}catch(NumberFormatException e) {
		            		System.out.println("eps argument not valid! Example: -i10");
		            	}
		            }
		            break;
		            
		        default:
		            break;
	        }
	    }
	
		//Realigner.getConsensus(test);
		for (int contig = 0; contig < test.size(); contig++) {
			System.out.println("<===============================================================>");
			System.out.println("Realigning contig/layout with ID:" + contig);
			Consensus cons = Realigner.reAlign(test.get(contig), epsilon, iterationNumber);
			cons.layoutID = contig;
			if (contig == 0) {
				Writer.printUngappedConsensusToFile(cons,false);
			}
			else {
				Writer.printUngappedConsensusToFile(cons,true);
			}
		}
		System.out.println("<===============================================================>");
		System.out.println("Results written in consensus.fasta and consensus_profile.txt");
	}

}
