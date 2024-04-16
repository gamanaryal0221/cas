package vcp.np.cas.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.http.HttpServletRequest;
import vcp.np.cas.utils.Constants.Error;

public class Helper {
	
	public static URL parseUrl(String _url) {
        System.out.println("Parsing url: " + _url);
        
        URL url = null;

		if (_url != null && !_url.isEmpty()) {
			try {
				
                url = new URL(_url);
                
	        } catch (MalformedURLException e) {
	            System.out.println("Malformed url:" + _url + " :" + e.getMessage());
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Could not parse url:" + _url + " :" + e.getMessage());
	        }
		}else {
            System.out.println("Provided url(i.e. " + _url + ") is empty or null.");
		}
		return url;
	}
	
	public static String getCustomizedFullCasUrl(StringBuffer fullCasUrl, String plusUrl, Map<String, Object> params) {
		URL url = Helper.parseUrl(fullCasUrl.toString());
    	String port = String.valueOf(url.getPort());
    	
    	String _url = url.getProtocol() + "://" + url.getHost();
    	if (port != null && !port.isEmpty()) {
    		_url = _url + ":" + port;
    	}
        
    	if (plusUrl != null && !plusUrl.isEmpty()) {
    		_url = _url + plusUrl;
    	}
        
    	if (params != null) {
    		String paramsUrl = "";
        	for(String paramKey : params.keySet()) {
        		paramsUrl = paramsUrl + paramKey + "=" + params.getOrDefault(paramKey, "") + "&";
        	}
        	
        	if (paramsUrl != null && !paramsUrl.isEmpty()) {
        		_url = _url + "?" + paramsUrl;
        	}
    	}
    	
        return _url;
	}
	

	public static boolean isGenuineRequest(HttpServletRequest request) {
		boolean isGenuineRequest = false;
		try {
			isGenuineRequest = (boolean) request.getAttribute(Constants.Request.Is_GENUINE);
		}catch(Exception e) {
			e.getStackTrace();
			System.out.println("Error encountered while checking isGenuineRequest "+ e.getMessage());
		}
		System.out.println("==> "+ request.getRequestURI() + ":" + request.getMethod() + " -> isGenuineRequest::: "+ isGenuineRequest);
		return isGenuineRequest? isGenuineRequest:false;
	}
	

	public static ModelAndView getAllModelAndView(HttpServletRequest request) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = (ModelAndView) request.getAttribute(Constants.MODEL_AND_VIEW);
		}catch(Exception e) {
			e.getStackTrace();
			System.out.println("Error encountered while getting modelAndView "+ e.getMessage());
		}
		return modelAndView;
	}
	
	public static Map<String, String> error(String title, String message){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put(Error.Title.KEY, (title != null && !title.isEmpty())? title:Error.Title.TECHNICAL_ERROR);
		errorMap.put(Error.Message.KEY, (message != null && !message.isEmpty())? message:Error.Message.SMTH_WENT_WRONG);
		return errorMap;
	}

	public static Timestamp dateToTimestamp(Date date) {
		try {
			return new Timestamp(date.getTime());
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	
	public static String getCasPrintLog() {
		return ""
		+ "    ____          ___         _ _ _ _ \n"
		+ "   / ___|        / _ \\       /  _ _ _|\n"
		+ "  | (           / / \\ \\      \\ (____\n"
		+ "  | |     --   / /   \\ \\  --  \\____ \\\n"
		+ "  | (___      /  =====  \\     _ _ _) \\\n"
		+ "   \\____|    /_/       \\_\\   |_ _ _ _/\n"
		+ "";
	}

	public static boolean isValidMailAddress(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
			return true;
        } catch (AddressException e) {
			e.printStackTrace();
			return false;
        }
    }

	public static String generateRandomValue(int limit) {
		int _limit = (limit != -1)? limit:10;

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_+=-`~";
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < _limit; i++) {
            int index = (int) (characters.length() * Math.random());
            randomStringBuilder.append(characters.charAt(index));
        }
        return randomStringBuilder.toString();
    }
}
