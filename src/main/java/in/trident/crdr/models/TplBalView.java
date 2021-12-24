package in.trident.crdr.models;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9
 * 
 * @since 13 Dec 2021
 *
 */
public class TplBalView {
	
	private String acc;
	private String amt;
	private int level;
	private boolean header;
	
	public TplBalView() {
		// Default constructor
	}
	
	public TplBalView(String acc, String amt, int level, boolean header) {
		super();
		this.acc = acc;
		this.amt = amt;
		this.level = level;
		this.header = header;
	}
	
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "TplBalView [acc=" + acc + ", amt=" + amt + ", level=" + level + ", header=" + header + "]";
	}
	
	
}
