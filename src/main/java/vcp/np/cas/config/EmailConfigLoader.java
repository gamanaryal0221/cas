package vcp.np.cas.config;

import java.util.HashMap;
import java.util.Map;

import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.email.MailConfig;

public class EmailConfigLoader {
    
    public static MailConfig configure(Map<String, Object> mailConfig) throws Exception {
        System.out.println("\n############# Reading email configuration #############");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> senderDetail = (Map<String, Object>) mailConfig.getOrDefault(Constants.Config.SENDER, new HashMap<String, Object>());
            
        String address = (String) senderDetail.getOrDefault(Constants.Config.ADDRESS, "");
        if (address.isEmpty()) throw new Exception("Not found: Sender mail address");

        String password = (String) senderDetail.getOrDefault(Constants.Config.PASSWORD, "");
        if (password.isEmpty()) throw new Exception("Not found: Password of mail sender");
            
        return new MailConfig(address, password);
    }
}
