package com.codepath.apps.simpletweet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.simpletweet.models.SampleModel;
import com.codepath.apps.simpletweet.models.SampleModelDao;

@Database(entities={SampleModel.class}, version=1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract SampleModelDao sampleModelDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
