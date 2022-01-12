package in.trident.crdr.entities;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author nandhakumar Subramanian
 * 
 * @since 24/11/2021
 * 
 * @version 0.0.9
 * 
 * 
 */
@Entity
@Table(name = "company")
public class Company implements Comparable<Company> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long companyid;

	@Column(name = "userid")
	private Long userid;

	@Column(name = "CompName", length = 50)
	private String compName;
	@Column(name = "CompYear", length = 7)
	private String compYear;
	@Column(name = "ShortName", length = 3)
	private String shortName;
	@Column(name = "ProdID", length = 10)
	private String prodID;
	@Column(name = "RcNo", length = 50)
	private String rcNo;
	@Column(name = "CompType", length = 1)
	private String compType;
	@Column(name = "CompFormat")
	private boolean compFormat;
	@Column(name = "FromDate")
	private LocalDateTime fromDate;
	@Column(name = "ToDate")
	private LocalDateTime toDate;
	@Column(name = "Address1", length = 40)
	private String address1;
	@Column(name = "Address2", length = 40)
	private String address2;
	@Column(name = "City", length = 35)
	private String city;
	@Column(name = "Pincode", length = 7)
	private String pincode;
	@Column(name = "State", length = 35)
	private String state;
	@Column(name = "Phone1", length = 20)
	private String phone1;
	@Column(name = "Phone2", length = 20)
	private String phone2;
	@Column(name = "Fax", length = 20)
	private String fax;
	@Column(name = "Email", length = 50)
	private String email;
	@Column(name = "Quantity")
	private boolean quantity;
	@Column(name = "QtyWidth", length = 1)
	private String qtyWidth;
	@Column(name = "QtyDecimal", length = 1)
	private String qtyDecimal;
	@Column(name = "QtyTotal")
	private boolean qtyTotal;
	@Column(name = "AllBDate")
	private LocalDateTime allBDate;
	@Column(name = "LocalBDate")
	private LocalDateTime localBDate;
	@Column(name = "BNoofAc")
	private int bNoofAc;
	@Column(name = "BNoofEntries")
	private int bNoofEntries;
	@Column(name = "BLastTransDate")
	private LocalDateTime bLastTransDate;
	@Column(name = "AllBMedium", length = 10)
	private String allBMedium;
	@Column(name = "LocalBMedium", length = 10)
	private String localBMedium;
	@Column(name = "AllBUser", length = 10)
	private String allBUser;
	@Column(name = "LocalBUser", length = 10)
	private String localBUser;
	@Column(name = "RestoreDate")
	private LocalDateTime restoreDate;
	@Column(name = "RestoreMedium", length = 1)
	private String restoreMedium;
	@Column(name = "RestoreUser", length = 10)
	private String restoreUser;
	@Column(name = "CNoofAc")
	private int cNoofAc;
	@Column(name = "CNoofEntries")
	private int cNoofEntries;
	@Column(name = "CLastTransDate")
	private LocalDateTime cLastTransDate;
	@Column(name = "CurrUser", length = 10)
	private String currUser;
	@Column(name = "LastUser", length = 10)
	private String lastUser;
	@Column(name = "CurrLogin")
	private LocalDateTime currLogin;
	@Column(name = "LastLogin")
	private LocalDateTime lastLogin;
	@Column(name = "LogOut")
	private Date logOut;
	@Column(name = "LockDate")
	private LocalDateTime lockDate;
	@Column(name = "CloseStk")
	private Double closeStk;
	@Column(name = "OpenCash")
	private Double openCash;
	@Column(name = "OpenDiffDr")
	private Double openDiffDr;
	@Column(name = "OpenDiffCr")
	private Double openDiffCr;
	@Column(name = "Hide")
	private boolean hide;
	@Column(name = "GraphicPrint")
	private boolean graphicPrint;

	public Company() {

	}

	/**
	 * @param userid
	 * @param compName
	 * @param compYear
	 * @param shortName
	 * @param prodID
	 * @param rcNo
	 * @param compType
	 * @param compFormat
	 * @param fromDate
	 * @param toDate
	 * @param address1
	 * @param address2
	 * @param city
	 * @param pincode
	 * @param state
	 * @param phone1
	 * @param phone2
	 * @param fax
	 * @param email
	 * @param quantity
	 * @param qtyWidth
	 * @param qtyDecimal
	 * @param qtyTotal
	 * @param allBDate
	 * @param localBDate
	 * @param bNoofAc
	 * @param bNoofEntries
	 * @param bLastTransDate
	 * @param allBMedium
	 * @param localBMedium
	 * @param allBUser
	 * @param localBUser
	 * @param restoreDate
	 * @param restoreMedium
	 * @param restoreUser
	 * @param cNoofAc
	 * @param cNoofEntries
	 * @param cLastTransDate
	 * @param currUser
	 * @param lastUser
	 * @param currLogin
	 * @param lastLogin
	 * @param logOut
	 * @param lockDate
	 * @param closeStk
	 * @param openCash
	 * @param openDiffDr
	 * @param openDiffCr
	 * @param hide
	 * @param graphicPrint
	 */
	public Company(Long userid, String compName, String compYear, String shortName, String prodID, String rcNo,
			String compType, boolean compFormat, LocalDateTime fromDate, LocalDateTime toDate, String address1,
			String address2, String city, String pincode, String state, String phone1, String phone2, String fax,
			String email, boolean quantity, String qtyWidth, String qtyDecimal, boolean qtyTotal,
			LocalDateTime allBDate, LocalDateTime localBDate, int bNoofAc, int bNoofEntries,
			LocalDateTime bLastTransDate, String allBMedium, String localBMedium, String allBUser, String localBUser,
			LocalDateTime restoreDate, String restoreMedium, String restoreUser, int cNoofAc, int cNoofEntries,
			LocalDateTime cLastTransDate, String currUser, String lastUser, LocalDateTime currLogin,
			LocalDateTime lastLogin, Date logOut, LocalDateTime lockDate, Double closeStk, Double openCash,
			Double openDiffDr, Double openDiffCr, boolean hide, boolean graphicPrint) {
		super();
		this.userid = userid;
		this.compName = compName;
		this.compYear = compYear;
		this.shortName = shortName;
		this.prodID = prodID;
		this.rcNo = rcNo;
		this.compType = compType;
		this.compFormat = compFormat;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.pincode = pincode;
		this.state = state;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.fax = fax;
		this.email = email;
		this.quantity = quantity;
		this.qtyWidth = qtyWidth;
		this.qtyDecimal = qtyDecimal;
		this.qtyTotal = qtyTotal;
		this.allBDate = allBDate;
		this.localBDate = localBDate;
		this.bNoofAc = bNoofAc;
		this.bNoofEntries = bNoofEntries;
		this.bLastTransDate = bLastTransDate;
		this.allBMedium = allBMedium;
		this.localBMedium = localBMedium;
		this.allBUser = allBUser;
		this.localBUser = localBUser;
		this.restoreDate = restoreDate;
		this.restoreMedium = restoreMedium;
		this.restoreUser = restoreUser;
		this.cNoofAc = cNoofAc;
		this.cNoofEntries = cNoofEntries;
		this.cLastTransDate = cLastTransDate;
		this.currUser = currUser;
		this.lastUser = lastUser;
		this.currLogin = currLogin;
		this.lastLogin = lastLogin;
		this.logOut = logOut;
		this.lockDate = lockDate;
		this.closeStk = closeStk;
		this.openCash = openCash;
		this.openDiffDr = openDiffDr;
		this.openDiffCr = openDiffCr;
		this.hide = hide;
		this.graphicPrint = graphicPrint;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getCompYear() {
		return compYear;
	}

	public void setCompYear(String compYear) {
		this.compYear = compYear;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getProdID() {
		return prodID;
	}

	public void setProdID(String prodID) {
		this.prodID = prodID;
	}

	public String getRcNo() {
		return rcNo;
	}

	public void setRcNo(String rcNo) {
		this.rcNo = rcNo;
	}

	public String getCompType() {
		return compType;
	}

	public void setCompType(String compType) {
		this.compType = compType;
	}

	public boolean isCompFormat() {
		return compFormat;
	}

	public void setCompFormat(boolean compFormat) {
		this.compFormat = compFormat;
	}

	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isQuantity() {
		return quantity;
	}

	public void setQuantity(boolean quantity) {
		this.quantity = quantity;
	}

	public String getQtyWidth() {
		return qtyWidth;
	}

	public void setQtyWidth(String qtyWidth) {
		this.qtyWidth = qtyWidth;
	}

	public String getQtyDecimal() {
		return qtyDecimal;
	}

	public void setQtyDecimal(String qtyDecimal) {
		this.qtyDecimal = qtyDecimal;
	}

	public boolean isQtyTotal() {
		return qtyTotal;
	}

	public void setQtyTotal(boolean qtyTotal) {
		this.qtyTotal = qtyTotal;
	}

	public LocalDateTime getAllBDate() {
		return allBDate;
	}

	public void setAllBDate(LocalDateTime allBDate) {
		this.allBDate = allBDate;
	}

	public LocalDateTime getLocalBDate() {
		return localBDate;
	}

	public void setLocalBDate(LocalDateTime localBDate) {
		this.localBDate = localBDate;
	}

	public int getbNoofAc() {
		return bNoofAc;
	}

	public void setbNoofAc(int bNoofAc) {
		this.bNoofAc = bNoofAc;
	}

	public int getbNoofEntries() {
		return bNoofEntries;
	}

	public void setbNoofEntries(int bNoofEntries) {
		this.bNoofEntries = bNoofEntries;
	}

	public LocalDateTime getbLastTransDate() {
		return bLastTransDate;
	}

	public void setbLastTransDate(LocalDateTime bLastTransDate) {
		this.bLastTransDate = bLastTransDate;
	}

	public String getAllBMedium() {
		return allBMedium;
	}

	public void setAllBMedium(String allBMedium) {
		this.allBMedium = allBMedium;
	}

	public String getLocalBMedium() {
		return localBMedium;
	}

	public void setLocalBMedium(String localBMedium) {
		this.localBMedium = localBMedium;
	}

	public String getAllBUser() {
		return allBUser;
	}

	public void setAllBUser(String allBUser) {
		this.allBUser = allBUser;
	}

	public String getLocalBUser() {
		return localBUser;
	}

	public void setLocalBUser(String localBUser) {
		this.localBUser = localBUser;
	}

	public LocalDateTime getRestoreDate() {
		return restoreDate;
	}

	public void setRestoreDate(LocalDateTime restoreDate) {
		this.restoreDate = restoreDate;
	}

	public String getRestoreMedium() {
		return restoreMedium;
	}

	public void setRestoreMedium(String restoreMedium) {
		this.restoreMedium = restoreMedium;
	}

	public String getRestoreUser() {
		return restoreUser;
	}

	public void setRestoreUser(String restoreUser) {
		this.restoreUser = restoreUser;
	}

	public int getcNoofAc() {
		return cNoofAc;
	}

	public void setcNoofAc(int cNoofAc) {
		this.cNoofAc = cNoofAc;
	}

	public int getcNoofEntries() {
		return cNoofEntries;
	}

	public void setcNoofEntries(int cNoofEntries) {
		this.cNoofEntries = cNoofEntries;
	}

	public LocalDateTime getcLastTransDate() {
		return cLastTransDate;
	}

	public void setcLastTransDate(LocalDateTime cLastTransDate) {
		this.cLastTransDate = cLastTransDate;
	}

	public String getCurrUser() {
		return currUser;
	}

	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}

	public String getLastUser() {
		return lastUser;
	}

	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}

	public LocalDateTime getCurrLogin() {
		return currLogin;
	}

	public void setCurrLogin(LocalDateTime currLogin) {
		this.currLogin = currLogin;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLogOut() {
		return logOut;
	}

	public void setLogOut(Date logOut) {
		this.logOut = logOut;
	}

	public LocalDateTime getLockDate() {
		return lockDate;
	}

	public void setLockDate(LocalDateTime lockDate) {
		this.lockDate = lockDate;
	}

	public Double getCloseStk() {
		return closeStk;
	}

	public void setCloseStk(Double closeStk) {
		this.closeStk = closeStk;
	}

	public Double getOpenCash() {
		return openCash;
	}

	public void setOpenCash(Double openCash) {
		this.openCash = openCash;
	}

	public Double getOpenDiffDr() {
		return openDiffDr;
	}

	public void setOpenDiffDr(Double openDiffDr) {
		this.openDiffDr = openDiffDr;
	}

	public Double getOpenDiffCr() {
		return openDiffCr;
	}

	public void setOpenDiffCr(Double openDiffCr) {
		this.openDiffCr = openDiffCr;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isGraphicPrint() {
		return graphicPrint;
	}

	public void setGraphicPrint(boolean graphicPrint) {
		this.graphicPrint = graphicPrint;
	}

	@Override
	public String toString() {
		return "Company [companyid=" + companyid + ", userid=" + userid + ", compName=" + compName + ", compYear="
				+ compYear + ", shortName=" + shortName + ", prodID=" + prodID + ", rcNo=" + rcNo + ", compType="
				+ compType + ", compFormat=" + compFormat + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", address1=" + address1 + ", address2=" + address2 + ", city=" + city + ", pincode=" + pincode
				+ ", state=" + state + ", phone1=" + phone1 + ", phone2=" + phone2 + ", fax=" + fax + ", email=" + email
				+ ", quantity=" + quantity + ", qtyWidth=" + qtyWidth + ", qtyDecimal=" + qtyDecimal + ", qtyTotal="
				+ qtyTotal + ", allBDate=" + allBDate + ", localBDate=" + localBDate + ", bNoofAc=" + bNoofAc
				+ ", bNoofEntries=" + bNoofEntries + ", bLastTransDate=" + bLastTransDate + ", allBMedium=" + allBMedium
				+ ", localBMedium=" + localBMedium + ", allBUser=" + allBUser + ", localBUser=" + localBUser
				+ ", restoreDate=" + restoreDate + ", restoreMedium=" + restoreMedium + ", restoreUser=" + restoreUser
				+ ", cNoofAc=" + cNoofAc + ", cNoofEntries=" + cNoofEntries + ", cLastTransDate=" + cLastTransDate
				+ ", currUser=" + currUser + ", lastUser=" + lastUser + ", currLogin=" + currLogin + ", lastLogin="
				+ lastLogin + ", logOut=" + logOut + ", lockDate=" + lockDate + ", closeStk=" + closeStk + ", openCash="
				+ openCash + ", openDiffDr=" + openDiffDr + ", openDiffCr=" + openDiffCr + ", hide=" + hide
				+ ", graphicPrint=" + graphicPrint + "]";
	}

	@Override
	public int compareTo(Company o) {
		return this.companyid.compareTo(o.companyid);
	}

}
