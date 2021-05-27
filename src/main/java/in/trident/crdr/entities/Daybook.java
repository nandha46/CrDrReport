package in.trident.crdr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="daybook")
public class Daybook implements Comparable<Daybook>{
	@Id
	@Column(name="dbid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dbId;
	
	@Column(name = "Sno", nullable = false, length = 20)
	private int sNo;
	
	@Column(name = "TDate", nullable= false)
	private String date;
	//TODO @Temporal Type date 
	
	@Column(name="Narration", nullable = false)
	private String narration;
	
	@Column(name="acccode", nullable = false)
	private int acccode;
	
	@Column(name="dramt", nullable = false)
	private Double drAmt;
	
	@Column(name="cramt", nullable = false)
	private Double crAmt;
	
	@Column(name="stkvalue", nullable = false)
	private int sktValue;
	
	public Long getDbId() {
		return dbId;
	}
	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public String getDate() {
	/*	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		Date d = sdf.parse(date);
		sdf.applyPattern("dd-MM-yyyy");
		date = sdf.format(d);  */
		date = date.substring(0,date.length()-9);
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narratiom) {
		this.narration = narratiom;
	}
	public int getAccCode() {
		return acccode;
	}
	public void setAccCode(int accCode) {
		this.acccode = accCode;
	}
	public Double getDrAmt() {
		return drAmt;
	}
	public void setDrAmt(Double drAmt) {
		this.drAmt = drAmt;
	}
	public Double getCrAmt() {
		return crAmt;
	}
	public void setCrAmt(Double crAmt) {
		this.crAmt = crAmt;
	}
	public int getSktValue() {
		return sktValue;
	}
	@Override
	public String toString() {
		return "Daybook [dbId=" + dbId + ", sNo=" + sNo + ", date=" + date + ", narration=" + narration + ", acccode="
				+ acccode + ", drAmt=" + drAmt + ", crAmt=" + crAmt + ", sktValue=" + sktValue + "]";
	}
	public void setSktValue(int sktValue) {
		this.sktValue = sktValue;
	}
	@Override
	public int compareTo(Daybook o) {
		return this.date.compareTo(o.date);
	}
}
