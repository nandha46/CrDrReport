package in.trident.crdr.security;

/**
 * @author Nandhakumar Subramanian
 * 
 * @since 
 * 
 * @version 0.0.1
 * 
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import in.trident.crdr.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		final String ADMIN = "admin";
		final String DEVELOPER = "developer";
		final String USER = "user";

		String[] preLoginPaths = new String[] { "/", "/login", "/403", "/error", "/css/**", "/img/**", "/script/**" };
		String[] findPaths = new String[] { "/findDaybook", "/findLedger", "/findTrialBal", "/findTradingPL",
				"/findBalSheet" };
		String[] viewPaths = new String[] { "/daybooks", "/ledger", "/pdfLedger", "/trial", "/tradingPL",
				"/BalanceSheet" };
		String[] userPaths = new String[] { "/reports", "/upload", "/uploaded", "/uploadcsv", "/csvuploaded",
				"/profile", "/success", "/StoreCompany", "/company_selection", "/companyselect", "/delete_company",
				"/process_delete_company", "/api/v1/years" };
		String[] adminPaths = new String[] { "/delete_user", "/process_delete_user", "/disable_user", "/users",
				"/create_user", "/process_register", "/actuator/**", "/api/v1/**", "/h2-console/**" };

		http.authenticationProvider(authProvider())
				.csrf().ignoringAntMatchers("/h2-console/**")
				.and()
				.headers().frameOptions().sameOrigin()
				.and()
				.authorizeRequests()
				.antMatchers(preLoginPaths).permitAll()
				.antMatchers("/actuator/health", "/actuator/info").permitAll()
				.antMatchers(adminPaths).hasAnyAuthority(ADMIN, DEVELOPER)
				.antMatchers(findPaths).hasAnyAuthority(DEVELOPER, ADMIN, USER)
				.antMatchers(viewPaths).hasAnyAuthority(DEVELOPER, ADMIN, USER)
				.antMatchers(userPaths).hasAnyAuthority(DEVELOPER, ADMIN, USER)
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login").usernameParameter("email").passwordParameter("pass")
				.loginProcessingUrl("/processLogin").defaultSuccessUrl("/company_selection").permitAll()
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/403");

		return http.build();
	}
}
