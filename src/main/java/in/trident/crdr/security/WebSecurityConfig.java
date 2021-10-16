package in.trident.crdr.security;

/**
 * @author Nandhakumar Subramanian
 * 
 * @since 
 * @version 0.0.1
 * 
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import in.trident.crdr.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
	
	@Override
	protected void configure (AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
			.antMatchers("/register","/process_register","/","/css/**").permitAll()
			.antMatchers("/edit/**").hasAnyAuthority("admin")
			.antMatchers("/daybooks","/ledger","/trial").hasAnyAuthority("developer")
			.antMatchers("/findDaybook","/findLedger","/findTrialBal","/findTradingPL","/findBalSheet","/reports").hasAnyAuthority("developer")
			.antMatchers("/users").hasAnyAuthority("developer")
			.antMatchers("/delete/**").hasAnyAuthority("developer","client")
			.anyRequest().authenticated()
			.and()
			.formLogin().usernameParameter("email").defaultSuccessUrl("/").permitAll()
			.and()
			.logout().logoutSuccessUrl("/").permitAll()
			.and()
			.exceptionHandling().accessDeniedPage("/403")
			;  
	}
}
