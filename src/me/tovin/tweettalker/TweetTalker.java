package me.tovin.tweettalker;

import java.util.ArrayList;

// Flurry
import com.flurry.android.FlurryAgent;

import me.tovin.tweettalker.TwitterAPI;
import me.tovin.tweettalker.R;

// Twitter API
import twitter4j.Twitter;
import twitter4j.TwitterException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TweetTalker extends Activity {

	// Random code to tie voice recognition to this app
	private static final int VOICE_RECOGNITION_REQUEST = 4110;

	private Button tweetButton;
	private Button startOverButton;
	private Button addButton;

	private EditText tweet;

	String voiceOutput;
	String voiceInput;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweettalker);

		// Define EditText
		tweet = (EditText) findViewById(R.id.textBox);

		// Define Buttons
		tweetButton = (Button) findViewById(R.id.tweetButton);
		startOverButton = (Button) findViewById(R.id.startOverButton);
		addButton = (Button) findViewById(R.id.addButton);

		// Define Button Listeners

		// Tweet contents of EditBox
		tweetButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Get string from EditText box
				voiceOutput = tweet.getText().toString();
				// Tweet
				tweet(voiceOutput);
				FlurryAgent.logEvent("Tweet");
			}
		});

		// Clear contents of EditBox
		startOverButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tweet.setText("");
				FlurryAgent.logEvent("Clear tweet");
			}
		});

		// Set text of EditBox with Google's Voice Recognition
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Add a space if EditText isn't empty.
				if (tweet.getText().toString().equals("")) {
				} else {
					tweet.append(" ");
				}
				startVoiceRecognitionActivity();
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

	// Send tweet to Twitter using the API
	private void tweet(String tweetString) {
		Twitter t = ((TwitterAPI) getApplication()).getTwitter();
		try {
			Toast.makeText(this, "Tweeting...", Toast.LENGTH_LONG).show();
			t.updateStatus(tweetString);
			Toast.makeText(this, "Tweet sent", Toast.LENGTH_LONG).show();
			tweet.setText("");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * This is Google's Voice Recognition code taken straight from:
	 * 
	 * http://developer.android.com/resources/articles/speech-input.html
	 */

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Use LANGUAGE_MODEL_FREE_FORM for sentence recognition
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			voiceInput = matches.get(0);
			tweet.append(voiceInput);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}