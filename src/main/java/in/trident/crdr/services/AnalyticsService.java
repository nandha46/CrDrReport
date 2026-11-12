package in.trident.crdr.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.DashboardAnalytics;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.util.FormatUtil;

@Service
public class AnalyticsService {

	private final DaybookRepository daybookRepo;
	private final AccHeadRepo accHeadRepo;
	private final CompanyRepo companyRepo;

	public AnalyticsService(DaybookRepository daybookRepo, AccHeadRepo accHeadRepo, CompanyRepo companyRepo) {
		this.daybookRepo = daybookRepo;
		this.accHeadRepo = accHeadRepo;
		this.companyRepo = companyRepo;
	}

	public DashboardAnalytics buildDashboardAnalytics(Long uid, CompanySelection cs) {
		DashboardAnalytics dto = new DashboardAnalytics();
		dto.setCompanyName(cs.getCompanyName());
		dto.setActiveYear(cs.getYear());
		dto.setOpeningCash(FormatUtil.formatAmount(FormatUtil.nvlDouble(cs.getOpenCash())));
		dto.setClosingStock(FormatUtil.formatAmount(FormatUtil.nvlDouble(cs.getClosingStock())));

		String fromIso = cs.getFromDate() != null ? cs.getFromDate().toLocalDate().format(FormatUtil.ISO_DATE)
				: "1900-01-01";
		String toIso = cs.getToDate() != null ? cs.getToDate().toLocalDate().format(FormatUtil.ISO_DATE)
				: "2999-12-31";

		List<Daybook> entries = daybookRepo.findDaybookRange(fromIso, toIso, uid, cs.getCompanyId());
		if (entries == null) {
			entries = Collections.emptyList();
		}

		dto.setAccountCount(accHeadRepo.findAllAccHead(uid, cs.getCompanyId()).size());
		dto.setTransactionCount(entries.size());

		BigDecimal totalDr = BigDecimal.ZERO;
		BigDecimal totalCr = BigDecimal.ZERO;
		Map<String, double[]> byMonth = new TreeMap<>();

		for (Daybook d : entries) {
			double dr = FormatUtil.nvlDouble(d.getDrAmt());
			double cr = FormatUtil.nvlDouble(d.getCrAmt());
			totalDr = totalDr.add(BigDecimal.valueOf(dr));
			totalCr = totalCr.add(BigDecimal.valueOf(cr));

			String rawDate = d.getDate();
			String ym = (rawDate != null && rawDate.length() >= 7) ? rawDate.substring(0, 7) : "Unknown";
			double[] bucket = byMonth.computeIfAbsent(ym, k -> new double[2]);
			bucket[0] += dr;
			bucket[1] += cr;
		}

		dto.setTotalDebit(FormatUtil.formatAmount(totalDr));
		dto.setTotalCredit(FormatUtil.formatAmount(totalCr));
		dto.setNetFlow(FormatUtil.formatAmount(totalCr.subtract(totalDr)));

		List<String> mLabels = new ArrayList<>(byMonth.keySet());
		List<Double> mDebits = new ArrayList<>();
		List<Double> mCredits = new ArrayList<>();
		for (String k : mLabels) {
			mDebits.add(byMonth.get(k)[0]);
			mCredits.add(byMonth.get(k)[1]);
		}
		dto.setMonthLabels(mLabels);
		dto.setMonthDebits(mDebits);
		dto.setMonthCredits(mCredits);

		List<Company> years = companyRepo.findCompanyYearsByName(cs.getCompanyName(), uid);
		years.sort(Comparator.comparing(c -> c.getCompYear() != null ? c.getCompYear() : ""));
		List<String> yLabels = new ArrayList<>();
		List<Double> yDebits = new ArrayList<>();
		List<Double> yCredits = new ArrayList<>();

		for (Company c : years) {
			String yf = c.getFromDate() != null ? c.getFromDate().toLocalDate().format(FormatUtil.ISO_DATE)
					: "1900-01-01";
			String yt = c.getToDate() != null ? c.getToDate().toLocalDate().format(FormatUtil.ISO_DATE)
					: "2999-12-31";
			List<Daybook> yEntries = daybookRepo.findDaybookRange(yf, yt, uid, c.getCompanyid());
			double yd = 0.0;
			double yc = 0.0;
			if (yEntries != null) {
				for (Daybook d : yEntries) {
					yd += FormatUtil.nvlDouble(d.getDrAmt());
					yc += FormatUtil.nvlDouble(d.getCrAmt());
				}
			}
			yLabels.add(c.getCompYear() != null ? c.getCompYear() : String.valueOf(c.getCompanyid()));
			yDebits.add(yd);
			yCredits.add(yc);
		}
		dto.setYearLabels(yLabels);
		dto.setYearDebits(yDebits);
		dto.setYearCredits(yCredits);

		List<Daybook> sorted = new ArrayList<>(entries);
		sorted.sort((a, b) -> {
			String da = a.getDate() != null ? a.getDate() : "";
			String db = b.getDate() != null ? b.getDate() : "";
			return db.compareTo(da);
		});
		dto.setRecentEntries(sorted.size() > 8 ? sorted.subList(0, 8) : sorted);

		return dto;
	}
}
