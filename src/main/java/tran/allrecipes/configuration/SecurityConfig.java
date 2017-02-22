package tran.allrecipes.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import tran.allrecipes.service.UserDetailsServiceImpl;
import tran.allrecipes.service.UtilityServiceImpl;

/**
 * @author Todd
 * A class implementing spring security instead of using XML configuration(s).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	/** number of seconds in one day*/
	private static final int SECONDS_IN_ONE_DAY = 60 * 60 * 24;
	/** the number of days for the remember-me token. */
	private static final int REMEMBER_ME_TOKEN_DAYS = 14;
	/** The duration of the remember-me token. */
	private final int tokenValiditySeconds = SECONDS_IN_ONE_DAY * REMEMBER_ME_TOKEN_DAYS;
	/** the remember me cookie name. */
	private static final String REMEMBER_ME_TOKEN_NAME = "AR-Remember-Me-Cookie";
	/** the remember me cookie name in the signin form. */
	private static final String REMEMBER_ME_PARAMETER_NAME = "AR-remember-me";
	/** number of logarithmic rounds to apply while hashing a password. */
	private static final int NUMBER_LOG_ROUNDS = 14;
	
	/** Field to gain access to the user details service to verify a user's authentication information. */
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	/** 
	 * Allows for the authentication manager to have global scope. 
	 * @param auth An object for in-memory authentication of the UserDetailsService.
	 * */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	/**
	 * @param http An object to allow modify security related content.
	 * Assigns specific security settings to different URL(s).
	 */
	@Override
	protected void configure(HttpSecurity http) {
		try {
			http.
			formLogin().
			loginProcessingUrl("/login").
			loginPage("/?message=you must be authenticated to view this page.").
			defaultSuccessUrl("/").
			failureUrl("/signinError").
			and().
			rememberMe().tokenRepository(persistentTokenRepository()).
			tokenValiditySeconds(tokenValiditySeconds).
			rememberMeParameter(REMEMBER_ME_PARAMETER_NAME).
			rememberMeCookieName(REMEMBER_ME_TOKEN_NAME);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error related to web security configuration.");
		}
	}
	
	/**
	 * @param auth An object to modify authentication contents.
	 * Adds an authentication provider.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		try {
			DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
			provider.setPasswordEncoder(new BCryptPasswordEncoder(NUMBER_LOG_ROUNDS));
			provider.setUserDetailsService(userDetailsService);
			auth.authenticationProvider(provider);
		}
		catch(Exception e) {
			System.out.println("error setting custom authentication provider");
		}
	}
	
	/**
	 * @return An object to gain access to the persistent_logins table and add in the persistent token.
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		UtilityServiceImpl utilityService = new UtilityServiceImpl();
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = utilityService.getRepositoryTokenWithDataSource();
		return jdbcTokenRepositoryImpl;
	}
}
