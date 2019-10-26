package com.codepath.apps.simpletweet.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String name, screenName, profileImageUrl;
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
