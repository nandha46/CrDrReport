package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.PagePerformanceMetric;

@Repository
public interface PagePerformanceMetricRepo extends JpaRepository<PagePerformanceMetric, Long> {

	List<PagePerformanceMetric> findTop50ByOrderByRecordedAtDesc();

	@Query("select m.requestPath, m.httpMethod, m.optimizationTag, count(m), avg(m.durationMs), min(m.durationMs), max(m.durationMs) "
			+ "from PagePerformanceMetric m group by m.requestPath, m.httpMethod, m.optimizationTag "
			+ "order by avg(m.durationMs) desc")
	List<Object[]> summarizeByPathAndTag();
}
