package com.tunnelmanager.server.api;

/**
 * Class JsonFactory
 * Return json string for generic messages
 *
 * @author Pierre-Olivier on 08/03/14.
 */
public class JsonFactory {
    /**
     * Json error message
     * @param command module name
     * @param error error name
     * @return json string
     */
    public static String error(String command, String error) {
        return "{\"command\":\"" + command + "\", \"error\":\"" + error + "\"}";
    }

    /**
     * Generate json "key":"value"
     * @param args key value key value ...
     * @return json string
     */
    public static String simpleJson(String... args) {
        if(args.length % 2 == 0) {
            String json = "{";

            for(int i = 0; i < args.length; i += 2 ) {
                String key = args[i];
                String value = args[i+1];

                json += "\"" + key + "\":\"" + value + "\"";

                if(i != args.length - 2) {
                    json += ",";
                }
            }

            json += "}";

            return json;
        } else {
            return JsonFactory.error("error", "json_server_error");
        }
    }
}
