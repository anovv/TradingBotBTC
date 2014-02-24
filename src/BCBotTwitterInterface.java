import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class BCBotTwitterInterface implements Runnable {
	
	//@botBCbot
	
	public String consumer_key = "TKTukcq37Ow1qfZdpkcA";
	public String consumer_secret = "0K2L52AHnzUfLKkIw44PF4WCqnjhNGKgQqdB8pyFE";
	public String oauth_access_token = "1972567476-8ahWaCY5w9fLS6eyMubP5iuuscBaOz1cXVmBCco";
	public String oauth_access_token_secret = "tyO2qEGPRJgcG8Aueb8JeUOw1Y6uohQLQRGQeiDc";	
	
	public long delay = 30*60*1000;//30 min in millis
	
	String text = null;
	
	BCBotTwitterInterface(String text){
		this.text = text;
	}
	
	public void sendTweet(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(consumer_key)
            .setOAuthConsumerSecret(consumer_secret)
            .setOAuthAccessToken(oauth_access_token)
            .setOAuthAccessTokenSecret(oauth_access_token_secret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        final Twitter twitter = tf.getInstance();                	
        
        try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			System.out.println(e.toString());
		}
		
		try {					
	    	twitter.updateStatus(text);					
	    } catch (TwitterException e) {
	    	System.out.println(e.toString());
	    }
	}

	@Override
	public void run() {
		sendTweet();
	}
}
