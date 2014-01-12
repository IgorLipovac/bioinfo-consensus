import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Consensus {
	
	public Consensus() {
		symbols = new ArrayList<Metasymbol>();
		consensusScore = 0.f;
	}
	
	public List<Metasymbol> symbols;
	public double consensusScore;
	
	public void printGappedConsensusToFile(String path) {
		
		try {
			FileWriter fr = new FileWriter(new File(path));
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < symbols.size(); i++) {
				if (i % 69 == 0) {
					builder.append('\n');
				}
				if (symbols.get(i).symbols.size() == 1) {
					builder.append(symbols.get(i).symbols.get(0));
				} else {
					builder.append('N');
				}
			}
			
			fr.write(builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void printUngappedConsensusToFile(String path) {
		
		try {
			@SuppressWarnings("resource")
			FileWriter fr = new FileWriter(new File(path));
			StringBuilder builder = new StringBuilder();
			long written = 0;
			for (int i = 0; i < symbols.size(); i++) {
				if (written % 69 == 0) {
					builder.append('\n');
				}
				char c = symbols.get(i).symbols.get(0);
				if (c == '-') {
					continue;
				} else {
					if (symbols.get(i).symbols.size() == 1) {
						builder.append(symbols.get(i).symbols.get(0));
					} else {
						builder.append('N');
					}
					written++;
				}
			}
			
			fr.write(builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
