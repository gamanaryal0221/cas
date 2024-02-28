package vcp.np.cas.controllers;

import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.cas.services.CommonService;
import vcp.np.cas.services.PasswordService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Helper;

@Controller
@RequestMapping("/password")
public class PasswordController {
	

	@Autowired
	private CommonService commonService;
	
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
    	
        model.addAllAttributes(Helper.getAllModelAndView(request).getModel());
        
        if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
        
        return Constants.Templates.RESET_PASSWORD;
    }
    

    @PostMapping("/forgot")
    public String forgotPassword(HttpServletRequest request, @RequestParam String username, Model model, HttpServletResponse httpServletResponse) {
		return null;
    	
//        ModelAndView modelAndView = Helper.getAllModelAndView(request);
//        Map<String, Object> modelAndViewMap = modelAndView.getModel();
//        model.addAllAttributes(modelAndViewMap);
//        model.addAttribute(Constants.USERNAME, username);
//        
//        Long clientId = (Long) modelAndViewMap.get(Constants.CLIENT_ID);
//        Long serviceId = (Long) modelAndViewMap.get(Constants.SERVICE_ID);
//        Long clientServiceId = (Long) modelAndViewMap.get(Constants.CLIENTSERVICE_ID);
//        
//        if (!Helper.isGenuineRequest(request) || clientId == null || serviceId == null) {
//        	if (clientId == null || serviceId == null) {
//        		model.addAllAttributes(Helper.error(null, null));
//        		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
//        	}
//        	return Constants.Templates.ERROR;
//        }
//        
//        try {
//
//            Map<String, Object> loginData = loginService.loginUsingCredentials(clientId, serviceId, clientServiceId, username, password);
//            
//            if ((boolean) loginData.getOrDefault(Constants.IS_LOGIN_SUCCESSFUL, false)) {
//            	
//            	URL url = Helper.parseUrl(request.getParameter(Constants.HOST_URL));
//            	
//            	String redirectionUrl = "";
//            	if (url != null) redirectionUrl = url.getProtocol().toString() + "://" + url.getHost().toString();
//            	
//            	String port = (url.getPort() != -1)? (String.valueOf(url.getPort())):"";
//            	if (!port.isEmpty()) redirectionUrl = redirectionUrl + ":" + port;
//            	
//            	if (redirectionUrl == null || redirectionUrl.isEmpty()) redirectionUrl = (String) loginData.get(Constants.HOST_URL);
//            	System.out.println("Redirecting to url: " + redirectionUrl);
//            	
//            	if (redirectionUrl == null || redirectionUrl.isEmpty()) {
//            		throw new Exception("Error encountered on making redirection url login success for username:" + username);
//            	}else {
//            		redirectionUrl = redirectionUrl + "?token=" + ((String) loginData.getOrDefault(Constants.JwtToken.KEY, ""));
//            	}
//            	
//            	httpServletResponse.sendRedirect(redirectionUrl);
//            	return null;
//            }else {
//        		throw new Exception("Could not login username:" + username);
//            }
//            
//        }catch(Exception e) {
//        	e.printStackTrace();
//
//    		model.addAllAttributes(Helper.error(null, null));
//    		model.addAllAttributes(commonService.getClientServiceTheme(null, null));
//        	return Constants.Templates.ERROR;
//        }
//        
    }


}
