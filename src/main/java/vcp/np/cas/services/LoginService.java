package vcp.np.cas.services;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.repositories.custom.CustomQueries;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Constants.Error.Message;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

@Service
public class LoginService {


	@Autowired
	public CommonService commonService;

	@Autowired
	public AuthenticationService authenticationService;

	@Autowired
	public JwtTokenService jwtTokenService;

	@Autowired
	public CustomQueries customQueries;
	
	
	public Map<String, Object> loginUsingCredentials(String hostUrl, Long clientId, Long serviceId, Long clientServiceId, String username, String password) throws Exception {
		Map<String, Object> loginData = new HashMap<String, Object>();
		
		Timestamp loginTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Logging in username: " + username + " on clientserviceId:" + clientServiceId + " using credential at " + loginTimeStamp);
		
    	// Verifying user's existence in the database
    	User user = commonService.fetchTheUserFromDb(username);
    	if (user == null) {
    		loginData.put(Error.Message.KEY, Message.CREDENTIAL_IS_NOT_AUTHENTIC);
    		return loginData;
    	}
    	Long userId = user.getId();
    	
    	// Verifying user's credentials
    	if (!authenticationService.doesCredentialMatch(user, password)) {
    		loginData.put(Error.Message.KEY, Message.CREDENTIAL_IS_NOT_AUTHENTIC);
    		return loginData;
    	}
    	
    	// Verifying user's access on the application
    	UserClientService userClientService = commonService.getUserClientService(userId, clientServiceId);
    	if (userClientService == null) {
    		loginData.put(Error.Message.KEY, Message.CREDENTIAL_IS_NOT_AUTHENTIC);
    		return loginData;
    	}
    	
    	if (!userClientService.isActive()) {
    		Integer daysOfInactivity = customQueries.getUserInactivityDays(userId, clientId, serviceId, loginTimeStamp);
        	if (daysOfInactivity != 0) {
        		loginData.put(Error.Message.KEY, "Your account has been deactivated due to inactivity in " + daysOfInactivity + " day(s).");
        		return loginData;
        	}
    	}
    	
    	Integer daysSinceLastPasswordChange = customQueries.getDaysSinceLastPasswordChange(userId, clientId, loginTimeStamp);
    	if (daysSinceLastPasswordChange != 0) {
    		loginData.put(Constants.IS_PASSWORD_EXPIRED, true);
    		
    		Map<String, Object> extraData = new HashMap<String, Object>();
    		extraData.put(Constants.JwtToken.DAYS_SINCE_LAST_PASSWORD_CHANGE, daysSinceLastPasswordChange);
    		
    		String jwtToken = jwtTokenService.generateToken(JwtTokenPurpose.PASSWORD_RESET, hostUrl, userClientService, extraData);
        	if (jwtToken == null) throw new Exception("Could not generate jwt token");
      
    		loginData.put(Constants.JwtToken.KEY, jwtToken);
    		return loginData;
    	}
    	
    	// Generating JWT token for user to access the respective client's application
    	String jwtToken = jwtTokenService.generateToken(JwtTokenPurpose.LOGIN_SUCCESSFUL, hostUrl, userClientService, null);
    	if (jwtToken == null) throw new Exception("Could not generate jwt token");
		loginData.put(Constants.JwtToken.KEY, jwtToken);
    	
    	
    	customQueries.updateTheUserLoginTime(userId, clientId, serviceId, loginTimeStamp);
    	
    	
    	loginData.put(Constants.REQUEST_HOST, userClientService.getClientService().getRequestHost());
    	loginData.put(Constants.JwtToken.KEY, jwtToken);
    	loginData.put(Constants.LOGIN_SUCCESS_PATH, commonService.getLoginSuccessPathOfService(serviceId));
    	loginData.put(Constants.IS_LOGIN_SUCCESSFUL, true);
    	
    	System.out.println("Login success for user[id:" + user.getId() + ", username:" + user.getUsername() + "] on client-service[id:" + clientServiceId + ", requestHost:" + userClientService.getClientService().getRequestHost() + "]");

		return loginData;
	}

}
