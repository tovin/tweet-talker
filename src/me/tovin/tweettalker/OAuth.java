// Tovin Hudson | IT 344 | Project 1

/* 

 I used this code to OAuth with Twitter based on the following tutorial:

 Android & Twitter - OAuth Authentication Tutorial 
 http://automateddeveloper.blogspot.com/2011/06/android-twitter-oauth-authentication.html

 */

package me.tovin.tweettalker;

import com.flurry.android.FlurryAgent;

import me.tovin.tweettalker.R;

// OAuth required for Twitter authentication 
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

// Twitter API
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

//Flurry
import com.flurry.android.FlurryAgent;

// Authorizes app to Twitter with OAuth 
public class OAuth extends Activity {

	private Twitter twitter;
	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;

	private String CONSUMER_KEY = "rjJ3m0ErWjKsH1wdJCyg";
	private String CONSUMER_SECRET = "Hm4sz7nVWdAynMyLvYKYXnQevKGezXWzwFMPRcLTEE";
	private String CALLBACK_URL = "callback://tweettalker"; //Returns to app after web authentication
	private String SAVED_TOKEN = "SavedToken";

	private Button buttonLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.setProperty("http.keepAlive", "false");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauth);
		
		// Checks if already OAuth'd 
		checkForSavedLogin();

		// Sets my secret Twitter API keys
		getConsumerProvider();
		
		// Authenticate button
		buttonLogin = (Button)findViewById(R.id.ButtonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {
				FlurryAgent.logEvent("Attempted to authenticate with Twitter API");
				askOAuth();
			}
		});
	}
	
	// Flurry
    @Override
    protected void onStart()
    {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "7CRGRD7CD8BVJY3X55BS");
    }
     
    // Flurry
    @Override
    protected void onStop()
    {
    	super.onStop();		
    	FlurryAgent.onEndSession(this);
    }

	private void checkForSavedLogin() {
		AccessToken a = getAccessToken();
		if (a==null) return;

		// Initialize Twitter API with my secret keys
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		((TwitterAPI)getApplication()).setTwitter(twitter);
		
		startFirstActivity();
		finish();
	}

	private void startFirstActivity() {
		// Calls TweetTalker
		Intent i = new Intent(this, TweetTalker.class);
		startActivity(i);
	}


	// Checks for saved API token
	private AccessToken getAccessToken() {
		SharedPreferences settings = getSharedPreferences(SAVED_TOKEN, MODE_PRIVATE);
		String token = settings.getString("accessTokenToken", "");
		String tokenSecret = settings.getString("accessTokenSecret", "");
		if (token!=null && tokenSecret!=null && !"".equals(tokenSecret) && !"".equals(token)){
			return new AccessToken(token, tokenSecret);
		}
		return null;
	}

	// Browser authentication
	private void askOAuth() {
		try {
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			provider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
			String authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
			Toast.makeText(this, "Twitter wants to authorize this app.", Toast.LENGTH_LONG).show();
			setConsumerProvider();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	// On return from Twitter.com authentication save user token
	@Override
	protected void onResume() {
		super.onResume();
		if (this.getIntent()!=null && this.getIntent().getData()!=null){
			Uri uri = this.getIntent().getData();
			if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
				try {
					provider.retrieveAccessToken(consumer, verifier);

					AccessToken a = new AccessToken(consumer.getToken(), consumer.getTokenSecret());
					storeAccessToken(a);

					// Initialize Twitter API
					twitter = new TwitterFactory().getInstance();
					twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					twitter.setOAuthAccessToken(a);
					((TwitterAPI)getApplication()).setTwitter(twitter);
					
					// Flurry
					FlurryAgent.logEvent("Successfully Authenticated with Twitter API");
					
					startFirstActivity();

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	// Saves users token
	private void storeAccessToken(AccessToken a) {
		SharedPreferences settings = getSharedPreferences(SAVED_TOKEN, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessTokenToken", a.getToken());
		editor.putString("accessTokenSecret", a.getTokenSecret());
		editor.commit();
	}
	
	
	private void getConsumerProvider() {
		OAuthProvider p = ((TwitterAPI)getApplication()).getProvider();
		if (p!=null){
			provider = p;
		}
		CommonsHttpOAuthConsumer c = ((TwitterAPI)getApplication()).getConsumer();
		if (c!=null){
			consumer = c;
		}
	}
	
	private void setConsumerProvider() {
		if (provider!=null){
			((TwitterAPI)getApplication()).setProvider(provider);
		}
		if (consumer!=null){
			((TwitterAPI)getApplication()).setConsumer(consumer);
		}
	}

}
