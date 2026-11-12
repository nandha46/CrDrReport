package in.trident.crdr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final PerformanceTrackingInterceptor performanceTrackingInterceptor;

	public WebMvcConfig(PerformanceTrackingInterceptor performanceTrackingInterceptor) {
		this.performanceTrackingInterceptor = performanceTrackingInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(performanceTrackingInterceptor).excludePathPatterns("/css/**", "/script/**", "/img/**",
				"/h2-console/**", "/favicon.ico");
	}
}
