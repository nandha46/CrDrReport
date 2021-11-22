package in.trident.crdr.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.core.util.datetime.DatePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class PdfService {
	
	@Autowired
	private LedgerService ledgerService;
	
	public String exportPdf(LedgerForm ledgerForm, Long id) throws FileNotFoundException, JRException {
		List<LedgerView> ledgers = ledgerService.createLedgerViewList(ledgerForm, id);
		File file = ResourceUtils.getFile("classpath:Blank_A4.jrxml");
		JasperDesign jd = JRXmlLoader.load(new FileInputStream(file));
		JasperReport jasperReport = JasperCompileManager.compileReport(jd);
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(ledgers);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("CompanyName", "ZODIAC ENTERPRISES (2018-19)");
		parameters.put("Address", "#12, Main Road, Madurai - 625 001.");
		parameters.put("LedgerListParam", datasource);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters , new JREmptyDataSource());
		JasperExportManager.exportReportToPdfFile(jasperPrint,"C:\\Users\\nandh\\OneDrive\\Desktop\\Pdfs\\ledger_"+Calendar.getInstance().getTime()+".pdf");
		return "Report generated";
	}
}
