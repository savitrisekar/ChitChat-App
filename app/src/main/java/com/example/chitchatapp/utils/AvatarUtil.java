package com.example.chitchatapp.utils;

public class AvatarUtil {
    public static String generateAvatar(String s) {
        s = s.replaceAll(" ", "");
        return "https://robohash.org/" + s + "/bgset_bg2/3.14160?set=set4";
    }
}
