package com.yuji.tdb;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.yuji.tdb.common.CommonUtil;
import com.yuji.tdb.db.KeyValueDao;
import com.yuji.tdb.debug.Debug;
import com.yuji.tdb.twitter.TwitterUtil;

@SuppressWarnings("serial")
public class TrainDelayBotServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(TrainDelayBotServlet.class.getName());
	// private static RequestToken requestToken = null;
	// private static AccessToken accessToken = null;
	// private static String consumerKey = null;
	// private static String consumerSecret = null;

	private KeyValueDao dao = KeyValueDao.getInstance();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			log.info("start");

			resp.setContentType("text/plain");
			resp.getWriter().println("<h3>traindelaybot Hello, world</h3>");

			String parmConsumerKey = req.getParameter("key");
			String parmConsumerSecret = req.getParameter("secret");
			String parmResult = req.getParameter("result");
			if (!CommonUtil.isNull(parmConsumerKey)
					&& !CommonUtil.isNull(parmConsumerSecret)) {
				setOAuthAuthorization(req, resp, parmConsumerKey,
						parmConsumerSecret);
				return;
			}

			String consumerKey = dao.get(KeyValueDao.KEY_CONSUMER_KEY);
			String consumerSecret = dao.get(KeyValueDao.KEY_CONSUMER_SECRET);
			if (CommonUtil.isNull(consumerKey)
					|| CommonUtil.isNull(consumerSecret)) {
				log.warning("" + Debug.getLineNo());
				return;
			}

			if (!CommonUtil.isNull(parmResult)) {
				String requestTokenKey = dao.get(KeyValueDao.KEY_REQUEST_TOKEN);
				String requestTokenSecret = dao
						.get(KeyValueDao.KEY_REQUEST_TOKEN_SECRET);

				if (CommonUtil.isNull(requestTokenKey)
						|| CommonUtil.isNull(requestTokenSecret)) {
					log.warning("" + Debug.getLineNo());
					return;
				}
				RequestToken requestToken = new RequestToken(requestTokenKey,
						requestTokenSecret);
				resultOAuthAuthorization(consumerKey, consumerSecret,
						requestToken);
				return;
			}

			String accessTokenKey = dao.get(KeyValueDao.KEY_ACCESS_TOKEN);
			String accessTokenSecret = dao
					.get(KeyValueDao.KEY_ACCESS_TOKEN_SECRET);
			if (CommonUtil.isNull(accessTokenKey)
					|| CommonUtil.isNull(accessTokenSecret)) {
				log.warning("" + Debug.getLineNo());
				return;
			}

			AccessToken accessToken = new AccessToken(accessTokenKey,
					accessTokenSecret);
			TwitterUtil util = TwitterUtil.getInstance();
			util.main(consumerKey, consumerSecret, accessToken);
		} finally {
			log.info("end");
		}

	}

	private void setOAuthAuthorization(HttpServletRequest req,
			HttpServletResponse resp, String parmConsumerKey,
			String parmConsumerSecret) {
		try {
			ConfigurationBuilder confBuilder = new ConfigurationBuilder();

			confBuilder.setDebugEnabled(true);
			confBuilder.setOAuthConsumerKey(parmConsumerKey);
			confBuilder.setOAuthConsumerSecret(parmConsumerSecret);
			Configuration conf = confBuilder.build();

			String url = req.getRequestURL().toString();

			OAuthAuthorization oauth = new OAuthAuthorization(conf);
			// Twitterの認証画面からの戻り先を指定します。
			// AccessTokenコントローラを指定します。
			String callbackURL = url + "?result=yes";
			// RequestToken requestToken =
			// oauth.getOAuthRequestToken(callbackURL);
			RequestToken requestToken = oauth.getOAuthRequestToken(callbackURL);
			// RequestTokenをセッションに保存しておきます。
			// sessionScope("RequestToken", requestToken);
			// Twitterの認証画面へリダイレクトします。

			// consumerKey = parmConsumerKey;
			// consumerSecret = parmConsumerSecret;
			dao.put(KeyValueDao.KEY_CONSUMER_KEY, parmConsumerKey);
			dao.put(KeyValueDao.KEY_CONSUMER_SECRET, parmConsumerSecret);
			dao.put(KeyValueDao.KEY_REQUEST_TOKEN, requestToken.getToken());
			dao.put(KeyValueDao.KEY_REQUEST_TOKEN_SECRET,
					requestToken.getTokenSecret());

			resp.sendRedirect(requestToken.getAuthenticationURL());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.severe(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.severe(e.toString());
		}
	}

	private void resultOAuthAuthorization(String consumerKey,
			String consumerSecret, RequestToken requestToken) {
		try {
			// Twitter twitter = new TwitterFactory().getInstance();
			// String tokenKey = requestToken.getToken();
			// String tokenSecret = requestToken.getTokenSecret();

			ConfigurationBuilder confBuilder = new ConfigurationBuilder();
			confBuilder.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret);
			TwitterFactory factory = new TwitterFactory(confBuilder.build());
			Twitter twitter = factory.getInstance();

			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
			dao.put(KeyValueDao.KEY_ACCESS_TOKEN, accessToken.getToken());
			dao.put(KeyValueDao.KEY_ACCESS_TOKEN_SECRET,
					accessToken.getTokenSecret());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.severe(e.toString());
		}
	}
}
