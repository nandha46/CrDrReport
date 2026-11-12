package in.trident.crdr.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.Transactions;
import in.trident.crdr.models.TrialView;

/**
 * Generates printable PDF and Excel (.xlsx) financial reports.
 */
@Service
public class ReportExportService {

	private static final String POWERED_BY = "CrDr Report powered by Extenzia Technology Solutions";

	public byte[] exportDaybookPdf(String companyName, String period, String openingBal, List<DaybookView> views) {
		return buildPdf(companyName, "Daybook Report", period, new String[] { "Date / Account", "Narration", "Debit", "Credit" },
				new float[] { 22f, 44f, 17f, 17f }, table -> {
					boolean first = true;
					for (DaybookView v : views) {
						if (first) {
							addRow(table, new String[] { "", "Opening Balance", "", safe(openingBal) }, true, false);
							first = false;
						}
						addRow(table, new String[] { safe(v.getDate()), safe(v.getDayOfWeek()), "", "" }, true, false);
						if (v.getTransList() != null) {
							for (Transactions t : v.getTransList()) {
								addRow(table, new String[] { safe(t.getShortName()), safe(t.getNarration()),
										safe(t.getDebitAmt()), safe(t.getCreditAmt()) }, false, false);
							}
						}
						addRow(table, new String[] { "", "Day Totals", safe(v.getDebitTot()), safe(v.getCreditTot()) }, true, false);
						addRow(table, new String[] { "", "Closing Balance", "", safe(v.getClosingBal()) }, true, false);
					}
				});
	}

	public byte[] exportDaybookExcel(String companyName, String period, String openingBal, List<DaybookView> views) {
		return buildExcel(companyName, "Daybook Report", period,
				new String[] { "Date / Account", "Narration", "Debit", "Credit" }, (sheet, styles) -> {
					int r = 4;
					boolean first = true;
					for (DaybookView v : views) {
						if (first) {
							r = writeExcelRow(sheet, r, new String[] { "", "Opening Balance", "", safe(openingBal) }, styles.bold, 2);
							first = false;
						}
						r = writeExcelRow(sheet, r, new String[] { safe(v.getDate()), safe(v.getDayOfWeek()), "", "" }, styles.bold, 2);
						if (v.getTransList() != null) {
							for (Transactions t : v.getTransList()) {
								r = writeExcelRow(sheet, r, new String[] { safe(t.getShortName()), safe(t.getNarration()),
										safe(t.getDebitAmt()), safe(t.getCreditAmt()) }, styles.body, 2);
							}
						}
						r = writeExcelRow(sheet, r, new String[] { "", "Day Totals", safe(v.getDebitTot()), safe(v.getCreditTot()) }, styles.bold, 2);
						r = writeExcelRow(sheet, r, new String[] { "", "Closing Balance", "", safe(v.getClosingBal()) }, styles.bold, 2);
					}
				});
	}

	public byte[] exportLedgerPdf(String companyName, String period, List<LedgerView> ledgers) {
		return buildPdf(companyName, "Ledger Report", period,
				new String[] { "Date", "Narration", "Debit", "Credit", "Balance", "Dr/Cr" },
				new float[] { 14f, 38f, 14f, 14f, 14f, 6f }, table -> {
					for (LedgerView lv : ledgers) {
						addRow(table, new String[] { safe(lv.getDate()), safe(lv.getAccheadName()) + " (Opening)",
								"", "", safe(lv.getOpeningBal()), safe(lv.getdOrC()) }, true, false);
						if (lv.getListDailybooks() != null) {
							for (Dailybooks d : lv.getListDailybooks()) {
								addRow(table, new String[] { safe(d.getDate()), safe(d.getNarration()),
										safe(d.getDebitAmt()), safe(d.getCreditAmt()), safe(d.getBalance()),
										safe(d.getDebitOrCredit()) }, false, false);
							}
						}
					}
				});
	}

