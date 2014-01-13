import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Writer {

	public static void printGappedConsensusToFile(String path, Consensus consensus,boolean append) {
		
		try {
			FileWriter fr = new FileWriter(new File(path), append);
			fr.write(">"+consensus.layoutID);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < consensus.symbols.size(); i++) {
				if (i % 60 == 0) {
					builder.append('\n');
				}
				if (consensus.symbols.get(i).symbols.size() > 1) {
					builder.append('n');
					
				} else {
					builder.append(consensus.symbols.get(i).symbols.get(0));
				}
			}
			
			fr.write(builder.toString());
			fr.flush();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void printUngappedConsensusToFile(Consensus consensus, boolean append) {
		
		try {
			@SuppressWarnings("resource")
			FileWriter fr = new FileWriter(new File("consensus.fasta"), append);
			FileWriter fr2 = new FileWriter(new File("consensus_profile.txt"), append);
			fr.write(">"+consensus.layoutID+"\n");
			fr2.write(">"+consensus.layoutID+"\n");
			StringBuilder builder = new StringBuilder();
			StringBuilder builder2 = new StringBuilder();
			long written = 0;
			for (int i = 0; i < consensus.symbols.size(); i++) {
				
				ArrayList<Character> symbols = (ArrayList<Character>) consensus.symbols.get(i).symbols;
				if (symbols.contains('-') && symbols.size()==1) {
					continue;
				}
				if (symbols.size() > 1) {
					if (symbols.contains('-') && symbols.size() == 2) {
						int index = symbols.indexOf('-');
						if (index == 0) {
							builder.append(consensus.symbols.get(i).symbols.get(1));
							builder2.append(consensus.symbols.get(i).symbols.get(1));
						} else {
							builder.append(consensus.symbols.get(i).symbols.get(0));
							builder2.append(consensus.symbols.get(i).symbols.get(0));
						}
					} else {
						builder.append('n');
						builder2.append("[");
						for (char c : consensus.symbols.get(i).symbols) {
							builder2.append(c);
						}
						builder2.append("]");
					}
					
				} else {
					builder.append(consensus.symbols.get(i).symbols.get(0));
					builder2.append(consensus.symbols.get(i).symbols.get(0));
				}
				written++;
				if (written % 60 == 0 && written!=0) {
					builder.append('\n');
					builder2.append('\n');
				}
				
			}
			fr2.write(builder2.toString());
			fr.write(builder.toString());
			fr.write("\n");
			fr.flush();
			fr.close();
			fr2.write("\n");
			fr2.flush();
			fr2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
