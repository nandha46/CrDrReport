package in.trident.crdr.config;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import in.trident.crdr.entities.PagePerformanceMetric;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.PagePerformanceMetricRepo;
import in.trident.crdr.services.CustomUserDetails;

@Component
public class PerformanceTrackingInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTrackingInterceptor.class);
	private static final String START_TIME_ATTR = "crdrPerfStartNs";

	private final PagePerformanceMetricRepo metricRepo;
	private final CompSelectionRepo compSelectionRepo;

	@Value("${crdr.performance.tag:v0.0.14-optimized}")
	private String optimizationTag;

	public PerformanceTrackingInterceptor(PagePerformanceMetricRepo metricRepo, CompSelectionRepo compSelectionRepo) {
		this.metricRepo = metricRepo;
		this.compSelectionRepo = compSelectionRepo;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		request.setAttribute(START_TIME_ATTR, System.nanoTime());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		Object startObj = request.getAttribute(START_TIME_ATTR);
		if (!(startObj instanceof Long)) {
			return;
		}
		String path = request.getRequestURI();
		if (path == null || path.startsWith("/css/") || path.startsWith("/script/") || path.startsWith("/img/")
				|| path.startsWith("/h2-console") || "/favicon.ico".equals(path) || "/error".equals(path)) {
			return;
		}

		long durationMs = Math.max(0L, (System.nanoTime() - (Long) startObj) / 1_000_000L);
		Long userId = null;
		Long companyId = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
			userId = ((CustomUserDetails) auth.getPrincipal()).getId();
			try {
				companyId = compSelectionRepo.findCompanyIdByUserId(userId);
			} catch (Exception ignored) {
				// Keep metric recording non-fatal
			}
		}

		try {
			metricRepo.save(new PagePerformanceMetric(path, request.getMethod(), response.getStatus(), durationMs,
					userId, companyId, optimizationTag, LocalDateTime.now()));
		} catch (Exception e) {
			LOGGER.debug("Skipping performance metric persistence for {}: {}", path, e.getMessage());
		}
	}
}
