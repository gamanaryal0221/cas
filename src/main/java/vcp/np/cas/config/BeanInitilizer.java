package vcp.np.cas.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import vcp.np.cas.profile.Profile;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.services.email.MailCredential;



@Configuration
@Primary
public class BeanInitilizer {
    
	private final Profile profile;


    public BeanInitilizer(Profile profile) throws Exception {
    	System.out.println("\n:::::::::: Initializing bean creation ::::::::::");

		this.profile = profile;
	}
    
	@Bean
    JwtTokenService jwtTokenService() throws Exception {
        return JwtTokenConfigLoader.configure(profile);
    }

	@Bean
	MailCredential emailConfig() throws Exception {
		return MailConfigLoader.configure(profile);
	}

}
