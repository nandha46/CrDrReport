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
	
	
	/** 
	* Deprecated method. Better alternative found
	*	
	* public int findDays(String d1,String d2) {
	*	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	*	long diff=0l;
	*	try {
	*		diff = df.parse(d2).getTime() - df.parse(d1).getTime();
	*		diff /= 1000*60*60*24;
	*	} catch (ParseException e) {
	*		e.printStackTrace();
	*	}
	*	return (int)diff;
	*  }
	*
	*/
	
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
