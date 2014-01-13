import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Writer {

	public static void printGappedConsensusToFile(String path, Consensus consensus) {
		
		try {
			FileWriter fr = new FileWriter(new File(path), true);
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
	public static void printUngappedConsensusToFile(String path, Consensus consensus) {
		
		try {
			@SuppressWarnings("resource")
			FileWriter fr = new FileWriter(new File(path));
			StringBuilder builder = new StringBuilder();
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
						} else {
							builder.append(consensus.symbols.get(i).symbols.get(0));
						}
					} else {
						builder.append('n');
					}
					
				} else {
					builder.append(consensus.symbols.get(i).symbols.get(0));
				}
				written++;
				if (written % 60 == 0) {
					builder.append('\n');
				}
			}
			
			fr.write(builder.toString());
			fr.flush();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
