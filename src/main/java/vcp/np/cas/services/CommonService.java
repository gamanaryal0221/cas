package vcp.np.cas.services;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.datasource.usermanagement.domains.ClientService;
import vcp.np.datasource.usermanagement.domains.ClientServiceTheme;
import vcp.np.datasource.usermanagement.domains.ServiceSettings;
import vcp.np.datasource.usermanagement.domains.UserClientService;
import vcp.np.datasource.usermanagement.repositories.ClientServiceRepository;
import vcp.np.datasource.usermanagement.repositories.ClientServiceThemeRepository;
import vcp.np.datasource.usermanagement.repositories.ServiceSettingsRepository;
import vcp.np.datasource.usermanagement.repositories.UserClientServiceRepository;
import vcp.np.cas.utils.Constants;


@Service
public class CommonService {

	@Autowired
	public ClientServiceRepository clientServiceRepository;

	@Autowired
	public UserClientServiceRepository userClientServiceRepository;

	@Autowired
	public ClientServiceThemeRepository clientServiceThemeRepository;

	@Autowired
	public ServiceSettingsRepository serviceSettingsRepository;

	@Autowired
	public PlainSqlQueries plainSqlQueries;

	
	public ClientService getClientServiceDetail(URL url) {
	    System.out.println("Fetching client-service detail...");

	    if (url == null) {
	        System.out.println("URL:" + url + " is null in getClientServiceDetail.");
	        return null;
	    }
	    
	    try {
            String requestHost = url.getHost();
            System.out.println("requestHost: " + requestHost);

            Optional<ClientService> optionalClientService = clientServiceRepository.findByRequestHost(requestHost);
			ClientService clientService = (optionalClientService == null)? null:optionalClientService.get();
        	System.out.println("clientService: " + clientService);

            if (clientService == null) {
                System.out.println("Could not find requestHost: " + requestHost);
            }
            return clientService;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in getClientServiceDetail: " + e.getMessage());
            return null;
        }

	}

