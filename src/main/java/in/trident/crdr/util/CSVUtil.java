package in.trident.crdr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.Daybook;

/**
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9e
 * 
 * @since 14 oct 2021
 *
 */
public final class CSVUtil {

	private static final String[] EXPECTED_HEADERS = { "Sno", "TDate", "Narration", "acccode", "dramt", "cramt",
			"stkvalue" };

	private CSVUtil() {
	}

	public static boolean hasCSVFormat(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return false;
		}
		String contentType = file.getContentType();
		String filename = file.getOriginalFilename();
		boolean csvType = "text/csv".equalsIgnoreCase(contentType)
				|| "application/csv".equalsIgnoreCase(contentType)
				|| "application/vnd.ms-excel".equalsIgnoreCase(contentType);
		boolean csvExt = filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".csv");
		return csvType || csvExt;
	}

	public static List<Daybook> csvToDaybook(Long userid, InputStream in) {
		return csvToDaybook(userid, 0L, in);
	}

	public static List<Daybook> csvToDaybook(Long userid, Long companyId, InputStream in) {
		CSVFormat format = CSVFormat.DEFAULT.builder()
				.setHeader(EXPECTED_HEADERS)
				.setSkipHeaderRecord(true)
				.setIgnoreHeaderCase(true)
				.setTrim(true)
				.build();

		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
				CSVParser csvParser = new CSVParser(fileReader, format)) {
			List<Daybook> daybooks = new ArrayList<>();
			for (CSVRecord record : csvParser) {
				if (record.size() < EXPECTED_HEADERS.length) {
					throw new IllegalArgumentException(
							"CSV row " + record.getRecordNumber() + " does not match expected columns.");
				}
				Daybook daybook = new Daybook(
						Integer.parseInt(record.get("Sno")),
						record.get("TDate"),
						record.get("Narration"),
						Integer.parseInt(record.get("acccode")),
						Double.parseDouble(record.get("dramt")),
						Double.parseDouble(record.get("cramt")),
						Integer.parseInt(record.get("stkvalue")),
						userid,
						companyId == null ? 0L : companyId);
				daybooks.add(daybook);
			}
			return daybooks;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
		}
	}
}
