package edu.northeastern.ccs.im.server.utils;

import java.util.UUID;

/**
 * The type Utils class used for all common static method.
 */
public class Utils {
    /**
     * A Private constructor to hide the implicit public one.
     */
    private Utils(){}

    /**
     * generates a new unique id string.
     *
     * @return uniqueId
     */
    public static String getUniqueId(){
        return UUID.randomUUID().toString();
    }
}