	public byte[] exportLedgerExcel(String companyName, String period, List<LedgerView> ledgers) {
		return buildExcel(companyName, "Ledger Report", period,
				new String[] { "Date", "Narration", "Debit", "Credit", "Balance", "Dr/Cr" }, (sheet, styles) -> {
					int r = 4;
					for (LedgerView lv : ledgers) {
						r = writeExcelRow(sheet, r, new String[] { safe(lv.getDate()),
								safe(lv.getAccheadName()) + " (Opening)", "", "", safe(lv.getOpeningBal()),
								safe(lv.getdOrC()) }, styles.bold, 2);
						if (lv.getListDailybooks() != null) {
							for (Dailybooks d : lv.getListDailybooks()) {
								r = writeExcelRow(sheet, r, new String[] { safe(d.getDate()), safe(d.getNarration()),
										safe(d.getDebitAmt()), safe(d.getCreditAmt()), safe(d.getBalance()),
										safe(d.getDebitOrCredit()) }, styles.body, 2);
							}
						}
					}
				});
	}

	public byte[] exportTrialBalancePdf(String companyName, String period, List<TrialView> rows) {
		return buildPdf(companyName, "Trial Balance", period, new String[] { "Account Name", "Debit", "Credit" },
				new float[] { 56f, 22f, 22f }, table -> {
					for (TrialView row : rows) {
						addRow(table, new String[] { safe(row.getAccName()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader(), false);
					}
				});
	}

	public byte[] exportTrialBalanceExcel(String companyName, String period, List<TrialView> rows) {
		return buildExcel(companyName, "Trial Balance", period, new String[] { "Account Name", "Debit", "Credit" },
				(sheet, styles) -> {
					int r = 4;
					for (TrialView row : rows) {
						r = writeExcelRow(sheet, r,
								new String[] { safe(row.getAccName()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader() ? styles.bold : styles.body, 1);
					}
				});
	}

	public byte[] exportTradingPlPdf(String companyName, String period, List<TradingPLView> rows) {
		return buildPdf(companyName, "Trading & Profit and Loss", period,
				new String[] { "Particulars", "Debit", "Credit" }, new float[] { 56f, 22f, 22f }, table -> {
					for (TradingPLView row : rows) {
						addRow(table,
								new String[] { safe(row.getParticulars()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader(), false);
					}
				});
	}

	public byte[] exportTradingPlExcel(String companyName, String period, List<TradingPLView> rows) {
		return buildExcel(companyName, "Trading & Profit and Loss", period,
				new String[] { "Particulars", "Debit", "Credit" }, (sheet, styles) -> {
					int r = 4;
					for (TradingPLView row : rows) {
						r = writeExcelRow(sheet, r,
								new String[] { safe(row.getParticulars()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader() ? styles.bold : styles.body, 1);
					}
				});
	}

	public byte[] exportBalanceSheetPdf(String companyName, String period, List<BalanceSheetView> rows) {
		return buildPdf(companyName, "Balance Sheet", period,
				new String[] { "Particulars", "Liabilities", "Assets" }, new float[] { 56f, 22f, 22f }, table -> {
					for (BalanceSheetView row : rows) {
						addRow(table,
								new String[] { safe(row.getParticulars()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader(), false);
					}
				});
	}

	public byte[] exportBalanceSheetExcel(String companyName, String period, List<BalanceSheetView> rows) {
		return buildExcel(companyName, "Balance Sheet", period,
				new String[] { "Particulars", "Liabilities", "Assets" }, (sheet, styles) -> {
					int r = 4;
					for (BalanceSheetView row : rows) {
						r = writeExcelRow(sheet, r,
								new String[] { safe(row.getParticulars()), safe(row.getDebit()), safe(row.getCredit()) },
								row.isHeader() ? styles.bold : styles.body, 1);
					}
				});
	}

	@FunctionalInterface
	private interface PdfBodyPopulator {
		void populate(PdfPTable table);
	}

	@FunctionalInterface
	private interface ExcelBodyPopulator {
		void populate(Sheet sheet, ExcelStyles styles);
	}

	private byte[] buildPdf(String companyName, String reportTitle, String period, String[] headers, float[] widths,
			PdfBodyPopulator populator) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document doc = new Document(PageSize.A4, 28, 28, 32, 32);
		try {
			PdfWriter.getInstance(doc, out);
			doc.open();

			com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(15, 23, 42));
			com.lowagie.text.Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
			com.lowagie.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, Color.WHITE);
			com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(100, 116, 139));

			Paragraph companyPara = new Paragraph(safe(companyName), titleFont);
			companyPara.setAlignment(Element.ALIGN_CENTER);
			doc.add(companyPara);

			Paragraph reportPara = new Paragraph(reportTitle + (period != null && !period.isEmpty() ? "  (" + period + ")" : ""),
					subFont);
			reportPara.setAlignment(Element.ALIGN_CENTER);
			reportPara.setSpacingAfter(12f);
			doc.add(reportPara);

			PdfPTable table = new PdfPTable(headers.length);
			table.setWidthPercentage(100);
			table.setWidths(widths);
			table.setHeaderRows(1);

			for (String h : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
				cell.setBackgroundColor(new Color(15, 23, 42));
				cell.setPadding(6f);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			populator.populate(table);
			doc.add(table);

			Paragraph footer = new Paragraph(POWERED_BY, footerFont);
			footer.setAlignment(Element.ALIGN_RIGHT);
			footer.setSpacingBefore(10f);
			doc.add(footer);

			doc.close();
		} catch (DocumentException e) {
			throw new IllegalStateException("Failed to generate PDF report", e);
		}
		return out.toByteArray();
	}

	private void addRow(PdfPTable table, String[] values, boolean bold, boolean shaded) {
		com.lowagie.text.Font font = FontFactory.getFont(bold ? FontFactory.HELVETICA_BOLD : FontFactory.HELVETICA, 9,
				new Color(15, 23, 42));
		for (int i = 0; i < values.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(values[i], font));
			cell.setPadding(5f);
			if (shaded || bold) {
				cell.setBackgroundColor(new Color(241, 245, 249));
			}
			cell.setHorizontalAlignment(i >= 2 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
			table.addCell(cell);
		}
	}

	private byte[] buildExcel(String companyName, String reportTitle, String period, String[] headers,
			ExcelBodyPopulator populator) {
		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = wb.createSheet("Report");
			ExcelStyles styles = new ExcelStyles(wb);

			Row r0 = sheet.createRow(0);
			Cell c0 = r0.createCell(0);
			c0.setCellValue(safe(companyName));
			c0.setCellStyle(styles.title);

			Row r1 = sheet.createRow(1);
			Cell c1 = r1.createCell(0);
			c1.setCellValue(reportTitle + (period != null && !period.isEmpty() ? " (" + period + ")" : ""));
			c1.setCellStyle(styles.subtitle);

			Row hdr = sheet.createRow(3);
			for (int i = 0; i < headers.length; i++) {
				Cell hc = hdr.createCell(i);
				hc.setCellValue(headers[i]);
				hc.setCellStyle(styles.header);
			}

			populator.populate(sheet, styles);

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}
			wb.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to generate Excel report", e);
		}
	}

	private int writeExcelRow(Sheet sheet, int rowIdx, String[] values, CellStyle style, int numericStartCol) {
		Row row = sheet.createRow(rowIdx);
		for (int i = 0; i < values.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(values[i]);
			cell.setCellStyle(style);
		}
		return rowIdx + 1;
	}

	private String safe(String val) {
		return val == null ? "" : val;
	}

	private static class ExcelStyles {
		final CellStyle title;
		final CellStyle subtitle;
		final CellStyle header;
		final CellStyle body;
		final CellStyle bold;

		ExcelStyles(Workbook wb) {
			Font titleFont = wb.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 14);
			title = wb.createCellStyle();
			title.setFont(titleFont);

			Font subFont = wb.createFont();
			subFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			subtitle = wb.createCellStyle();
			subtitle.setFont(subFont);

			Font hdrFont = wb.createFont();
			hdrFont.setBold(true);
			hdrFont.setColor(IndexedColors.WHITE.getIndex());
			header = wb.createCellStyle();
			header.setFont(hdrFont);
			header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			header.setAlignment(HorizontalAlignment.CENTER);

			body = wb.createCellStyle();
			body.setBorderBottom(BorderStyle.THIN);
			body.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

			Font boldFont = wb.createFont();
			boldFont.setBold(true);
			bold = wb.createCellStyle();
			bold.cloneStyleFrom(body);
			bold.setFont(boldFont);
			bold.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			bold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
	}
}
