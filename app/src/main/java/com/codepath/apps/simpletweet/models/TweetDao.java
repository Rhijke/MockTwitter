package com.codepath.apps.simpletweet.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT Tweet.favorited AS tweet_favorited, Tweet.retweeted AS tweet_retweeted, " +
            "Tweet.favoriteCount AS tweet_favoriteCount, Tweet.retweetCount AS tweet_retweetCount, " +
            "Tweet.body AS tweet_body, Tweet.createAt AS tweet_createAt, Tweet.id AS tweet_id, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY Tweet.createAt DESC LIMIT 300")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
