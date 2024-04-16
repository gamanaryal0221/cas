package vcp.np.cas.config.interceptor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import vcp.np.cas.interceptors.RequestInterceptor;


@Configuration
@Primary
public class InterceptorsInitilizer extends WebMvcConfigurationSupport {

    public InterceptorsInitilizer() {
        super();
        System.out.println("\n:::::::::: Initializing interceptors ::::::::::");
    }
    
    @Bean
    RequestInterceptor requestInterceptor() {
		return new RequestInterceptor();
    }

    @Override
	protected
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor());
    }
    
}
