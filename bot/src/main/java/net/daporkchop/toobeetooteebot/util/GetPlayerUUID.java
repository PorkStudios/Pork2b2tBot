package net.daporkchop.toobeetooteebot.util;

import java.io.*;
import java.util.*;
import java.net.URL;



import org.apache.commons.io.IOUtils;

import org.json.simple.*;
import org.json.simple.parser.ParseException;


public class GetPlayerUUID {
	
	public String getUuid(String name) throws ParseException {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));           
            if(UUIDJson.isEmpty()) return "Nonexistant Name";                       
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            return UUIDObject.get("id").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
	
}