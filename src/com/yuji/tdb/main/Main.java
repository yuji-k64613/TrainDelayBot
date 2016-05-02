package com.yuji.tdb.main;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.yuji.tdb.utility.StringUtility;

public class Main {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1(){
		String text = "あい bb cc";
		int length = 1;
		String ret = StringUtility.parseSubstring(text, length);
		System.out.println(ret);
	}

	public static void main() {
		String consumerKey = "";
		String consumerSecret = "";
		String tokenKey = "";
		String tokenSecret = "";

		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret);
			TwitterFactory factory = new TwitterFactory(confBuilder.build());

			AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
			Twitter twitter = factory.getInstance(accessToken);

			Status status = twitter.updateStatus("テスト");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
