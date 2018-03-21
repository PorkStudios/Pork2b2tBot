package net.daporkchop.toobeetooteebot.util;

import java.io.*;
import java.util.*;
import org.json.*;

public class GetPlayerUUID {
	
	public String getUuid(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));           
            if(UUIDJson.isEmpty()) return "Nonexistant Name";                       
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            return UUIDObject.get("id").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
       
        return "error";
    }
	
}