package in.trident.crdr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @version 0.0.1
 */

@Entity
@Table(name = "daybook")
public class Daybook implements Comparable<Daybook> {
	public Daybook() {

	}

	/**
	 * @param sNo
	 * @param date
	 * @param narration
	 * @param acccode
	 * @param drAmt
	 * @param crAmt
	 * @param sktValue
	 * @param userid
	 * @param companyid
	 */
	public Daybook(int sNo, String date, String narration, int acccode, Double drAmt, Double crAmt, int sktValue,
			Long userid, Long companyid) {
		super();
		this.sNo = sNo;
		this.date = date;
		this.narration = narration;
		this.acccode = acccode;
		this.drAmt = drAmt;
		this.crAmt = crAmt;
		this.sktValue = sktValue;
		this.userid = userid;
		this.companyid = companyid;
	}

	@Id
	@Column(name = "dbid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dbId;

	@Column(name = "Sno", nullable = false, length = 20)
	private int sNo;

	@Column(name = "TDate", nullable = false)
	private String date;

	@Column(name = "Narration", nullable = false)
	private String narration;

	@Column(name = "acccode", nullable = false)
	private int acccode;

	@Column(name = "dramt", nullable = false)
	private Double drAmt;

	@Column(name = "cramt", nullable = false)
	private Double crAmt;

	@Column(name = "stkvalue", nullable = false)
	private int sktValue;

	@Column(name = "userid", nullable = false)
	private Long userid;

	@Column(name = "companyid", nullable = false)
	private Long companyid;

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
		if (date.length() > 12) {
			date = date.substring(0, date.length() - 9);
		}
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

	public int getAcccode() {
		return acccode;
	}

	public void setAcccode(int acccode) {
		this.acccode = acccode;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public void setSktValue(int sktValue) {
		this.sktValue = sktValue;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

	@Override
	public String toString() {
		return "Daybook [dbId=" + dbId + ", sNo=" + sNo + ", date=" + date + ", narration=" + narration + ", acccode="
				+ acccode + ", drAmt=" + drAmt + ", crAmt=" + crAmt + ", sktValue=" + sktValue + ", userid=" + userid
				+ ", companyid=" + companyid + "]";
	}

	@Override
	public int compareTo(Daybook o) {
		return this.date.compareTo(o.date);
	}
}
