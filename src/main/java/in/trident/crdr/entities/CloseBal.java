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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name="CDate")
	private String date;
	
	@Column(name="debit_total")
	private double drTot;
	
	@Column(name="credit_total")
	private double crTot;
	
	@Column(name="close_bal")
	private double closeBal;

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

	@Override
	public String toString() {
		return "CloseBal [id=" + id + ", date=" + date + ", drTot=" + drTot + ", crTot=" + crTot + ", closeBal="
				+ closeBal + "]";
	}

}
