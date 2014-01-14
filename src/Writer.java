import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Writer {

	public static void printGappedConsensusToFile(String path, Consensus consensus,boolean append) {
		
		try {
			FileWriter fr = new FileWriter(new File(path), append);
			fr.write(">"+consensus.getLayoutID());
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < consensus.getSymbols().size(); i++) {
				if (i % 60 == 0) {
					builder.append('\n');
				}
				if (consensus.getSymbols().get(i).symbols.size() > 1) {
					builder.append('n');
					
				} else {
					builder.append(consensus.getSymbols().get(i).symbols.get(0));
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
			fr.write(">"+consensus.getLayoutID()+"\n");
			fr2.write(">"+consensus.getLayoutID()+"\n");
			StringBuilder builder = new StringBuilder();
			StringBuilder builder2 = new StringBuilder();
			long written = 0;
			long writtenInto2 = 0;
			for (int i = 0; i < consensus.getSymbols().size(); i++) {
				
				ArrayList<Character> symbols = (ArrayList<Character>) consensus.getSymbols().get(i).symbols;
				if (symbols.contains('-') && symbols.size()==1) {
					continue;
				}
				if (symbols.size() > 1) {
					if (symbols.contains('-') && symbols.size() == 2) {
						int index = symbols.indexOf('-');
						if (index == 0) {
							builder.append(consensus.getSymbols().get(i).symbols.get(1));
							builder2.append(consensus.getSymbols().get(i).symbols.get(1));
							
						} else {
							builder.append(consensus.getSymbols().get(i).symbols.get(0));
							builder2.append(consensus.getSymbols().get(i).symbols.get(0));
							
						}
					} else {
						builder.append('n');
						builder2.append("[");
						writtenInto2++;
						if (writtenInto2 % 60 == 0 && writtenInto2!=0) {
							builder2.append('\n');
						}
						for (char c : consensus.getSymbols().get(i).symbols) {
							builder2.append(c);
							writtenInto2++;
							if (writtenInto2 % 60 == 0 && writtenInto2!=0) {
								builder2.append('\n');
							}
						}
						builder2.append("]");
					}
					
				} else {
					builder.append(consensus.getSymbols().get(i).symbols.get(0));
					builder2.append(consensus.getSymbols().get(i).symbols.get(0));
					
				}
				written++;
				if (written % 60 == 0 && written!=0) {
					builder.append('\n');
					
				}
				writtenInto2++;
				if (writtenInto2 % 60 == 0 && writtenInto2!=0) {
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
