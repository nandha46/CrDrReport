package in.trident.crdr.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.repositories.DaybookRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class PdfService {
	
	@Autowired
	private LedgerService ledgerService;
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	public String exportPdf(LedgerForm ledgerForm, Long id) throws FileNotFoundException, JRException {
	//	List<LedgerView> ledgers = ledgerService.createLedgerViewList(ledgerForm, id);
		List<Daybook> daybooks = daybookRepo.findDaybookRange("2018-04-01", "2019-03-31");
		File file = ResourceUtils.getFile("classpath:daybooks.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(daybooks);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("CompanyName", "ZODIAC ENTERPRISES (2018-19)");
		parameters.put("Address", "#12, Main Road, Madurai - 625 001.");
	//	parameters.put("LedgerListParam", ledgers);
	//	parameters.put("Parameter1", daybooks);
	//	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters , datasource);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
		Random random = new Random();
		int x = random.nextInt();
		JasperExportManager.exportReportToPdfFile(jasperPrint,"C:\\Users\\nandh\\OneDrive\\Desktop\\Pdfs\\ledger_"+x+".pdf");
		
		return "Report generated";
	}
}
