package vcp.np.cas.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.repositories.custom.CustomQueries;
import vcp.np.cas.services.AuthenticationService.PasswordDetails;
import vcp.np.cas.utils.Constants;
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
	
	
	public Map<String, Object> loginUsingCredentials(Long clientId, Long serviceId, Long clientServiceId, String username, String password) throws Exception {
		Map<String, Object> loginData = new HashMap<String, Object>();
		
		Timestamp loginTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Logging in username: " + username + " on clientserviceId:" + clientServiceId + " using credential at " + loginTimeStamp);
		
    	// Verifying user's existence in the database
    	User user = commonService.fetchTheUserFromDb(username);
    	if (user == null) {
    		loginData.put(Constants.ERROR_MESSAGE, "Provided credentials is not determined to be authentic.");
    		return loginData;
    	}
    	Long userId = user.getId();
    	
    	// Verifying user's credentials
    	if (!doesCredentialMatch(user, password)) {
    		loginData.put(Constants.ERROR_MESSAGE, "Provided credentials is not determined to be authentic.");
    		return loginData;
    	}
    	
    	// Verifying user's access on the application
    	UserClientService userClientService = commonService.getUserClientService(userId, clientServiceId);
    	System.out.println("Does user[id" + userId + "] have access on client-service[id:" + clientServiceId + "]?\n >> " + (userClientService != null));
    	if (userClientService == null) {
    		loginData.put(Constants.ERROR_MESSAGE, "Provided credentials is not determined to be authentic.");
    		return loginData;
    	}
    	
    	if (!userClientService.isActive()) {
    		Integer daysOfInactivity = customQueries.getUserInactivityDays(userId, clientId, serviceId, loginTimeStamp);
        	if (daysOfInactivity != 0) {
        		loginData.put(Constants.ERROR_MESSAGE, "Your account has been deactivated due to inactivity in " + daysOfInactivity + " day(s).");
        		return loginData;
        	}
    	}
    	
    	if (customQueries.isUserPasswordExpired(userId, clientId, loginTimeStamp)) {
    		loginData.put(Constants.IS_PASSWORD_EXPIRED, true);
    		return loginData;

    	}
    	
    	// Generating JWT token for user to access the respective client's application
    	String jwtToken = jwtTokenService.generateToken(JwtTokenPurpose.LOGIN_SUCCESSFUL, userClientService);
    	System.out.println(jwtToken);
    	if (jwtToken == null || jwtToken.isEmpty()) {
    		throw new Exception("Could not generate jwt token for user[id: " + userId + "] on client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]");
    	}
    	
    	
    	customQueries.updateTheUserLoginTime(userId, clientId, serviceId, loginTimeStamp);
    	
    	
    	loginData.put(Constants.REQUEST_HOST, userClientService.getClientService().getRequestHost());
    	loginData.put(Constants.JwtToken.KEY, jwtToken);
    	loginData.put(Constants.LOGIN_SUCCESS_PATH, commonService.getLoginSuccessPathOfService(serviceId));
    	loginData.put(Constants.IS_LOGIN_SUCCESSFUL, true);
    	
    	System.out.println("Login success for user[id:" + user.getId() + ", username:" + user.getUsername() + "] on client-service[id:" + clientServiceId + ", requestHost:" + userClientService.getClientService().getRequestHost() + "]");

		return loginData;
	}
    
    public boolean doesCredentialMatch(User user, String rawPassword) {
    	boolean isAuthenticationSuccessful = false;
    	
    	if (user != null && rawPassword != null && !rawPassword.isEmpty()) {
    		
    		PasswordDetails passwordDetails = new PasswordDetails(user.getSaltValue(), user.getPassword());
    		isAuthenticationSuccessful = authenticationService.isPasswordCorrect(rawPassword, passwordDetails);
    		
    	}
    	System.out.println("Is credential matched for user[id:" + user.getId() + "]?\n >> "+ isAuthenticationSuccessful);
		return isAuthenticationSuccessful;
    	
    }

}
