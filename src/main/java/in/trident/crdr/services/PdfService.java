package in.trident.crdr.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.util.FormatUtil;
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

	@Autowired
	private CompSelectionRepo compSelectionRepo;

	public String exportPdf(LedgerForm ledgerForm, Long uid, Long cid) throws IOException, JRException {
		List<LedgerView> ledgers = ledgerService.createLedgerViewList(ledgerForm, uid, cid);
		ClassPathResource template = new ClassPathResource("Blank_A4.jrxml");
		try (InputStream templateStream = template.getInputStream()) {
			JasperDesign jd = JRXmlLoader.load(templateStream);
			JasperReport jasperReport = JasperCompileManager.compileReport(jd);
			JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(ledgers);

			CompanySelection cs = compSelectionRepo.findCompanyByUser(uid);
			String companyName = cs != null && cs.getCompanyName() != null ? cs.getCompanyName() : "CrDr Report";
			String address = cs != null && cs.getAddress() != null ? cs.getAddress() : "";

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("CompanyName", companyName);
			parameters.put("Address", address);
			parameters.put("LedgerListParam", datasource);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			File pdfDir = new File("pdf");
			if (!pdfDir.exists() && !pdfDir.mkdirs()) {
				throw new IOException("Unable to create output directory: " + pdfDir.getAbsolutePath());
			}
			String filename = "ledger_" + LocalDateTime.now().format(FormatUtil.TIMESTAMP_SAFE) + ".pdf";
			File outputFile = new File(pdfDir, filename);
			JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.getAbsolutePath());
			return outputFile.getAbsolutePath();
		}
	}
}
