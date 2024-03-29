package com.codepath.apps.simpletweet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {
    @ColumnInfo
    public String body, createAt;
    @Ignore
    public User user;
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public long userId;
    @ColumnInfo
    public long favoriteCount;
    @ColumnInfo
    public long retweetCount;

    @ColumnInfo
    public boolean favorited;
    @ColumnInfo
    public boolean retweeted;



    // empty contructor needed by the Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createAt = jsonObject.getString("created_at");
        tweet.favoriteCount = jsonObject.getLong("favorite_count");
        tweet.retweetCount = jsonObject.getLong("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i< jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
