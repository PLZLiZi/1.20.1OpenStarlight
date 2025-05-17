package net.daichang.starlight.server.util.fonts;

import java.util.HashMap;
import java.util.Map;

public class Mappings {
    public static final Map<String, String> obfClass = new HashMap<>();
    public static final Map<String, String> obfFields = new HashMap<>();
    public synchronized static String getObfClass(String friendlyName) {
        return obfClass.get(friendlyName)==null?friendlyName:obfClass.get(friendlyName);
    }
    public static String getObfField(String searge) {
        return obfFields.get(searge)==null?searge:obfFields.get(searge);
    }
}
