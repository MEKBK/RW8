package com.kbk.Rest;

import com.kbk.model.User;

import java.util.HashMap;
import java.util.Map;

public class Restful {
    public static Map<String, Object> set(int code, String msg, User user){
        Map<String, Object> restful = new HashMap<>();
        restful.put("code" , code);
        restful.put("msg" , msg);
        restful.put("user" , user);
        return restful;
    }

    public static Map<String, Object> set(int code, String msg){
        Map<String, Object> restful = new HashMap<>();
        restful.put("code" , code);
        restful.put("msg" , msg);
        return restful;
    }
}
