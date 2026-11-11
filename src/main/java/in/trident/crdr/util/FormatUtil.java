package in.trident.crdr.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

/**
 * Shared thread-safe Indian currency/number and date formatting utilities.
 */
public final class FormatUtil {

	public static final Locale EN_IN = Locale.forLanguageTag("en-IN");
	public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	public static final DateTimeFormatter TIMESTAMP_SAFE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	private static final LocalizedNumberFormatter CURRENCY_FORMATTER = NumberFormatter.withLocale(EN_IN)
			.precision(Precision.fixedFraction(2));

	private FormatUtil() {
	}

	public static LocalizedNumberFormatter indianCurrencyFormatter() {
		return CURRENCY_FORMATTER;
	}

	public static String formatAmount(Double value) {
		BigDecimal bd = nvl(value).setScale(2, RoundingMode.HALF_UP);
		return CURRENCY_FORMATTER.format(bd).toString();
	}

	public static String formatAmount(BigDecimal value) {
		BigDecimal bd = (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
		return CURRENCY_FORMATTER.format(bd).toString();
	}

	public static BigDecimal nvl(Double value) {
		return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
	}

	public static double nvlDouble(Double value) {
		return value == null ? 0.0d : value;
	}

	public static BigDecimal parseFormattedAmount(String text) {
		if (text == null || text.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(text.replace(",", "").trim());
	}

	public static LocalDate parseIsoDate(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Date must not be blank");
		}
		String normalized = normalizeDatePrefix(value);
		try {
			return LocalDate.parse(normalized, ISO_DATE);
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("Invalid date format (expected yyyy-MM-dd): " + value, ex);
		}
	}

	public static String formatDisplayDate(String value) {
		return parseIsoDate(value).format(DISPLAY_DATE);
	}

	public static String normalizeDatePrefix(String value) {
		String trimmed = value == null ? "" : value.trim();
		return trimmed.length() > 10 ? trimmed.substring(0, 10) : trimmed;
	}
}
