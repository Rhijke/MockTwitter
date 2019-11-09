package com.codepath.apps.simpletweet.models;

import android.util.Log;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {
    @Embedded
    User user;
    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i =0; i < tweetWithUsers.size(); i++){
            Log.i("TweetWithUser", tweetWithUsers.get(i).tweet.createAt);
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.user = tweetWithUsers.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }
}
