package in.trident.crdr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity Class of AccHead Table
 * 
 * @author Nandhakumar Subramanian
 *
 * @version 0.0.2
 */

@Entity
@Table(name = "acchead")
public class AccHead implements Comparable<AccHead> {

	public AccHead() {
		super();
	}

	/**
	 * @param accCode
	 * @param accName
	 * @param accType     Account type
	 * @param drAmt       Debit Amount
	 * @param crAmt
	 * @param sNo         serial number
	 * @param level1      level
	 * @param orderCode
	 * @param shortName
	 * @param budgetDrAmt
	 * @param budgetCrAmt
	 * @param userStatus
	 * @param stkNeeded
	 * @param stkOpen
	 * @param stkStatus
	 * @param userid
	 * @param companyid
	 */
	public AccHead(int accCode, String accName, String accType, double drAmt, double crAmt, int sNo, int level1,
			int orderCode, String shortName, double budgetDrAmt, double budgetCrAmt, boolean userStatus,
			boolean stkNeeded, double stkOpen, String stkStatus, Long userid, Long companyid) {
		super();
		this.accCode = accCode;
		this.accName = accName;
		this.accType = accType;
		this.drAmt = drAmt;
		this.crAmt = crAmt;
		this.sNo = sNo;
		this.level1 = level1;
		this.orderCode = orderCode;
		this.shortName = shortName;
		this.budgetDrAmt = budgetDrAmt;
		this.budgetCrAmt = budgetCrAmt;
		this.userStatus = userStatus;
		this.stkNeeded = stkNeeded;
		this.stkOpen = stkOpen;
		this.stkStatus = stkStatus;
		this.userid = userid;
		this.companyid = companyid;
	}

	//TODO BigDecimal for currency
	
	@Id
	@Column(name = "accheadid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accHeadId;

	@Column(name = "acc_code", nullable = false)
	private int accCode;

	@Column(name = "acc_name", nullable = false)
	private String accName;

	@Column(name = "acc_type", nullable = false)
	private String accType;

	@Column(name = "dr_amt", nullable = false)
	private double drAmt;

	@Column(name = "cr_amt", nullable = false)
	private double crAmt;

	@Column(name = "sno", nullable = false)
	private int sNo;

	@Column(name = "level_1", nullable = false)
	private int level1;

	@Column(name = "order_code", nullable = false)
	private int orderCode;

	@Column(name = "short_name")
	private String shortName;

	@Column(name = "budget_dramt", nullable = false)
	private double budgetDrAmt;

	@Column(name = "budget_cramt", nullable = false)
	private double budgetCrAmt;

	@Column(name = "user_status", nullable = false)
	private boolean userStatus;

	@Column(name = "stk_need", nullable = false)
	private boolean stkNeeded;

	@Column(name = "stk_open", nullable = false)
	private double stkOpen;

	@Column(name = "stk_status")
	private String stkStatus;

	@Column(name = "userid", nullable = false)
	private Long userid;

	@Column(name = "companyid", nullable = false)
	private Long companyid;

	public Long getAccHeadId() {
		return accHeadId;
	}

	public void setAccHeadId(Long accHeadId) {
		this.accHeadId = accHeadId;
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

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public double getDrAmt() {
		return drAmt;
	}

	public void setDrAmt(double drAmt) {
		this.drAmt = drAmt;
	}

	public double getCrAmt() {
		return crAmt;
	}

	public void setCrAmt(double crAmt) {
		this.crAmt = crAmt;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public double getBudgetDrAmt() {
		return budgetDrAmt;
	}

	public void setBudgetDrAmt(double budgetDrAmt) {
		this.budgetDrAmt = budgetDrAmt;
	}

	public double getBudgetCrAmt() {
		return budgetCrAmt;
	}

	public void setBudgetCrAmt(double budgetCrAmt) {
		this.budgetCrAmt = budgetCrAmt;
	}

	public boolean isUserStatus() {
		return userStatus;
	}

	public void setUserStatus(boolean userStatus) {
		this.userStatus = userStatus;
	}

	public boolean isStkNeeded() {
		return stkNeeded;
	}

	public void setStkNeeded(boolean stkNeeded) {
		this.stkNeeded = stkNeeded;
	}

	public double getStkOpen() {
		return stkOpen;
	}

	public void setStkOpen(double stkOpen) {
		this.stkOpen = stkOpen;
	}

	public String getStkStatus() {
		return stkStatus;
	}

	public void setStkStatus(String stkStatus) {
		this.stkStatus = stkStatus;
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
	public int compareTo(AccHead o) {
		return Integer.compare(this.sNo, o.sNo);
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accCode;
		result = prime * result + ((accHeadId == null) ? 0 : accHeadId.hashCode());
		result = prime * result + ((accName == null) ? 0 : accName.hashCode());
		result = prime * result + ((accType == null) ? 0 : accType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(budgetCrAmt);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(budgetDrAmt);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((companyid == null) ? 0 : companyid.hashCode());
		temp = Double.doubleToLongBits(crAmt);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(drAmt);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + level1;
		result = prime * result + orderCode;
		result = prime * result + sNo;
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + (stkNeeded ? 1231 : 1237);
		temp = Double.doubleToLongBits(stkOpen);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((stkStatus == null) ? 0 : stkStatus.hashCode());
		result = prime * result + (userStatus ? 1231 : 1237);
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccHead other = (AccHead) obj;
		if (accCode != other.accCode)
			return false;
		if (accHeadId == null) {
			if (other.accHeadId != null)
				return false;
		} else if (!accHeadId.equals(other.accHeadId))
			return false;
		if (accName == null) {
			if (other.accName != null)
				return false;
		} else if (!accName.equals(other.accName))
			return false;
		if (accType == null) {
			if (other.accType != null)
				return false;
		} else if (!accType.equals(other.accType))
			return false;
		if (Double.doubleToLongBits(budgetCrAmt) != Double.doubleToLongBits(other.budgetCrAmt))
			return false;
		if (Double.doubleToLongBits(budgetDrAmt) != Double.doubleToLongBits(other.budgetDrAmt))
			return false;
		if (companyid == null) {
			if (other.companyid != null)
				return false;
		} else if (!companyid.equals(other.companyid))
			return false;
		if (Double.doubleToLongBits(crAmt) != Double.doubleToLongBits(other.crAmt))
			return false;
		if (Double.doubleToLongBits(drAmt) != Double.doubleToLongBits(other.drAmt))
			return false;
		if (level1 != other.level1)
			return false;
		if (orderCode != other.orderCode)
			return false;
		if (sNo != other.sNo)
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (stkNeeded != other.stkNeeded)
			return false;
		if (Double.doubleToLongBits(stkOpen) != Double.doubleToLongBits(other.stkOpen))
			return false;
		if (stkStatus == null) {
			if (other.stkStatus != null)
				return false;
		} else if (!stkStatus.equals(other.stkStatus))
			return false;
		if (userStatus != other.userStatus)
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AccHead [accHeadId=" + accHeadId + ", accCode=" + accCode + ", accName=" + accName + ", accType="
				+ accType + ", drAmt=" + drAmt + ", crAmt=" + crAmt + ", sNo=" + sNo + ", level1=" + level1
				+ ", orderCode=" + orderCode + ", shortName=" + shortName + ", budgetDrAmt=" + budgetDrAmt
				+ ", budgetCrAmt=" + budgetCrAmt + ", userStatus=" + userStatus + ", stkNeeded=" + stkNeeded
				+ ", stkOpen=" + stkOpen + ", stkStatus=" + stkStatus + ", userid=" + userid + ", companyid="
				+ companyid + "]";
	}
}
