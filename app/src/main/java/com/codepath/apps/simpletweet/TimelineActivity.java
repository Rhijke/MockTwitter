package com.codepath.apps.simpletweet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.codepath.apps.simpletweet.models.Tweet;
import com.codepath.apps.simpletweet.models.TweetDao;
import com.codepath.apps.simpletweet.models.TweetWithUser;
import com.codepath.apps.simpletweet.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.codepath.apps.simpletweet.models.TweetWithUser.getTweetList;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG ="TimelineActivity";
    private SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    TweetDao tweetDao;

    public static final int REQUEST_CODE = 1990;
    private long tweetUid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.drawable.ic_twitter);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        client = TwitterApplication.getRestClient(this);
        // Room database
        tweetDao = ((TwitterApplication) getApplicationContext()).getMyDatabase().tweetDao();

        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "load more data");
                loadMoreData();
            }
        };        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        // Query for existing tweets in the DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });
        populateHomeTimeline();
    }

    private void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    // 2. Deserialize and construct new model objects from the API response
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    adapter.addAll(tweetsFromNetwork);
                    swipeContainer.setRefreshing(false);
                    // Query for existing tweets in the DB
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("DB", "Save data to DB");
                            // Insert users
                            List<User> usersFromNetwork = User.fromJsonArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            // Insert tweets
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                            List<Tweet> list = getTweetList(tweetDao.recentItems());
                            Log.i(TAG, list.get(list.size() - 1).body);
                        }
                    });
                    tweetUid = tweetsFromNetwork.get(tweetsFromNetwork.size()-1).id;
                } catch (JSONException e) {
                    Log.e(TAG, "json exception", e);
                }
                // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
                adapter.notifyItemRangeInserted(adapter.tweets.size()-26, 25);
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("LoadMoreData", response);
            }
        }, tweetUid);
    }

    public void onLogout(MenuItem mi) {
        client.clearAccessToken();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
    public void onCompose(MenuItem mi) {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
            adapter.add(tweet);
        }
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                handleData(json);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Failure " + response, throwable);
            }
        });
    }

    private void handleData(JsonHttpResponseHandler.JSON json) {
        JSONArray jsonArray = json.jsonArray;
        try {
            final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
            adapter.clear();
            adapter.addAll(tweetsFromNetwork);
            swipeContainer.setRefreshing(false);
            // Query for existing tweets in the DB
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    // Insert users
                    List<User> usersFromNetwork = User.fromJsonArray(tweetsFromNetwork);
                    tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                    // Insert tweets
                    tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                    List<Tweet> list = getTweetList(tweetDao.recentItems());
                    Log.i(TAG, list.get(0).body);
                }
            });
            Log.i(TAG, tweetsFromNetwork.get(0).user.name + " " + tweetsFromNetwork.get(0).body);
            // tweetUid = tweetsFromNetwork.get(tweetsFromNetwork.size()-1).id;
            tweetUid = tweetsFromNetwork.get(0).id;
        } catch (JSONException e) {
            Log.e(TAG, "json exception", e);
        }
        adapter.notifyDataSetChanged();
    }
}
