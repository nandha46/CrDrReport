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
 * @author Nandhakumar Subramanian
 * 
 * @since 25 nov 2021
 * 
 * @version 0.0.9
 *
 */
@Entity
@Table(name ="company_selection")
public class CompanySelection {
	@Id
	@Column(name ="sid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sid;
	private Long companyId;
	private Long userId;
	private String companyName;
	private String year;
	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	private String address;
	private int noOfAccs;
	private int noOfEntries;
	private Date uploadDate;
	private Double closingStock;
	private Double openCash;
	private String companyType;
	
	public CompanySelection() {
		// Default constructor
	}
	
	public CompanySelection(Long cid, long uid, String cName, String year, LocalDateTime fromDate, LocalDateTime toDate, String address, int noOfaccs, int noOfEntries, Date uploadDate, Double closingStock, Double openCash, String companyType) {
		this.companyId = cid;
		this.userId = uid;
		this.companyName = cName;
		this.year = year;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.address = address;
		this.noOfAccs = noOfaccs;
		this.noOfEntries = noOfEntries;
		this.uploadDate = uploadDate;
		this.closingStock = closingStock;
		this.openCash = openCash;
		this.companyType = companyType;
	}
	
	public CompanySelection(Long sid, Long cid, long uid, String cName, String year, LocalDateTime fromDate, LocalDateTime toDate, String address, int noOfaccs, int noOfEntries, Date uploadDate, Double closingStock, Double openCash, String companyType) {
		this.sid = sid;
		this.companyId = cid;
		this.userId = uid;
		this.companyName = cName;
		this.year = year;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.address = address;
		this.noOfAccs = noOfaccs;
		this.noOfEntries = noOfEntries;
		this.uploadDate = uploadDate;
		this.closingStock = closingStock;
		this.openCash = openCash;
		this.companyType = companyType;
	}
	
	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getNoOfAccs() {
		return noOfAccs;
	}
	public void setNoOfAccs(int noOfAccs) {
		this.noOfAccs = noOfAccs;
	}
	public int getNoOfEntries() {
		return noOfEntries;
	}
	public void setNoOfEntries(int noOfEntries) {
		this.noOfEntries = noOfEntries;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Double getClosingStock() {
		return closingStock;
	}
	public void setClosingStock(Double closingStock) {
		this.closingStock = closingStock;
	}
	public Double getOpenCash() {
		return openCash;
	}
	public void setOpenCash(Double openCash) {
		this.openCash = openCash;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	@Override
	public String toString() {
		return "CompanySelection [sid=" + sid + ", companyId=" + companyId + ", userId=" + userId + ", companyName="
				+ companyName + ", year=" + year + ", fromDate=" + fromDate + ", toDate=" + toDate + ", address="
				+ address + ", noOfAccs=" + noOfAccs + ", noOfEntries=" + noOfEntries + ", uploadDate=" + uploadDate
				+ ", closingStock=" + closingStock + ", openCash=" + openCash + ", companyType=" + companyType + "]";
	}
}
