package in.trident.crdr.models;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9d
 * 
 * @since 20 jan 2022
 *
 */
public class AdminForm {

	private Long userId;
	private Long companyId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "AdminForm [userId=" + userId + ", companyId=" + companyId + "]";
	}

}
