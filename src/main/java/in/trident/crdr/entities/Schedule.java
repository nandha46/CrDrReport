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
 * @since 31 Jul 2021
 * @version 0.0.6b
 *
 */
@Entity
@Table(name = "schedule")
public class Schedule implements Comparable<Schedule> {

	public Schedule() {
		
	}
	
	@Id
	@Column(name = "sno")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sNo;

	@Column(name = "acc_code")
	private int accCode;

	@Column(name = "acc_name")
	private String accName;
	
	@Column(name = "acc_type")
	private String accType;

	@Column(name = "dr_amt")
	private Double drAmt;
	
	@Column(name = "cr_amt")
	private Double crAmt;

	@Column(name = "level1")
	private int level1;

	@Column(name = "order_code")
	private int orderCode;

	@Column(name = "userid", nullable = false)
	private Long userid;

	@Column(name = "companyid", nullable = false)
	private Long companyid;

	
	
	/**
	 * @param accCode
	 * @param accName
	 * @param accType
	 * @param drAmt
	 * @param crAmt
	 * @param level1
	 * @param orderCode
	 * @param userid
	 * @param companyid
	 */
	public Schedule(int accCode, String accName, String accType, Double drAmt, Double crAmt, int level1, int orderCode,
			Long userid, Long companyid) {
		super();
		this.accCode = accCode;
		this.accName = accName;
		this.accType = accType;
		this.drAmt = drAmt;
		this.crAmt = crAmt;
		this.level1 = level1;
		this.orderCode = orderCode;
		this.userid = userid;
		this.companyid = companyid;
	}

	public Integer getsNo() {
		return sNo;
	}

	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}

	public int getAccCode() {
		return accCode;
	}

	public void setAccCode(int accCode) {
		this.accCode = accCode;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public Double getCrAmt() {
		return crAmt;
	}

	public void setCrAmt(Double crAmt) {
		this.crAmt = crAmt;
	}

	public Double getDrAmt() {
		return drAmt;
	}

	public void setDrAmt(Double drAmt) {
		this.drAmt = drAmt;
	}

	public int getLevel1() {
		return level1;
	}

	public void setLevel1(int level1) {
		this.level1 = level1;
	}

	public int getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(int orderCode) {
		this.orderCode = orderCode;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

	@Override
	public int compareTo(Schedule o) {
		return this.sNo.compareTo(o.sNo);
	}

	@Override
	public String toString() {
		return "Schedule [sNo=" + sNo + ", accCode=" + accCode + ", accName=" + accName + ", crAmt=" + crAmt
				+ ", drAmt=" + drAmt + ", level1=" + level1 + ", orderCode=" + orderCode + ", accType=" + accType
				+ ", userid=" + userid + ", companyid=" + companyid + "]";
	}

}
