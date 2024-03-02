package vcp.np.cas.utils;

import java.util.List;

public class StringUtils {

	private static String[] listToArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new String[0];
        } else {
            return list.toArray(new String[0]);
        }
    }
	
}
