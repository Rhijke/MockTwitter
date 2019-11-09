package com.codepath.apps.simpletweet;


import android.content.Context;
import android.content.res.Configuration;
import android.media.Image;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweet.models.Tweet;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;
    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
        // Bind the tween with view hold
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

    // Add a list of items -- change to type used

    public void addAll(List<Tweet> tList) {
        tweets.addAll(tList );
        notifyDataSetChanged();
    }

    public void add(Tweet tweet) {
        tweets.add(0, tweet);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvCreatedAt;
        TextView tvFavCount, tvRtCount;
        Button btFavorited, btRetweet;

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
            btRetweet = itemView.findViewById(R.id.btnTweet);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@"+tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvRtCount.setText(String.valueOf(tweet.retweetCount));
            tvFavCount.setText(String.valueOf(tweet.favoriteCount));
            if (tweet.favorited) {
                btFavorited.setBackgroundResource(R.drawable.ic_favorite);
            }
            if (tweet.retweeted) {
                btRetweet.setBackgroundResource(R.drawable.ic_retweet_pressed);
            }
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
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
        }
    }
}
