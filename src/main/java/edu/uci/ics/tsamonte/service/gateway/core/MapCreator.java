package edu.uci.ics.tsamonte.service.gateway.core;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;

public class MapCreator {
    public static HashMap<String, String> createHeaderMap(String email, String session_id, String transaction_id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("session_id", session_id);
        map.put("transaction_id", transaction_id);
        return map;
    }

    public static HashMap<String, String> createParamsMap(MultivaluedMap<String, String> params) {
        HashMap<String, String> map = new HashMap<String, String>();
        for(String key : params.keySet()) {
            map.put(key, params.getFirst(key));
        }
        return map;
    }
}
