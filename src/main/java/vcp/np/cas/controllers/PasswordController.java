package vcp.np.cas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.cas.domains.ClientService;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.JwtTokenService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Constants.JwtToken;
import vcp.np.cas.utils.Constants.Error;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

@Controller
@RequestMapping("/password")
public class PasswordController {
	

	@Autowired
	private CommonService commonService;

	@Autowired
	private JwtTokenService jwtTokenService;
	
//	@Autowired
//	private PasswordService passwordService;
	

    @GetMapping("/forgot")
    public String showForgotPasswordPage(HttpServletRequest request, Model model) {
    	
        model.addAllAttributes(Helper.getAllModelAndView(request).getModel());
        
        if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
        
        return Constants.Templates.FORGOT_PASSWORD;
    }
   

    @GetMapping("/reset")
    public String showResetPasswordPage(HttpServletRequest request, Model model) {
    	
    	try {
    		
    		String jwtToken = request.getParameter(Constants.JwtToken.KEY);
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
        	}
        	
        	Claims claims = jwtTokenService.parseToken(jwtToken);
    		System.out.println("claims: " + claims);
    		if (claims == null) {
        		model.addAllAttributes(Helper.error(Error.Title.TECHNICAL_ERROR, Error.Message.SMTH_WENT_WRONG));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}
    		    		
    		String purpose = (String) claims.get(JwtToken.PURPOSE);
    		if (!List.of(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()).contains(purpose)) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}
    		
    		if (JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode().equals(purpose)) {
    			model.addAttribute(Constants.PAGE_DESCRIPTION, "Your password has been expired since it was last changed " + claims.get(JwtToken.DAYS_SINCE_LAST_PASSWORD_CHANGE) + " day(s) earlier.<br>Please change your password to proceed.");
    		}else {
    			model.addAttribute(Constants.PAGE_DESCRIPTION, "Please create a new password");
    		}
    		
    		String requestHost = (String) claims.get(JwtToken.REQUEST_HOST);
    		String hostUrl = (String) claims.get(JwtToken.HOST_URL);
    		ClientService clientService = (requestHost != null && !requestHost.isEmpty())? commonService.getClientServiceDetail(requestHost):null;
    		if (clientService == null) clientService = (hostUrl != null && !hostUrl.isEmpty())? commonService.getClientServiceDetail(Helper.parseUrl(hostUrl)):null;
    		if (clientService == null) {
        		model.addAllAttributes(Helper.error(Error.Title.MALFORMED_URL, Error.Message.TRY_WITH_VALID_URL));
        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
        		return Constants.Templates.ERROR;
    		}else {
    			model.addAttribute(Constants.JwtToken.KEY, jwtToken);
    			model.addAttribute(Constants.CLIENT_DISPLAY_NAME, clientService.getClient().getDisplayName());
        		model.addAllAttributes(commonService.getClientServiceTheme(clientService.getClient().getId(), clientService.getService().getId()));
                return Constants.Templates.PASSWORD_RESET;
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();
    		
    		model.addAllAttributes(Helper.error(Constants.Error.Title.TECHNICAL_ERROR, Constants.Error.Message.SMTH_WENT_WRONG));
    		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
    		return Constants.Templates.ERROR;
    	}
    	
    	
    }
    

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestParam String jwtToken, @RequestParam String password, @RequestParam String confirmPassword, Model model) {
    	
    	try {
    		
    		System.out.println("jwtToken: " + jwtToken);
        	if (jwtToken == null || jwtToken.isEmpty()) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.COULD_NOT_PROCESS);
        	}
        	
        	Claims claims = jwtTokenService.parseToken(jwtToken);
    		System.out.println("claims: " + claims);
    		if (claims == null) {
        		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.Message.SMTH_WENT_WRONG);
    		}
    		    		
    		String purpose = (String) claims.get(JwtToken.PURPOSE);
    		if (!List.of(JwtTokenPurpose.FORCED_PASSWORD_RESET.getCode(), JwtTokenPurpose.PASSWORD_RESET.getCode()).contains(purpose)) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.COULD_NOT_PROCESS);
    		}
    		
    		String requestHost = (String) claims.get(JwtToken.REQUEST_HOST);
    		String hostUrl = (String) claims.get(JwtToken.HOST_URL);
    		ClientService clientService = (requestHost != null && !requestHost.isEmpty())? commonService.getClientServiceDetail(requestHost):null;
    		if (clientService == null) clientService = (hostUrl != null && !hostUrl.isEmpty())? commonService.getClientServiceDetail(Helper.parseUrl(hostUrl)):null;
    		if (clientService == null) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.COULD_NOT_PROCESS);
    		}else {
    			
    			if (password == null || confirmPassword == null || password.isEmpty() || confirmPassword.isEmpty()) {
    				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("All fields are mandatory");
    			}else if (!password.equals(confirmPassword)) {
    	            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Passwords do not match");
    	        }else {
                    return ResponseEntity.ok("Seems good till now");
    	        }
    			
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();

    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.Message.SMTH_WENT_WRONG);
    	}
    	
    	
    }
    

    @PostMapping("/forgot")
    public String forgotPassword(HttpServletRequest request, @RequestParam String username, Model model, HttpServletResponse httpServletResponse) {
		return null;
    }


}
