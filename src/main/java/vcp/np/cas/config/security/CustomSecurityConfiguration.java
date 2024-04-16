package vcp.np.cas.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Primary
public class CustomSecurityConfiguration {

	@SuppressWarnings("deprecation")
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("\n:::::::::: Configuring security ::::::::::");

        // http.authorizeRequests(auth -> auth
        // 		.requestMatchers(
        // 				"/login",
        // 				"/password/**",
        // 				"/static/**",
        // 				"/js/**",
        // 				"/css/**")
        // 		.permitAll()
        // 		.anyRequest()
        // 		.authenticated())
        // 		.logout(logout -> logout
        // 				.invalidateHttpSession(true)
        // 				.clearAuthentication(true)
        // 				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        // 				.logoutSuccessUrl("/login?logOutSuccess=true")
        // 				.permitAll());


        http
                .authorizeRequests(requests -> requests
						.requestMatchers("/login").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll() // Allow access to CSS and JS files without authentication
                        .anyRequest().authenticated())
                // .formLogin(login -> login
                //         .loginPage("/login")
                //         .permitAll())
                .logout(logout -> logout
                        .permitAll());

		return http.build();
	}

}
