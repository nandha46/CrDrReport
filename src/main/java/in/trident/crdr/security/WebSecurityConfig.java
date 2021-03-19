package in.trident.crdr.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private DataSource dataSource;

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
	/*	http.authorizeRequests()
			.antMatchers("/users").authenticated().anyRequest().permitAll()
				.and().formLogin().usernameParameter("email").defaultSuccessUrl("/users").permitAll()
					.and().logout().logoutSuccessUrl("/").permitAll();	*/
		http.authorizeRequests()
			.antMatchers("/").hasAnyAuthority("developer","user","admin","client")
			.antMatchers("/register").permitAll()
			.antMatchers("/edit/**").hasAnyAuthority("ADMIN")
			.antMatchers("/daybooks").hasAnyAuthority("developer")
			.antMatchers("/findDaybook").hasAnyAuthority("developer")
			.antMatchers("/findLedger").hasAnyAuthority("developer")
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
