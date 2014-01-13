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
	public int layoutID;
	
	

	
}
