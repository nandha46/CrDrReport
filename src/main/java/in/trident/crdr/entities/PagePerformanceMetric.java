package in.trident.crdr.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "page_performance_metrics", indexes = {
		@Index(name = "idx_perf_path_recorded", columnList = "request_path, recorded_at"),
		@Index(name = "idx_perf_tag", columnList = "optimization_tag") })
public class PagePerformanceMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request_path", nullable = false, length = 160)
	private String requestPath;

	@Column(name = "http_method", nullable = false, length = 10)
	private String httpMethod;

	@Column(name = "status_code", nullable = false)
	private int statusCode;

	@Column(name = "duration_ms", nullable = false)
	private long durationMs;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "company_id")
	private Long companyId;

	@Column(name = "optimization_tag", length = 64)
	private String optimizationTag;

	@Column(name = "recorded_at", nullable = false)
	private LocalDateTime recordedAt;

	public PagePerformanceMetric() {
	}

	public PagePerformanceMetric(String requestPath, String httpMethod, int statusCode, long durationMs, Long userId,
			Long companyId, String optimizationTag, LocalDateTime recordedAt) {
		this.requestPath = requestPath;
		this.httpMethod = httpMethod;
		this.statusCode = statusCode;
		this.durationMs = durationMs;
		this.userId = userId;
		this.companyId = companyId;
		this.optimizationTag = optimizationTag;
		this.recordedAt = recordedAt;
	}

	public Long getId() {
		return id;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public long getDurationMs() {
		return durationMs;
	}

	public void setDurationMs(long durationMs) {
		this.durationMs = durationMs;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getOptimizationTag() {
		return optimizationTag;
	}

	public void setOptimizationTag(String optimizationTag) {
		this.optimizationTag = optimizationTag;
	}

	public LocalDateTime getRecordedAt() {
		return recordedAt;
	}

	public void setRecordedAt(LocalDateTime recordedAt) {
		this.recordedAt = recordedAt;
	}
}
