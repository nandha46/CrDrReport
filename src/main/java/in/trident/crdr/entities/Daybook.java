package in.trident.crdr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="daybook")
public class Daybook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dbId;
	
	@Column(name = "Sno", nullable = false, length = 20)
	private int sNo;
	
	@Column(name = "TDate", nullable= false)
	private String date;
	// TODO date object or just String
	
	@Column(name="Narration", nullable = false)
	private String narratiom;
	
	@Column(name="AccCode", nullable = false)
	private int accCode;
	
	@Column(name="DrAmt", nullable = false)
	private Long drAmt;
	// TODO Credit n Debit does it need to store negative value/double value
	
	@Column(name="CrAmt", nullable = false)
	private Long crAmt;
	
	@Column(name="SktValue", nullable = false)
	private int sktValue;
	// TODO sktValue need to store negative value
	
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
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNarratiom() {
		return narratiom;
	}
	public void setNarratiom(String narratiom) {
		this.narratiom = narratiom;
	}
	public int getAccCode() {
		return accCode;
	}
	public void setAccCode(int accCode) {
		this.accCode = accCode;
	}
	public Long getDrAmt() {
		return drAmt;
	}
	public void setDrAmt(Long drAmt) {
		this.drAmt = drAmt;
	}
	public Long getCrAmt() {
		return crAmt;
	}
	public void setCrAmt(Long crAmt) {
		this.crAmt = crAmt;
	}
	public int getSktValue() {
		return sktValue;
	}
	public void setSktValue(int sktValue) {
		this.sktValue = sktValue;
	}
}
