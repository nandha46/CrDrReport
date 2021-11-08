package in.trident.crdr.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
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
	
	public String exportPdf(LedgerForm ledgerForm, Long id) throws FileNotFoundException, JRException {
		List<LedgerView> ledgers = ledgerService.createLedgerViewList(ledgerForm, id);
		File file = ResourceUtils.getFile("classpath:Blank_A4.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(ledgers);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("CompanyName", "ZODIAC ENTERPRISES (2018-19)");
		parameters.put("Address", "#12, Main Road, Madurai - 625 001.");
		parameters.put("LedgerListParam", ledgers);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters , datasource);
		JasperExportManager.exportReportToPdfFile(jasperPrint,"\"C:\\Users\\nandh\\OneDrive\\Desktop\\Pdfs\\ledger.pdf\"");
		
		return "Report generated";
	}
}
