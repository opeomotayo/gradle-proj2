package com.nisum.myteam.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class PropertyController {

    @Value("${message}")
    private String value1;

    private HashMap<String, Object> configMap = new HashMap<>();

  @RequestMapping(method = RequestMethod.GET,value = "message")
    public HashMap<String, Object> getProperties() {
        configMap.put("message", value1);
        return configMap;
    }
}
