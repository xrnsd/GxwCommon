package com.wgx.common;

import java.lang.reflect.Method;

public class SystemProperties {

    /**
     * Get the value for the given key.
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key,String def_val){
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");  
            Method get = c.getMethod("get", String.class, String.class);
            def_val = (String)(get.invoke(c, key, def_val ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return def_val;
        }
    }

    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case sensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     *         not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean getBoolean(String key,boolean def_val){
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");  
                Method getBoolean = c.getMethod("getBoolean", String.class, Boolean.class);
                def_val = (Boolean)(getBoolean.invoke(c, key, def_val));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                return def_val;
            }
    }

    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    public static boolean set(String key, String val) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");  
            Method String = c.getMethod("set", String.class, String.class);
            String.invoke(c, key, val);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    public static boolean set(String key, int val) {
        return set(key,String.valueOf(val));
    }

}
