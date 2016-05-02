package com.yuji.tdb.twitter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.yuji.tdb.common.CommonUtil;
import com.yuji.tdb.db.KeyValueDao;
import com.yuji.tdb.db.Train;
import com.yuji.tdb.db.TrainDao;
import com.yuji.tdb.debug.Debug;
import com.yuji.tdb.utility.StringUtility;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtil {
	private static final Logger log = Logger
			.getLogger(TwitterUtil.class.getName());
	private static TwitterUtil instance = null;
	private static Object obj = new Object();
	//private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private DateFormat df = new SimpleDateFormat("HH:mm");
	private static List<String> keywords = null;
	private long searchPeriod = 0;
	private long twitPeriod = 0;
	private Map<String, Long> twitTimeList = new HashMap<String, Long>();
	
	public static TwitterUtil getInstance() {
		if (instance == null) {
			synchronized (obj) {
				if (instance == null) {
					instance = new TwitterUtil();
				}
			}
		}
		return instance;
	}

	private TwitterUtil() {
		df.setTimeZone(TimeZone.getTimeZone("JST")); 
	}

	public void main(String consumerKey, String consumerSecret, AccessToken accessToken) {
		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret);
			TwitterFactory factory = new TwitterFactory(confBuilder.build());
			Twitter twitter = factory.getInstance(accessToken);

			List<Train> list = getTrainList();
			for (Train train : list){
				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				long cur = date.getTime();

				long id = train.getId();
				long t = getTwitTime(id);
				long period = getTwitPeriod();

				if (t > 0 && cur < t + period * 60 * 1000){
					continue;
				}
				twite(twitter, train);
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.severe(e.toString());
		}
	}

	private void twite(Twitter twitter, Train train) throws TwitterException {
		Query query = new Query();
		//query.setRpp(1000); // TODO
		query.setQuery(train.getSearchWord());
		QueryResult result = null;
		try {
			result = twitter.search(query);
		}
		catch (TwitterException e){
			log.severe("" + Debug.getLineNo());
			throw e;
		}
		List<Status> tweets = result.getTweets();
		
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		long cur = date.getTime();

		int count = 0;
		int hit = 0;
		int mhit = 0;
		long period = getSearchPeriod();
		long id = train.getId();
		
		int index;
		int mindex = 0;
		int size = tweets.size();
		for (index = 0; index < size; index++) {
			Status tweet = tweets.get(index);
			
			Date at = tweet.getCreatedAt();
			long t = at.getTime();
			
			String message = tweet.getText();
			
			if (t < cur - period * 60 * 1000){
				System.out.println("×" + tweet.getId() + " - "
						+ tweet.getText() + at);
				continue;
			}
			hit = filter(message);
			if (hit <= 0) {
				System.out.println("△" + tweet.getId() + " - "
						+ tweet.getText() + at);
				// 遅延以外の書き込み
				continue;
			}
			if (hit > mhit){
				hit = mhit;
				mindex = index;
			}

			System.out.println("@" + tweet.getId() + " - "
					+ tweet.getText() + at);
			count++;
		}

		String message = "[" + train.getName() + "] ";
		if (count < train.getCount()){
			message += df.format(date) + " 遅延なし";	
		}
		else {
			int N = 100;
			
			String sample = tweets.get(mindex).getText();
			sample = CommonUtil.replaceString(sample, "@", "＠");
			sample = StringUtility.parseSubstring(sample, N);
			message += df.format(date) + "(" + count + "ツイート)" + " " + sample;
		}
		System.out.println(message); //TODO
		
		Status status = null;
		try {
			if (count < train.getCount()){
				// 遅延なし
				//status = twitter.updateStatus(message); //TODO
				//setTwitTime(id, cur); // TODO 遅延なしの時のみ
			}
			else {
				// 遅延あり
				status = twitter.updateStatus(message); //TODO
				setTwitTime(id, cur);			
			}
		}
		catch (TwitterException e){
			log.severe(Debug.getLineNo() + " " + message);
			log.severe(Debug.getLineNo() + " " + status);
			throw e;
		}
	}
	
	private List<Train> getTrainList(){
		TrainDao dao = TrainDao.getInstance();
		List<Train> list = dao.search();
		if (list.size() <= 0){
			Train train;
			
			train = new Train("京浜東北線", "京浜東北", 5);
			dao.put(train);
			train = new Train("東海道線", "東海道", 5);
			dao.put(train);
			train = new Train("京浜急行線", "京急", 5);
			dao.put(train);
			list = dao.search();
		}
		return list;
	}
	
	public int filter(String text) {
		if (keywords == null) {
			keywords = new ArrayList<String>();
			keywords.add("遅延");
			keywords.add("遅れ");
			keywords.add("振り替え");
			keywords.add("振替");
			keywords.add("停止");
			keywords.add("事故");
		}

		int count = 0;
		for (String keyword : keywords) {
			if (text.indexOf(keyword) >= 0) {
				count++;
			}
		}
		return count;
	}
	
	public long getSearchPeriod(){
		if (searchPeriod <= 0){
			KeyValueDao dao = KeyValueDao.getInstance();
			searchPeriod = dao.getInt(KeyValueDao.KEY_SEARCH_PERIOD, 5);
		}
		return searchPeriod;
	}
	
	public long getTwitPeriod(){
		if (twitPeriod <= 0){
			KeyValueDao dao = KeyValueDao.getInstance();
			twitPeriod = dao.getInt(KeyValueDao.KEY_TWIT_PERIOD, 60);
		}
		return twitPeriod;
	}
	
	private long getTwitTime(long id){
		String key = KeyValueDao.KEY_TWIT_TIME + id;
		Long value = twitTimeList.get(key);
		if (value == null){
			value = 0L;
			setTwitTime(id, value);
		}
		return value;
	}
	
	private void setTwitTime(long id, long value){
		String key = KeyValueDao.KEY_TWIT_TIME + id;
		twitTimeList.put(key, value);
		KeyValueDao dao = KeyValueDao.getInstance();
		dao.put(key, String.valueOf(value));
	}
}
