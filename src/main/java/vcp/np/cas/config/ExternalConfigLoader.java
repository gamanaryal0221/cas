package vcp.np.cas.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import vcp.np.cas.utils.Constants;


// @Configuration
// @PropertySource("classpath:application.properties")
public class ExternalConfigLoader {

	public static Map<String, Object> load(Profile profile) {
		
    	System.out.println("############# Loading external config #############");

        String externalConfigPath = getExternalConfigPath(profile);
        System.out.println("Loading external config file from: '" + externalConfigPath + "' ...");


        File file = new File(externalConfigPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("External config file does not exist: " + externalConfigPath);
        }
        

        try (InputStream inputStream = new FileInputStream(file)) {
        	
            Yaml yaml = new Yaml();
            Map<String, Object> externalConfig = yaml.load(inputStream);
            // System.out.println("yamlData:" + externalConfig);

            if (externalConfig == null || externalConfig.size() == 0) {
            	throw new NullPointerException("External config file seems to have null or no data");
            }
            
       
            return externalConfig;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Failed to load external configuration file", e);
        }
    }


    private static String getExternalConfigPath(Profile profile) {
        String externalConfigPath = (String) profile.getProperty("external.config.path", "");
        if (externalConfigPath.isEmpty()) {
            throw new IllegalStateException("External config path is not specified.");
        }
        
        String externalConfigFilename = (String) profile.getProperty("external.config.filename", "");
        if (externalConfigFilename.isEmpty()) {
            throw new IllegalStateException("External config filename is not specified.");
        }


        String env = profile.getEnvironmentName();
        if (env == null || env.isEmpty() || env.equalsIgnoreCase(Constants.Environment.DEV)) {
            // do nothing
        }else if (env.equalsIgnoreCase(Constants.Environment.QC)){
            externalConfigPath = externalConfigPath + "\\" + Constants.Environment.QC;
        }else {
            externalConfigPath = externalConfigPath + "\\" + env + "\\" + Sprint.VERSION;
        }

        return externalConfigPath + "\\" + externalConfigFilename;
    }
	
	
//	private void populateMultiValueMap(Map<String, Object> data, String parentKey, MultiValueMap<String, Object> multiValueMap) {
//        for (Map.Entry<String, Object> entry : data.entrySet()) {
//            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
//            if (entry.getValue() instanceof Map) {
//                populateMultiValueMap((Map<String, Object>) entry.getValue(), key, multiValueMap);
//            } else {
//                multiValueMap.add(key, entry.getValue());
//            }
//        }
//    }
//	
//	
//    
//    private Map<String, String> flattenYamlData(Map<String, Object> yamlData) {
//    	
//    	try {
//    		
//    		Map<String, String> flattenedData = new HashMap<>();
//            flattenYamlDataRecursively("", yamlData, flattenedData);
//            return flattenedData;
//            
//    	}catch (Exception e) {
//            throw new RuntimeException("Error encounterd on parsing external configuration file", e);
//    	}
//        
//    }
//
//    private void flattenYamlDataRecursively(String prefix, Map<String, Object> yamlData, Map<String, String> flattenedData) throws Exception {
//    	for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            if (value instanceof Map) {
//                flattenYamlDataRecursively(prefix + key + ".", (Map<String, Object>) value, flattenedData);
//            } else {
//                flattenedData.put(prefix + key, value.toString());
//            }
//        }
//    }
    
}

