package vcp.np.cas.services;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.cas.domains.ClientService;
import vcp.np.cas.domains.ClientServiceTheme;
import vcp.np.cas.domains.ServiceSettings;
import vcp.np.cas.domains.User;
import vcp.np.cas.domains.UserClientService;
import vcp.np.cas.domains.UserEmail;
import vcp.np.cas.repositories.ClientServiceRepository;
import vcp.np.cas.repositories.ClientServiceThemeRepository;
import vcp.np.cas.repositories.ServiceSettingsRepository;
import vcp.np.cas.repositories.UserClientServiceRepository;
import vcp.np.cas.repositories.UserEmailRepository;
import vcp.np.cas.repositories.UserRepository;
import vcp.np.cas.utils.Constants;


@Service
public class CommonService {
	
	@Autowired
	public UserRepository userRepository;

	@Autowired
	public UserEmailRepository userEmailRepository;

	@Autowired
	public ClientServiceRepository clientServiceRepository;

	@Autowired
	public UserClientServiceRepository userClientServiceRepository;

	@Autowired
	public ClientServiceThemeRepository clientServiceThemeRepository;

	@Autowired
	public ServiceSettingsRepository serviceSettingsRepository;
	
	
	public ClientService getClientServiceDetail(URL url) {
	    System.out.println("Fetching client-service detail...");
	    ClientService clientService = null;

	    if (url != null) {

	        try {
                String requestHost = url.getHost();
                System.out.println("requestHost: " + requestHost);

                Optional<ClientService> optionalClientService = clientServiceRepository.findByRequestHost(requestHost);
                clientService = optionalClientService.orElse(null);
            	System.out.println("clientService: " + clientService);

                if (clientService == null) {
                    System.out.println("Could not find requestHost: " + requestHost);
                }

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Error occurred in getClientServiceDetail: " + e.getMessage());
	        }

	    } else {
	        System.out.println("URL is null.");
	    }

	    return clientService;
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
			
		    clientServiceThemeMap.put(Constants.LOGO_URL, "https://media.istockphoto.com/id/1313644269/vector/gold-and-silver-circle-star-logo-template.jpg?s=612x612&w=0&k=20&c=hDqCI9qTkNqNcKa6XS7aBim7xKz8cZbnm80Z_xiU2DI=");
		    clientServiceThemeMap.put(Constants.BACKGROUND_IMAGE_URL, "https://img.freepik.com/free-photo/autumn-leaf-falling-revealing-intricate-leaf-vein-generated-by-ai_188544-9869.jpg?size=626&ext=jpg&ga=GA1.1.1412446893.1705017600&semt=ais");
		}
		
		return clientServiceThemeMap;
	}
	
	
	public User fetchTheUserFromDb(String usernameOrEmail) {
		User user = null;
		
		user = getUserByUsername(usernameOrEmail);
		if (user == null) {
			UserEmail userEmail = getUserEmail(usernameOrEmail, true);
			if (userEmail != null) user = userEmail.getUser();
		}
		
		return user;
	}
	
	
	public User getUserByUsername(String username) {
		User user = null;
		
		if (username != null) {
			try {
				Optional<User> optionalUser = userRepository.findByUsername(username);
				user = optionalUser.orElse(null);
			}catch(Exception e) {
				e.printStackTrace();
	            System.out.println("Error occurred in getUserByUsername: " + e.getMessage());
			}
		}
		
		System.out.println("User[username:" + username + ((user != null)? "]":"] not") + " found in the db");
		return user;
	}

	
	public UserEmail getUserEmail(String email, boolean searchForPrimaryOnly) {
		UserEmail userEmail = null;
		
		if (email != null) {
			try {
				Optional<UserEmail> optionalUserEmail = null;
				if (searchForPrimaryOnly) {
					optionalUserEmail = userEmailRepository.findByEmailAndIsPrimary(email, true);
				}else {
					optionalUserEmail = userEmailRepository.findByEmail(email);
				}
				
				userEmail = optionalUserEmail.orElse(null);
			}catch(Exception e) {
				e.printStackTrace();
	            System.out.println("Error occurred in getUserEmail: " + e.getMessage());
			}
		}
		
		System.out.println("User[email:" + email + ((userEmail != null)? "]":"] not") + " found in the db");
		return userEmail;
	}


	public UserClientService getUserClientService(Long userId, Long clientServiceId) {
		UserClientService userClientService = null;
		
		if (userId != null && clientServiceId != null) {
			
	        try {

    			Optional<UserClientService> optionalUserClientService = userClientServiceRepository.findByUserIdAndClientServiceId(userId, clientServiceId);
    			userClientService = optionalUserClientService.orElse(null);
                if (userClientService == null) {
                    System.out.println("Could not find access of user[id:" + userId + "] on clientService[id:" + clientServiceId + "]");
                }

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Error occurred in getUserClientServiceDetail: " + e.getMessage());
	        }
			
		}else {
            System.out.println("user:" + userId + " or clientServiceId:" + clientServiceId + " is null");
		}
		
		return userClientService;
	}


	public String getLoginSuccessPathOfService(Long serviceId) {
		String loginSuccessPath = null;
		
		if (serviceId != null) {
			
	        try {

    			Optional<ServiceSettings> optionalServiceSettings = serviceSettingsRepository.findByServiceId(serviceId);
    			ServiceSettings serviceSettings = optionalServiceSettings.orElse(null);
                if (serviceSettings == null) {
                    System.out.println("Could not find settings of service[id:" + serviceId + "]");
                }else {
                	loginSuccessPath = serviceSettings.getLoginSuccessPath();
                }

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Error occurred in getLoginSuccessPathOfService: " + e.getMessage());
	        }
			
		}else {
            System.out.println("serviceId:" + serviceId + " is null");
		}
		
		return (loginSuccessPath != null && !loginSuccessPath.isEmpty())? loginSuccessPath:"";
	}
}