	public ClientService getClientServiceDetail(String requestHost) {
	    System.out.println("Fetching client-service detail...");

	    if (requestHost == null || requestHost.isEmpty()) {
	        System.out.println("requestHost: '" + requestHost + "' is null or empty in getClientServiceDetail.");
	        return null;
	    }
	    
	    try {
            System.out.println("requestHost: " + requestHost);

            Optional<ClientService> optionalClientService = clientServiceRepository.findByRequestHost(requestHost);
            ClientService clientService = (optionalClientService == null)? null:optionalClientService.get();
        	System.out.println("clientService: " + clientService);

            if (clientService == null) {
                System.out.println("Could not find requestHost: " + requestHost);
            }
            
            return clientService;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in getClientServiceDetail: " + e.getMessage());
            return null;
        }

	}
	
	
	public Map<String, String> getClientServiceTheme(Long clientId, Long serviceId) {
	    System.out.println("Fetching client-service theme [clientId:" + clientId + ",serviceId:" + serviceId + "]...");
		Map<String, String> clientServiceThemeMap = new HashMap<String, String>();
		
		if (clientId != null && serviceId != null) {
			
			List<ClientServiceTheme> clientServiceThemeList = clientServiceThemeRepository.findAllByClientId(clientId);			
			if (clientServiceThemeList != null && !clientServiceThemeList.isEmpty()) {
				
				Optional<ClientServiceTheme> optionalClientServiceTheme = clientServiceThemeList.stream()
				        .filter(theme -> theme.getClient().getId().equals(clientId) && theme.getService().getId().equals(serviceId))
				        .findFirst();

				if (optionalClientServiceTheme.isPresent()) {
				    ClientServiceTheme clientServiceTheme = optionalClientServiceTheme.get();
					System.out.println("Found client-service[clientId:" + clientId + ",serviceId:" + serviceId + "] theme:" + clientServiceTheme);

				    clientServiceThemeMap.put(Constants.LOGO_URL, clientServiceTheme.getLogoUrl());
				    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, clientServiceTheme.getBackgroundImageUrl());
				    clientServiceThemeMap.put(Constants.BACKGROUND_COLOR_CODE, clientServiceTheme.getColorCode());
				} else {
					System.out.println("Could not find client-service[clientId:" + clientId + ",serviceId:" + serviceId + "] theme");
					System.out.println("Searching for client theme");
					
					
					Optional<ClientServiceTheme> optionalClientTheme = clientServiceThemeList.stream()
					        .filter(theme -> theme.getClient().getId().equals(clientId) && theme.getService() == null)
					        .findFirst();

					if (optionalClientTheme.isPresent()) {
					    ClientServiceTheme clientTheme = optionalClientTheme.get();
						System.out.println("Found client[id:" + clientId + "] theme:" + clientTheme);
					    
					    clientServiceThemeMap.put(Constants.LOGO_URL, clientTheme.getLogoUrl());
					    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, clientTheme.getBackgroundImageUrl());
					    clientServiceThemeMap.put(Constants.BACKGROUND_COLOR_CODE, clientTheme.getColorCode());
					} else {
						System.out.println("Could not find client[id:" + clientId + "] theme");
					}
					
				}
				
			}else {
				System.out.println("Not Found: Themes of the client-service[clientId:" + clientId + ",serviceId:" + serviceId + "]");
			}
		}
		
		
		if (clientServiceThemeMap.isEmpty()) {
			System.out.println("Setting default theme ...");
			
		    clientServiceThemeMap.put(Constants.LOGO_URL, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS3qvfiNwIrb7XMfI6Kru-uCz57xEQeEND8T1IQYHVROA&s");
		    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, "https://img.freepik.com/free-photo/autumn-leaf-falling-revealing-intricate-leaf-vein-generated-by-ai_188544-9869.jpg?size=626&ext=jpg&ga=GA1.1.1412446893.1705017600&semt=ais");
		}
		
		return clientServiceThemeMap;
	}
	

	public UserClientService getUserClientServiceById(Long userId, Long clientServiceId) {
		
		if (userId == null || clientServiceId == null) {
            System.out.println("user:" + userId + " or clientServiceId:" + clientServiceId + " is null in getUserClientServiceById");
            return null;
		}
		
		try {

			Optional<UserClientService> optionalUserClientService = userClientServiceRepository.findByUserIdAndClientServiceId(userId, clientServiceId);
			UserClientService userClientService = (optionalUserClientService == null)? null:optionalUserClientService.get();
			
	    	System.out.println("Does user[id" + userId + "] have access on client-service[id:" + clientServiceId + "]?\n >> " + (userClientService != null));
	    	return userClientService;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in getUserClientServiceById: " + e.getMessage());
            return null;
        }
		
	}

	public UserClientService getUserClientServiceByCredential(String username, Long clientServiceId) {		
		if (username == null || username.isEmpty() || clientServiceId == null) {
            System.out.println("username:" + username + " or clientServiceId:" + clientServiceId + " is null or empty in getUserClientServiceByCredential");
            return null;
		}
		
		Long userClientServiceId = plainSqlQueries.getUserClientServiceIdByCredential(username, clientServiceId);
		if (userClientServiceId == null) {
			System.out.println("Did not get userClientServiceId from getUserClientServiceByCredential");
			return null;
		}
		
		Optional<UserClientService> optionalUserClientService = userClientServiceRepository.findById(userClientServiceId);
		UserClientService userClientService = (optionalUserClientService == null)? null:optionalUserClientService.get();
		if (userClientService == null) {
			System.out.println("Could not find userClientServiceId:" + userClientServiceId + " on db");
			return null;
		}
		
		return userClientService;
		
	}


	public String getLoginSuccessPathOfService(Long serviceId) {
		
		if (serviceId == null) {
            System.out.println("serviceId:" + serviceId + " is null in getLoginSuccessPathOfService");
            return "";
		}
		
		try {

			Optional<ServiceSettings> optionalServiceSettings = serviceSettingsRepository.findByServiceId(serviceId);
			ServiceSettings serviceSettings = (optionalServiceSettings == null)? null:optionalServiceSettings.get();
            if (serviceSettings == null) {
                System.out.println("Could not find settings of service[id:" + serviceId + "]");
                return "";
            }

        	String loginSuccessPath = serviceSettings.getLoginSuccessUri();
        	System.out.println("loginSuccessPath: " + loginSuccessPath);
            
            return (loginSuccessPath == null || loginSuccessPath.isEmpty())? "":loginSuccessPath;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in getLoginSuccessPathOfService: " + e.getMessage());
            return "";
        }
		
	}
}
