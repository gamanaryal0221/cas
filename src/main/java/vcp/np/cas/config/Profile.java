package vcp.np.cas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Profile {
	
    @Autowired
    private Environment environment;
    

    public String getEnvironmentName() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return activeProfiles[0];
        }else{
            System.out.println("Not found: environment name");
            return null;
        }
    }


	public Environment getEnvironment() {
		return environment;
    }

    
    public Object getProperty(String key) {
        if (key != null && !key.isEmpty()) {
            return environment.getProperty(key);
        }else{
            System.out.println("Invalid key: " + key);
            return null;
        }
    }

    public Object getProperty(String key, String defaultValue) {
        if (key != null && !key.isEmpty()) {
            return environment.getProperty(key, defaultValue);
        }else{
            System.out.println("Invalid key: " + key);
            return null;
        }
    }
    
}
