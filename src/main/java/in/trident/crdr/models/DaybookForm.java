package in.trident.crdr.models;

/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.2
 */
public class DaybookForm {

	private String startDate;
	private String endDate;

	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "DaybookForm [startDate=" + startDate + ", endDate=" + endDate + "]";
	}

}
