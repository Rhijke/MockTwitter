package com.codepath.apps.simpletweet;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweet.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static org.parceler.Parcels.wrap;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        client = TwitterApplication.getRestClient(context);
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }
    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view hold
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tList) {
        tweets.addAll(tList );
        notifyDataSetChanged();
    }

    public void add(Tweet tweet) {
        tweets.add(0, tweet);
        notifyDataSetChanged();
    }

    // Notify data set changed when user updates a tweet
    public void updateTweet() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvCreatedAt;
        TextView tvFavCount, tvRtCount;
        Button btFavorited, btRetweet, btShare;

        public ViewHolder(@NonNull View itemView ) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvName = itemView.findViewById(R.id.tvName);
            tvFavCount = itemView.findViewById(R.id.tvFavCount);
            tvRtCount = itemView.findViewById(R.id.tvRtCount);
            btFavorited =  itemView.findViewById(R.id.favBtn);
            btRetweet = itemView.findViewById(R.id.rtBtn);
            btShare = itemView.findViewById(R.id.shareBtn);
        }

        public JsonHttpResponseHandler getJsonHttpResponseHandler() {
            return new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i("Tweet", String.valueOf(statusCode));
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i("Tweet", String.valueOf(statusCode));
                    Log.i("Tweet", response);
                }
            };
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@"+tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvRtCount.setText(String.valueOf(tweet.retweetCount));
            tvFavCount.setText(String.valueOf(tweet.favoriteCount));

            // Handle favorite tweet
            btFavorited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // POST button click to twitter API
                    Log.i("Tweet", String.valueOf(tweet.id));
                    if (!tweet.favorited) {
                        client.postFavoriteTweet(tweet.id, getJsonHttpResponseHandler());
                        tweet.favoriteCount++;
                    } else  {
                        client.postUnFavoriteTweet(tweet.id, getJsonHttpResponseHandler());
                        tweet.favoriteCount--;
                    }
                    tweet.favorited = !tweet.favorited;
                    notifyDataSetChanged();
                }
            });

            btRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tweet.retweeted){
                        client.postRetweet(tweet.id, getJsonHttpResponseHandler());
                        tweet.retweetCount++;
                    } else {
                        client.postUnRetweet(tweet.id, getJsonHttpResponseHandler());
                        tweet.retweetCount--;
                    }
                    tweet.retweeted = !tweet.retweeted;
                    notifyDataSetChanged();
                }
            });

            btShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = tweet.body;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tweet.user.name);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });
            // Change display of button depending if user already retweeted or favorited tweet
            btFavorited.setBackgroundResource(tweet.favorited ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
            btRetweet.setBackgroundResource(tweet.retweeted ? R.drawable.ic_retweet_pressed : R.drawable.ic_retweet);
            // Set relative time stamp
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);
            String relativeDate = "";
            try {
                long dateMillis = sf.parse(tweet.createAt).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString()
                        .replace("hours", "hr")
                        .replace("minutes", "m")
                        .replace("minute", "m")
                        .replace("hour", "hr");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvCreatedAt.setText(relativeDate);

            // Display user's twitter profile picture next to tweet
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
        }
    }
}
