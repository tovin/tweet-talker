/* 

 I used this code to OAuth with Twitter based on the following tutorial:

 Android & Twitter - OAuth Authentication Tutorial 
 http://automateddeveloper.blogspot.com/2011/06/android-twitter-oauth-authentication.html

 */

package me.tovin.tweettalker;


import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import android.app.Application;

/**
 * @author rob
 *
 */
public class TwitterAPI extends Application{
	
	
	private Twitter twitter;
	/**
	 * @return the twitter
	 */
	public Twitter getTwitter() {
		return twitter;
	}

	/**
	 * @param twitter the twitter to set
	 */
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	/**
	 * @return the provider
	 */
	public OAuthProvider getProvider() {
		return provider;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}

	/**
	 * @return the consumer
	 */
	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}


}
