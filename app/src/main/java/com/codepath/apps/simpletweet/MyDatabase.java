package com.codepath.apps.simpletweet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.simpletweet.models.SampleModel;
import com.codepath.apps.simpletweet.models.SampleModelDao;
import com.codepath.apps.simpletweet.models.Tweet;
import com.codepath.apps.simpletweet.models.TweetDao;
import com.codepath.apps.simpletweet.models.User;

@Database(entities={SampleModel.class, Tweet.class, User.class}, version=4)
public abstract class MyDatabase extends RoomDatabase {
    public abstract SampleModelDao sampleModelDao();
    public abstract TweetDao tweetDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
