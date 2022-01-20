package in.trident.crdr.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="closebl")
public class CloseBal {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="CDate")
	private String date;
	
	@Column(name="debit_total")
	private double drTot;
	
	@Column(name="credit_total")
	private double crTot;
	
	@Column(name="close_bal")
	private double closeBal;
	
	@Column(name="userid", nullable = false)
	private Long userid;
	
	@Column(name="companyid", nullable = false)
	private Long companyid;

	public CloseBal() {
		
	}
	
	/**
	 * @param date
	 * @param drTot
	 * @param crTot
	 * @param closeBal
	 * @param userid
	 * @param companyid
	 */
	public CloseBal(String date, double drTot, double crTot, double closeBal, Long userid, Long companyid) {
		super();
		this.date = date;
		this.drTot = drTot;
		this.crTot = crTot;
		this.closeBal = closeBal;
		this.userid = userid;
		this.companyid = companyid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getDrTot() {
		return drTot;
	}

	public void setDrTot(double drTot) {
		this.drTot = drTot;
	}

	public double getCrTot() {
		return crTot;
	}

	public void setCrTot(double crTot) {
		this.crTot = crTot;
	}

	public double getCloseBal() {
		return closeBal;
	}

	public void setCloseBal(double closeBal) {
		this.closeBal = closeBal;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getComapnyid() {
		return companyid;
	}

	public void setComapnyid(Long comapnyid) {
		this.companyid = comapnyid;
	}

	@Override
	public String toString() {
		return "CloseBal [id=" + id + ", date=" + date + ", drTot=" + drTot + ", crTot=" + crTot + ", closeBal="
				+ closeBal + ", userid=" + userid + ", companyid=" + companyid + "]";
	}

	

}
