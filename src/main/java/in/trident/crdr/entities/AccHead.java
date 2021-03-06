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
 * @version 0.0.2
 */

@Entity
@Table(name="acchead")
public class AccHead implements Comparable<AccHead> {

	@Id
	@Column(name="accheadid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accHeadId;
	
	@Column(name="acc_code", nullable= false)
	private int accCode;
	
	@Column(name="acc_name", nullable= false)
	private String accName;
	
	@Column(name="acc_type", nullable= false)
	private String accType;
	
	@Column(name="dr_amt", nullable= false)
	private double drAmt;
	
	@Column(name="cr_amt", nullable= false)
	private double crAmt;
	
	@Column(name="sno", nullable= false)
	private int sNo;
	
	@Column(name="level_1", nullable= false)
	private int level1;
	
	@Column(name="order_code", nullable= false)
	private int orderCode;
	
	@Column(name="short_name")
	private String shortName;
	
	@Column(name="budget_dramt", nullable= false)
	private double budgetDrAmt;
	
	@Column(name="budget_cramt", nullable= false)
	private double budgetCrAmt;
	
	@Column(name="user_status", nullable= false)
	private boolean userStatus;
	
	@Column(name="stk_need", nullable= false)
	private boolean stkNeeded;
	
	@Column(name="stk_open", nullable= false)
	private double stkOpen;
	
	@Column(name="stk_status")
	private String stkStatus;
	
	@Override
	public int compareTo(AccHead o) {
		return Integer.compare(this.sNo, o.sNo);
	}

	@Override
	public String toString() {
		return "AccHead [accHeadId=" + accHeadId + ", accCode=" + accCode + ", accName=" + accName + ", accType="
				+ accType + ", drAmt=" + drAmt + ", crAmt=" + crAmt + ", sNo=" + sNo + ", level1=" + level1
				+ ", orderCode=" + orderCode + ", shortName=" + shortName + ", budgetDrAmt=" + budgetDrAmt
				+ ", budgetCrAmt=" + budgetCrAmt + ", userStatus=" + userStatus + ", stkNeeded=" + stkNeeded
				+ ", stkOpen=" + stkOpen + ", stkStatus=" + stkStatus + "]";
	}

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

}
