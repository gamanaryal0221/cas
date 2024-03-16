package vcp.np.cas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import vcp.np.cas.utils.Constants.Environment;

@SpringBootApplication
@EnableAsync
public class CasApplication {

	public static void main(String[] args) {

        String definedEnvironment = "";
        try {
            definedEnvironment = System.getenv(Environment.KEY);
            System.out.println("Defined environment: " + definedEnvironment);
        }catch (Exception e) {
            System.out.println("Could not read environment");
            e.printStackTrace();
        }

        if (definedEnvironment == null || definedEnvironment.isEmpty()) definedEnvironment = Environment.DEV;
        definedEnvironment = definedEnvironment.toLowerCase();

        if (!Environment.isValid(definedEnvironment)) {
            throw new IllegalStateException("Invalid environment defined");
        }

        System.out.println("Active environment: " + definedEnvironment);
        SpringApplication app = new SpringApplication(CasApplication.class);
        app.setAdditionalProfiles(definedEnvironment);
        app.run(args);
	}

}
