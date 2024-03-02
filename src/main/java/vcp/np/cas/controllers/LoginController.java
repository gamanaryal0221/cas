package vcp.np.cas.controllers;

import java.net.URL;
import java.util.List;
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
import vcp.np.cas.services.LoginService;
import vcp.np.cas.utils.Constants;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.email.Email;
import vcp.np.cas.utils.email.EmailSender;

@Controller
@RequestMapping("/login")
public class LoginController {
	

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private EmailSender emailSender;
    

    @GetMapping
    public String showLoginPage(HttpServletRequest request, Model model) {
    	
        model.addAllAttributes(Helper.getAllModelAndView(request).getModel());
        
        if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
        

    	Email email = new Email();
    	email.setReceiverAddressList(List.of("gamanaryal@gmail.com"));
    	email.setBccAddressList(List.of("gamanaryal2000@gmail.com"));
    	email.setSubject("Welcome to VCP");
    	email.setContent("<h4>VCP</h4><br><p>Thank you for joining us.</p>");
    	emailSender.trigger(email);
    	
        
        return Constants.Templates.LOGIN;
    }
    
    @PostMapping
    public String login(HttpServletRequest request, @RequestParam String username, @RequestParam String password, Model model, HttpServletResponse httpServletResponse) {
    	
        ModelAndView modelAndView = Helper.getAllModelAndView(request);
        Map<String, Object> modelAndViewMap = modelAndView.getModel();
        model.addAllAttributes(modelAndViewMap);
    	
    	if (!Helper.isGenuineRequest(request)) {
        	return Constants.Templates.ERROR;
        }
    	
        model.addAttribute(Constants.USERNAME, username);
        
        String hostUrl = (String) modelAndViewMap.getOrDefault(Constants.HOST_URL, "");
        Long clientId = (Long) modelAndViewMap.get(Constants.CLIENT_ID);
        Long serviceId = (Long) modelAndViewMap.get(Constants.SERVICE_ID);
        Long clientServiceId = (Long) modelAndViewMap.get(Constants.CLIENTSERVICE_ID);
        
        if (clientId == null || serviceId == null || hostUrl.isEmpty()) {
        	model.addAllAttributes(Helper.error(null, null));
        	return Constants.Templates.ERROR;
        }
        
        try {

            Map<String, Object> loginData = loginService.loginUsingCredentials(hostUrl, clientId, serviceId, clientServiceId, username, password);
            
            if ((boolean) loginData.getOrDefault(Constants.IS_LOGIN_SUCCESSFUL, false)) {
            	
            	URL url = Helper.parseUrl(hostUrl);
            	
            	String redirectionUrl = "";
            	if (url != null) redirectionUrl = url.getProtocol().toString() + "://" + url.getHost().toString();
            	
            	String port = (url.getPort() != -1)? (String.valueOf(url.getPort())):"";
            	if (!port.isEmpty()) redirectionUrl = redirectionUrl + ":" + port;
            	
            	if (redirectionUrl == null || redirectionUrl.isEmpty()) {
            		String requestHost = (String) loginData.getOrDefault(Constants.REQUEST_HOST, "");
            		if (!requestHost.isEmpty()) {
            			redirectionUrl = "https://" + requestHost;
            		}
            	}
            	System.out.println("Redirecting to url: " + redirectionUrl);
            	
            	if (redirectionUrl == null || redirectionUrl.isEmpty()) {
            		throw new Exception("Error encountered on making redirection url login success for username:" + username);
            	}else {
            		redirectionUrl = redirectionUrl + "?token=" + ((String) loginData.getOrDefault(Constants.JwtToken.KEY, ""));
            	}
            	
//            	httpServletResponse.sendRedirect(redirectionUrl);
            	return "login";
            }else {
            	
            	if ((boolean) loginData.getOrDefault(Constants.IS_PASSWORD_EXPIRED, false)) {
                	System.out.println("Redirecting to password reset page");
            		httpServletResponse.sendRedirect(Constants.Request.Uri.PASSWORD_RESET + "?" + Constants.JwtToken.KEY + "=" + ((String) loginData.getOrDefault(Constants.JwtToken.KEY, "")));
            		return null;
            	}else {
                	model.addAllAttributes(loginData);
            		System.out.println("Could not login username:" + username + " on " + hostUrl);
            		return Constants.Templates.LOGIN;
            	}
            	
            }
            
        }catch(Exception e) {
        	e.printStackTrace();

    		model.addAllAttributes(Helper.error(null, null));
        	return Constants.Templates.ERROR;
        }
        
    } 
}
