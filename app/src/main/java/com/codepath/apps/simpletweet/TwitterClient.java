package com.codepath.apps.simpletweet;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = com.codepath.apps.simpletweet.BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		params.put("count", 300);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params,"",  handler);
	}

	public void postFavoriteTweet(Long tweetId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		Log.i("Tweet", "Favorite id: " + tweetId);
		params.put("id", tweetId);
		client.post(apiUrl, params,"",  handler);
	}

	public void postUnFavoriteTweet(Long tweetId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params,"",  handler);
		Log.i("Tweet", "Unfavorite");
	}

	public void getNextPageOfTweets(JsonHttpResponseHandler handler, long maxId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

    public void postRetweet(Long tweetId, JsonHttpResponseHandler handler) {
		String url = "statuses/retweet/" + tweetId.toString();
		String apiUrl = getApiUrl(url);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params,"",  handler);
		Log.i("Tweet", "retweeted");
    }

	public void postUnRetweet(Long tweetId, JsonHttpResponseHandler handler) {
		String url = "statuses/unretweet/" + tweetId.toString();
		String apiUrl = getApiUrl(url);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params,"",  handler);
		Log.i("Tweet", "Unretweeted");
	}
}
