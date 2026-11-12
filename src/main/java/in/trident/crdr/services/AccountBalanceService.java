package in.trident.crdr.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.util.FormatUtil;

/**
 * Shared thread-safe balance calculation service using BigDecimal and per-company scoping.
 */
@Service
public class AccountBalanceService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private CompSelectionRepo compSelectionRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;

	public static final class BalanceResult {
		private final BigDecimal amount;
		private final String side;

		public BalanceResult(BigDecimal amount, String side) {
			this.amount = amount.setScale(2, RoundingMode.HALF_UP);
			this.side = side;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public String getSide() {
			return side;
		}

		public String getFormattedAmount() {
			return FormatUtil.formatAmount(amount);
		}

		public String[] toFormattedArray() {
			return new String[] { getFormattedAmount(), side };
		}
	}

	public LocalDate resolveFinancialYearStart(Long uid, LocalDate referenceDate) {
		if (uid != null) {
			CompanySelection cs = compSelectionRepo.findCompanyByUser(uid);
			if (cs != null && cs.getFromDate() != null) {
				return cs.getFromDate().toLocalDate();
			}
		}
		int year = referenceDate.getMonthValue() >= 4 ? referenceDate.getYear() : referenceDate.getYear() - 1;
		return LocalDate.of(year, 4, 1);
	}

	public double resolveCashOnHand(String endDate, Long uid, Long cid) {
		Double exact = closeBalRepo.findCloseBalByDate(endDate, uid, cid);
		if (exact != null) {
			return exact;
		}
		List<Double> prior = closeBalRepo.findCloseBalsOnOrBefore(endDate, uid, cid);
		if (prior != null && !prior.isEmpty() && prior.get(0) != null) {
			return prior.get(0);
		}
		return 0.0;
	}

	public Map<Integer, BigDecimal> loadNetDeltasByAccCode(String startDate, String endDate, Long uid, Long cid) {
		List<Object[]> rows = daybookRepo.sumNetCrMinusDrByAccCode(startDate, endDate, uid, cid);
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<Integer, BigDecimal> map = new HashMap<>(rows.size() * 2);
		for (Object[] row : rows) {
			if (row[0] != null) {
				Integer code = ((Number) row[0]).intValue();
				BigDecimal delta = row[1] == null ? BigDecimal.ZERO : BigDecimal.valueOf(((Number) row[1]).doubleValue());
				map.put(code, delta);
			}
		}
		return map;
	}

	public BalanceResult resolveFromOpeningAndDeltaMap(double openingCr, double openingDr, Integer code,
			Map<Integer, BigDecimal> deltaMap) {
		if (code == null || code == 0) {
			return new BalanceResult(BigDecimal.ZERO, "Cr");
		}
		BigDecimal delta = deltaMap != null ? deltaMap.getOrDefault(code, BigDecimal.ZERO) : BigDecimal.ZERO;
		return resolveBalance(BigDecimal.valueOf(openingCr), BigDecimal.valueOf(openingDr), delta);
	}

	public String[] calculateAccHeadBalance(Integer code, String endDate, Long uid, Long cid) {
		if (code == null || code == 0) {
			return new String[] { "", "Cr" };
		}
		BigDecimal openingCr = FormatUtil.nvl(accHeadRepo.findCrAmt(code, uid, cid));
		BigDecimal openingDr = FormatUtil.nvl(accHeadRepo.findDrAmt(code, uid, cid));
		LocalDate end = FormatUtil.parseIsoDate(endDate);
		String fyStart = resolveFinancialYearStart(uid, end).format(FormatUtil.ISO_DATE);
		BigDecimal delta = FormatUtil.nvl(daybookRepo.openBal(code, fyStart, endDate, uid, cid));
		return resolveBalance(openingCr, openingDr, delta).toFormattedArray();
	}

	public String[] calculateScheduleBalance(Integer code, String endDate, Long uid, Long cid) {
		if (code == null || code == 0) {
			return new String[] { "", "Cr" };
		}
		BigDecimal openingCr = FormatUtil.nvl(scheduleRepo.findCrAmt(code, uid, cid));
		BigDecimal openingDr = FormatUtil.nvl(scheduleRepo.findDrAmt(code, uid, cid));
		LocalDate end = FormatUtil.parseIsoDate(endDate);
		String fyStart = resolveFinancialYearStart(uid, end).format(FormatUtil.ISO_DATE);
		BigDecimal delta = FormatUtil.nvl(daybookRepo.openBal(code, fyStart, endDate, uid, cid));
		return resolveBalance(openingCr, openingDr, delta).toFormattedArray();
	}

	public BalanceResult calculateAccHeadOpeningBalance(Integer code, String startDateStr, Long uid, Long cid) {
		BigDecimal openingCr = FormatUtil.nvl(accHeadRepo.findCrAmt(code, uid, cid));
		BigDecimal openingDr = FormatUtil.nvl(accHeadRepo.findDrAmt(code, uid, cid));
		LocalDate start = FormatUtil.parseIsoDate(startDateStr);
		LocalDate fyStart = resolveFinancialYearStart(uid, start);

		BigDecimal priorDelta = BigDecimal.ZERO;
		if (start.isAfter(fyStart)) {
			String priorEnd = start.minusDays(1).format(FormatUtil.ISO_DATE);
			priorDelta = FormatUtil.nvl(daybookRepo.openBal(code, fyStart.format(FormatUtil.ISO_DATE), priorEnd, uid, cid));
		}
		return resolveBalance(openingCr, openingDr, priorDelta);
	}

	/**
	 * Resolves signed balance from opening Cr/Dr and net credit-minus-debit transactions.
	 * Positive net represents Cr; negative net represents Dr.
	 */
	public BalanceResult resolveBalance(BigDecimal openingCr, BigDecimal openingDr, BigDecimal netCrMinusDr) {
		BigDecimal cr = openingCr == null ? BigDecimal.ZERO : openingCr;
		BigDecimal dr = openingDr == null ? BigDecimal.ZERO : openingDr;
		BigDecimal delta = netCrMinusDr == null ? BigDecimal.ZERO : netCrMinusDr;

		// Treat Credit as positive and Debit as negative on a unified signed axis.
		BigDecimal signedNet = cr.subtract(dr).add(delta);
		if (signedNet.compareTo(BigDecimal.ZERO) > 0) {
			return new BalanceResult(signedNet, "Cr");
		}
		if (signedNet.compareTo(BigDecimal.ZERO) < 0) {
			return new BalanceResult(signedNet.abs(), "Dr");
		}
		// Zero balance preserves the opening side convention (Dr when openingCr == 0).
		String zeroSide = cr.compareTo(BigDecimal.ZERO) == 0 ? "Dr" : "Cr";
		return new BalanceResult(BigDecimal.ZERO, zeroSide);
	}
}
