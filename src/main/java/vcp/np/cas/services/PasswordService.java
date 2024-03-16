package vcp.np.cas.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.repositories.custom.CustomQueries;
import vcp.np.cas.services.AuthenticationService.PasswordDetails;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Constants.Error.Message;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.email.CustomMailSender;
import vcp.np.cas.utils.enums.JwtTokenPurpose;


@Service
public class PasswordService {

	
	@Autowired
	public CommonService commonService;

	@Autowired
	public AuthenticationService authenticationService;

	@Autowired
	public CustomQueries customQueries;

	@Autowired
	public CustomMailSender emailSender;

	private final JwtTokenService jwtTokenService;
	
	    
    public PasswordService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }


	public Map<String, Object> resetPassword(Claims claims, Long clientServiceId, String rawPassword) throws Exception {
		Map<String, Object> resetPasswordData = new HashMap<String, Object>();

		//Verifying the standard of password
    	if (!authenticationService.isPasswordValid(rawPassword)) {
    		resetPasswordData.put(Error.Message.KEY, Message.PASSWORD_DID_NOT_MEET_STANDARD);
    		return resetPasswordData;
    	}

    	// Verifying user's existence and access in the database
		Long userId = Long.valueOf(claims.getSubject());
		Timestamp passwordResetTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Resetting password of user[id:'" + (claims.getSubject()) + "'] at " + passwordResetTimeStamp);

    	UserClientService userClientService = commonService.getUserClientServiceById(userId, clientServiceId);
    	if (userClientService == null) {
    		resetPasswordData.put(Error.Message.KEY, Message.REQUEST_IS_INVALID);
    		return resetPasswordData;
    	}
    	User user = userClientService.getUser();
    	
    	// Checking if the password is same
    	if (authenticationService.doesItMatchWithOldPassword(user, rawPassword)) {
    		resetPasswordData.put(Error.Message.KEY, Message.CAN_NOT_SET_OLD_PASSWORD);
    		return resetPasswordData;
    	}
    	
    	// Updating user's password
    	PasswordDetails newPasswordDetails = authenticationService.makePassword(rawPassword);
    	if (!customQueries.isUserPasswordUpdated(userClientService.getUser(), newPasswordDetails, passwordResetTimeStamp)) {
    		resetPasswordData.put(Error.Message.KEY, Message.SMTH_WENT_WRONG);
    		return resetPasswordData;
    	}
    	
    	// Sending password reset success email
    	emailSender.trigerPasswordResetSuccessEmail((String) claims.getOrDefault(Constants.JwtToken.PURPOSE, ""), user.getMailAddress(), passwordResetTimeStamp);
    	
    	resetPasswordData.put(Constants.IS_PASSWORD_RESET_SUCCESSFUL, true);
		return resetPasswordData;
	}
	

	public Map<String, Object> forgotPassword(StringBuffer casFullUrl, String hostUrl, Long clientServiceId, String username) throws Exception {
		Map<String, Object> forgotPasswordData = new HashMap<String, Object>();

    	// Verifying user's existence and access in the database
		Timestamp forgotPasswordTimeStamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Validating forgot password request of user[username:'" + username + "'] at " + forgotPasswordTimeStamp);

    	UserClientService userClientService = commonService.getUserClientServiceByCredential(username, clientServiceId);
    	if (userClientService == null) {
    		forgotPasswordData.put(Error.Message.KEY, Message.USERNAME_IS_NOT_VALID);
    		return forgotPasswordData;
    	}
    	

    	// Generating JWT token for user to reset the password
    	Map<String, Object> jwtTokenData = jwtTokenService.generateToken(JwtTokenPurpose.PASSWORD_RESET, casFullUrl, hostUrl, userClientService, null);
		if (jwtTokenData == null) throw new Exception("Could not generate jwt token");
    	forgotPasswordData.put(Constants.JwtToken.KEY, jwtTokenData.getOrDefault(Constants.JwtToken.KEY, ""));
    	
    	Date expirationAt = (Date) jwtTokenData.get(Constants.JwtToken.EXPIRATION_AT);
    	jwtTokenData.remove(Constants.JwtToken.ISSUED_AT);
    	jwtTokenData.remove(Constants.JwtToken.EXPIRATION_AT);
    	String passwordResetUrl = Helper.getCustomizedFullCasUrl(casFullUrl, Constants.Request.Uri.PASSWORD_RESET, jwtTokenData);
    	
    	// Sending password reset request email
    	User user = userClientService.getUser();
    	emailSender.trigerForgotPasswordEmail(user.getMailAddress(), expirationAt, passwordResetUrl);
    	
    	forgotPasswordData.put(Constants.IS_FORGOT_PASSWORD_REQUEST_SUCCESSFUL, true);
		return forgotPasswordData;
	}

}
