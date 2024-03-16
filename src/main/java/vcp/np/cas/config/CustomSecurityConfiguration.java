package vcp.np.cas.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import vcp.np.cas.interceptors.RequestInterceptor;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.email.MailConfig;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class CustomSecurityConfiguration extends WebMvcConfigurationSupport {

	private final Map<String, Object> externalConfig;
	private final CommonService commonService;
	

    public CustomSecurityConfiguration(Profile profile, CommonService commonService) {
		System.out.println(getCasPrintLog());
    	System.out.println("############# Initializing custom configuration #############");

		externalConfig = ExternalConfigLoader.load(profile);

		this.commonService = commonService;
	}

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeRequests(authorizeRequests -> 
				authorizeRequests.requestMatchers(
						"/login",
						"/password/**",
						"/static/**",
						"/js/**",
						"/css/**"
						)
				.permitAll()
				.anyRequest()
				.authenticated()
			)
			.logout(logout -> 
				logout.invalidateHttpSession(true)
				.clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login?logOutSuccess=true")
				.permitAll()
			);

		return http.build();
	}

	
    @Bean
    RequestInterceptor requestInterceptor() {
        return new RequestInterceptor(commonService);
    }

    @Override
	protected
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor());
    }
	

    @SuppressWarnings("unchecked")
	@Bean
    JwtTokenService jwtTokenService() throws Exception {
        return JwtTokenConfiguration.configure((Map<String, Object>) externalConfig.get(Constants.Config.JWT_TOKEN));
    }

	@SuppressWarnings("unchecked")
	@Bean
	MailConfig emailConfig() throws Exception {
		return EmailConfigLoader.configure((Map<String, Object>) externalConfig.get(Constants.Config.MAIL));
	}

	private String getCasPrintLog() {
		return ""
		+ "    ____          ___         _ _ _ _ \n"
		+ "   / ___|        / _ \\       /  _ _ _|\n"
		+ "  | (           / / \\ \\      \\ (____\n"
		+ "  | |     --   / /   \\ \\  --  \\____ \\\n"
		+ "  | (___      /  =====  \\     _ _ _) \\\n"
		+ "   \\____|    /_/       \\_\\   |_ _ _ _/\n"
		+ "";
	}
}
