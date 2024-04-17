package vcp.np.cas.cas.login;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.datasource.usermanagement.domains.UserClientService;
import vcp.np.cas.services.AuthenticationService;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.PlainSqlQueries;
import vcp.np.cas.services.JwtTokenService;
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
	public PlainSqlQueries plainSqlQueries;
	
	
	private final JwtTokenService jwtTokenService;
	
	    
    public LoginService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }
	
	
	public Map<String, Object> loginUsingCredentials(StringBuffer casFullUrl, String hostUrl, Long clientId, Long serviceId, Long clientServiceId, String username, String password) throws Exception {
		Map<String, Object> loginData = new HashMap<String, Object>();
		
		Timestamp loginTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Logging in user[username:'" + username + "'] on clientserviceId:" + clientServiceId + " using credential at " + loginTimeStamp);
		
		
    	// Verifying user's existence and access in the database
		UserClientService userClientService = commonService.getUserClientServiceByCredential(username, clientServiceId);
    	if (userClientService == null) {
    		loginData.put(Error.Message.KEY, Message.CREDENTIAL_IS_NOT_AUTHENTIC);
    		return loginData;
    	}
    	
    	// Verifying user's credentials
    	if (!authenticationService.doesCredentialMatch(userClientService.getUser(), password)) {
    		loginData.put(Error.Message.KEY, Message.CREDENTIAL_IS_NOT_AUTHENTIC);
    		return loginData;
    	}
    	
    	Long userId = userClientService.getUser().getId();
    	// Verifying user's inactivity
    	if (!userClientService.isActive()) {
    		Integer daysOfInactivity = plainSqlQueries.getUserInactivityDays(userId, clientId, serviceId, loginTimeStamp);
        	if (daysOfInactivity != 0) {
        		loginData.put(Error.Message.KEY, "Your account has been deactivated due to inactivity in " + daysOfInactivity + " day(s).");
        		return loginData;
        	}
    	}
    	
    	// Verifying user's password expiration
    	Integer daysSinceLastPasswordChange = plainSqlQueries.getDaysSinceLastPasswordChange(userId, clientId, loginTimeStamp);
    	if (daysSinceLastPasswordChange != 0) {
    		loginData.put(Constants.IS_PASSWORD_EXPIRED, true);
    		
    		Map<String, Object> extraData = new HashMap<String, Object>();
    		extraData.put(Constants.JwtToken.DAYS_SINCE_LAST_PASSWORD_CHANGE, daysSinceLastPasswordChange);
    		
        	// Generating JWT token for user to reset password
    		Map<String, Object> jwtTokenData = jwtTokenService.generateToken(JwtTokenPurpose.CHANGE_PASSWORD, casFullUrl, hostUrl, userClientService, extraData);
			if (jwtTokenData == null) throw new Exception("Could not generate jwt token");
      
    		loginData.put(Constants.JwtToken.KEY, jwtTokenData.getOrDefault(Constants.JwtToken.KEY, ""));
    		return loginData;
    	}
    	
    	// Generating JWT token for user to access the respective client's application
    	Map<String, Object> jwtTokenData = jwtTokenService.generateToken(JwtTokenPurpose.LOGIN_SUCCESSFUL, casFullUrl, hostUrl, userClientService, null);
		if (jwtTokenData == null) throw new Exception("Could not generate jwt token");
		loginData.put(Constants.JwtToken.KEY, jwtTokenData.getOrDefault(Constants.JwtToken.KEY, ""));
    	
	
    	plainSqlQueries.updateTheUserLoginTime(userId, clientId, serviceId, loginTimeStamp);
    	
    	loginData.put(Constants.REQUEST_HOST, userClientService.getClientService().getRequestHost());
    	loginData.put(Constants.LOGIN_SUCCESS_PATH, commonService.getLoginSuccessPathOfService(serviceId));
    	loginData.put(Constants.IS_LOGIN_SUCCESSFUL, true);
    	
    	System.out.println("Login success for user[id:" + userId + ", username:" + username + "] on client-service[id:" + clientServiceId + ", requestHost:" + userClientService.getClientService().getRequestHost() + "]");

		return loginData;
	}

}
