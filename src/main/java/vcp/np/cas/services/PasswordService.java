package vcp.np.cas.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Constants.Error.Message;

@Service
public class PasswordService {


	@Autowired
	public CommonService commonService;

	@Autowired
	public AuthenticationService authenticationService;
//
//	@Autowired
//	public AuthenticationService authenticationService;
//
//	@Autowired
//	public JwtTokenService jwtTokenService;
	


	public Map<String, Object> resetPassword(Claims claims, Long clientServiceId, String rawPassword) {
		Map<String, Object> resetPasswordData = new HashMap<String, Object>();

    	// Verifying user's existence in the database
		String username = claims.getSubject();
    	User user = (username != null && !username.isEmpty())? commonService.fetchTheUserFromDb(username):null;
    	if (user == null) {
    		resetPasswordData.put(Error.Message.KEY, Message.REQUEST_IS_INVALID);
    		return resetPasswordData;
    	}
    	Long userId = user.getId();
    	
    	// Verifying user's access on the application
    	UserClientService userClientService = commonService.getUserClientService(userId, clientServiceId);
    	if (userClientService == null) {
    		resetPasswordData.put(Error.Message.KEY, Message.REQUEST_IS_INVALID);
    		return resetPasswordData;
    	}
    	
    	
    	// Checking if the password is same
    	if (!authenticationService.doesItMatchWithOldPassword(user, rawPassword)) {
    		resetPasswordData.put(Error.Message.KEY, Message.CAN_NOT_SET_OLD_PASSWORD);
    		return resetPasswordData;
    	}
    	
    	resetPasswordData.put(Constants.IS_PASSWORD_RESET_SUCCESSFUL, true);
		return resetPasswordData;
	}

}
