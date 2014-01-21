import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Consensus {
	private List<Metasymbol> symbols;
	private double consensusScore;
	private int layoutID;
	
	public Consensus() {
		this.setSymbols(new ArrayList<Metasymbol>());
		this.setConsensusScore(0.f);
	}
	public Consensus(int layoutID) {
		this.setLayoutID(layoutID);
		this.setSymbols(new ArrayList<Metasymbol>());
		this.setConsensusScore(0.f);
	}
	public List<Metasymbol> getSymbols() {
		return symbols;
	}
	public void setSymbols(List<Metasymbol> symbols) {
		this.symbols = symbols;
	}
	public double getConsensusScore() {
		return consensusScore;
	}
	public void setConsensusScore(double consensusScore) {
		this.consensusScore = consensusScore;
	}
	public int getLayoutID() {
		return layoutID;
	}
	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}
	
	public void addDashesInFront() {
		Metasymbol dash = new Metasymbol();
		dash.symbols.add('-');
		this.symbols.add(0, dash);
	}
	public void addDashToBack() {
		Metasymbol dash = new Metasymbol();
		dash.symbols.add('-');
		this.symbols.add(dash);
	}
}
