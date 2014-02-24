import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

public class BCBotDataManager {	
	
	public volatile double weightedBuy = 0.0;
	public volatile double weightedSell = 0.0;
	public volatile double lastSell = 0.0;
	public volatile Map<String, Double> amount = null;
	
	public BCBotKeys keySet = null;		
	public BCBotAnalyzer analyzer = null;
	public volatile boolean running = true;
	
	public BCBotGUI gui = null;
	
	public volatile ArrayList<Long> safeSellHistory = new ArrayList<Long>();
	public static long safeSellAlertTime = 3*60*60*1000; //3 hours in millis
	final BCBotApiHandler apiHandler;
	public Thread threadUpdate = null;
			
	final String pair;

	// Test stuff
	int ACCELERATION = 1; // large values may lead to incorrect result	
	
	boolean testing = false;	
	
	public ArrayList<Point> points = new ArrayList<Point>();	
		
	public BCBotDataManager(String pair, BCBotGUI gui, BCBotAnalyzer analyzer){
		this.pair = pair;		
		keySet = new BCBotKeys();		
		apiHandler = new BCBotApiHandler();
		this.analyzer = analyzer;
		this.gui = gui;
	}
	
	public void terminate(){
		running  = false;
		try {
			threadUpdate.join();
		} catch (Exception e) {
		}
		running = true;
	}
			
	public void update(){		
		
		threadUpdate = new Thread(new Runnable(){

			@Override
			public void run() {
			
				System.out.println("Update started...");							
				while(running){
						
					updateHelper();
				}
			}			
		});		
		threadUpdate.start();
	}
	
	public boolean checkSafeSellHistory(){
		boolean temp = false;
		if(safeSellHistory.size() < 3){
			return false;
		}else{
			for(int i = 0; i < safeSellHistory.size(); i++){
				if(Math.abs(safeSellHistory.get(i + 2) - safeSellHistory.get(i)) < safeSellAlertTime){
					temp = true;
					break;
				}
			}
			return temp;
		}
	}
	
	
	public void updateHelper(){	
		
		amount = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));					
		if(checkSafeSellHistory()){
			safeSellHistory.clear();
			new Thread(new Runnable(){

				@Override
				public void run() {
					analyzer.terminate();
					sell(gui);
					terminate();
				}				
			}).start();

			gui.buttons.get(pair).get("start_analyzer").setEnabled(false);
			gui.buttons.get(pair).get("stop_analyzer").setEnabled(false);
			gui.buttons.get(pair).get("start_updater").setEnabled(true);
			gui.buttons.get(pair).get("stop_updater").setEnabled(false);
			gui.buttons.get(pair).get("sell").setEnabled(false);
			gui.buttons.get(pair).get("buy").setEnabled(false);

			gui.changeText(pair, " Analyzer stoped - Alert!");
			
			sendNotification();
		}
		if(amount != null){
			//Map<String, Double> result = apiHandler.getRealPriceFromServerForPair(pair, amount.get("usd"));
			double tempSell = apiHandler.getWeightedSell(pair, amount.get(pair.split("_")[0]));//btc
			if(tempSell != -1)
				weightedSell = tempSell;
			double tempBuy = apiHandler.getWeightedBuy(pair, amount.get(pair.split("_")[1]));//usd
			if(tempBuy != -1)
				weightedBuy = tempBuy;
			double tempLastSell = apiHandler.getLastSell(pair);
			if(tempLastSell != -1)
				lastSell = tempLastSell;
		}else{
			//System.out.println("Updating... Amount is null");
		}
		//mapOfOpenOrders.put(pair, apiHandler.getOpenOrdersForPair(pair, keySet.info_key, keySet.info_secret));
		//if(amount != null)
			//System.out.println(distrib);
			//System.out.println("Updating... Weighted buy: " + weightedBuy + " Weighted sell: " + weightedSell + " Btc: " + amount.get("btc")+ " Usd: " + amount.get("usd"));
		//updateHelper(apiHandler, pair);	
	}	
	
	public void sendNotification(){
		
		String host = "smtp.gmail.com";
	    String from = "dirtyvalera@gmail.com";
	    String pass = "dirtyvalera1";
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "true"); 
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");
	 
	    String[] to = {"anov1992@yandex.ru", "backmeupplz@gmail.com"};
	    
	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message = new MimeMessage(session);
	    try{
		    message.setFrom(new InternetAddress(from));
		 
		    InternetAddress[] toAddress = new InternetAddress[to.length];
		 
		    for( int i=0; i < to.length; i++ ) {
		        toAddress[i] = new InternetAddress(to[i]);
		    }
		    
		    for( int i=0; i < toAddress.length; i++) {
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    message.setSubject("BOT ALERT");
		    message.setText("TIME TO TRADE!!!");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
	    }catch(Exception e){
	    	
	    }
	}

	
	public void sell(BCBotGUI gui){
		double amount = -1;
		while(amount == -1){
			if(this.amount != null){
				amount = this.amount.get(pair.split("_")[0]);
			}
		}
		double sell = weightedSell;		
		sellHelper(gui, amount);		
		if(analyzer.min != 0){
			//give time for updater to refresh
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			double relativeProfit = (weightedSell - analyzer.min)*100/analyzer.min;
			double roundedProfit = new BigDecimal(relativeProfit).setScale(3, RoundingMode.UP).doubleValue();
			
			String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
			String amountInfo = " " + pair.split("_")[0] + ": " + this.amount.get(pair.split("_")[0]) + " " + pair.split("_")[1] + ": " + this.amount.get(pair.split("_")[1]);
			
			String template1 = "Profit " + roundedProfit + " %" + amountInfo + " " + time;		
			String template2 = "Loss " + roundedProfit + " %" + amountInfo + " " + time;
			
			String info = (relativeProfit > 0) ? template1 : template2;
			
			gui.changeProfitInfo(pair, info);
			gui.saveProfitInfo(pair, info);
		}
		String tweet = "Sell for " + sell + "$";
		//new Thread(new BCBotTwitterInterface(tweet)).start();
	
	}
	
	public void sellHelper(BCBotGUI gui, double amount){	
		if(amount != -1){
			//apiHandler.sellPair(pair, amount.get(pair.split("_")[0]), keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"), gui);
			apiHandler.sellPair(pair, amount, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"), gui);	
			
		}
		//check for open orders
		Map<String, Map<String, String>> openOrders = apiHandler.getOpenOrdersForPair(pair, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"));
		
		if(openOrders != null){
			if(openOrders.size() != 0){
			//cancel all orders
				utilize(gui);
				//double temp = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret")).get(pair.split("_")[0]);
				double temp = -1;
				while(temp == -1){
					Map<String, Double> balance = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));
					if(balance != null)
						temp = balance.get(pair.split("_")[0]);
				}
				sellHelper(gui, temp);
			}
		}else{
			utilize(gui);
			//double temp = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret")).get(pair.split("_")[0]);
			double temp = -1;
			while(temp == -1){
				Map<String, Double> balance = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));
				if(balance != null)
					temp = balance.get(pair.split("_")[0]);
			}
			sellHelper(gui, temp);				
		}
	}
	
	public void buy(BCBotGUI gui){
		double amount = -1;
		while(amount == -1){
			if(this.amount != null){
				amount = this.amount.get(pair.split("_")[1]);
			}
		}
		double buy = weightedBuy;		
		buyHelper(gui, amount);
		String tweet = "Buy for " + buy + "$";
		//new Thread(new BCBotTwitterInterface(tweet)).start();		
	
	}
	
	public void buyHelper(BCBotGUI gui, double amount){
		if(amount != -1){
			//apiHandler.buyPair(pair, amount.get(pair.split("_")[1]), keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"), gui);
			//apiHandler.buyPair(pair, apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret")).get(pair.split("_")[1]), keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"), gui);
			apiHandler.buyPair(pair, amount, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"), gui);
			
			//apiHandler.buyPair(pair, distrib.get(pair), keySet.keys.get(pair).get("key"), keySet.keys.get(pair).get("secret"));					
		}
		//check for open orders		
		Map<String, Map<String, String>> openOrders = apiHandler.getOpenOrdersForPair(pair, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"));
		
		if(openOrders != null){
			if(openOrders.size() != 0){
			//cancel all orders
				utilize(gui);
				//double temp = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret")).get(pair.split("_")[1]);
				double temp = -1;
				while(temp == -1){
					Map<String, Double> balance = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));
					if(balance != null)
						temp = balance.get(pair.split("_")[1]);
				}
				buyHelper(gui, temp);
			}
		}else{
			utilize(gui);
			//double temp = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret")).get(pair.split("_")[1]);
			double temp = -1;
			while(temp == -1){
				Map<String, Double> balance = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));
				if(balance != null)
					temp = balance.get(pair.split("_")[1]);
			}
			buyHelper(gui, temp);
		}		
	}
	
	public void utilize(BCBotGUI gui){
		Map<String, Map<String, String>> openOrders = apiHandler.getOpenOrdersForPair(pair, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"));
		
		if(openOrders != null){
			if(openOrders.size() != 0){
				Iterator entries = openOrders.entrySet().iterator();
				while(entries.hasNext()){
					Entry entry = (Entry) entries.next();
					String order_id = (String)entry.getKey();
					JSONObject result = apiHandler.cancelOrder(order_id, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"));
					 
					//first check
					/*while(result == null){
						result = apiHandler.cancelOrder(order_id, keySet.trade_keys.get(pair).get("key"), keySet.trade_keys.get(pair).get("secret"));
					}*/
				}
				gui.changeText(pair, " All orders for pair " + pair + " are closed");
			}
		}else{
			utilize(gui);
		}
	}
	
	public static void generateSin(){
		//124 + 2sin(wt) for buy; w = pi/4 rad/s, t - s
		//123 + 2sin(wt) for sell;
				
		new Thread(new Runnable() {					
			@Override
			public void run() {						
				while(true){							
					long time = System.currentTimeMillis()/1000;
					long t = System.currentTimeMillis();
							
					double wBuy = 124 + 2*Math.cos(Math.PI/4 * time); 
					double wSell = 123 + 2*Math.cos(Math.PI/4 * time);							
					double lSell = 123 + 2*Math.cos(Math.PI/4 * time);
					
					BufferedWriter writer = null;
					String filename = "sin.txt";
					try{
						File logFile = new File(filename);
						writer = new BufferedWriter(new FileWriter(logFile, true));
						writer.write(wBuy + " " + wSell + " " + lSell + " " + t);
						writer.newLine();
					}catch(Exception e){
						System.out.println(e.toString());
					
					}finally{
						try{
							writer.close();
						}catch(Exception e){
							System.out.println(e.toString());						
						}
					}
												
					try {
						Thread.sleep(1000);//время обновления инфы
					} catch (InterruptedException e) {
						System.out.println(e.toString());
					}
				}
			}
		}).start();			
	}
	
	public void generateFromFile(final String pair){
		threadUpdate = new Thread(new Runnable(){

			@Override
			public void run() {
				//amount = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));					
				amount = new HashMap<String, Double>();
				amount.put("btc", 0.0);
				amount.put("ltc", 0.0);
				amount.put("nvc", 0.0);
				amount.put("nmc", 0.0);
				amount.put("usd", 0.0);
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}
				amount.put(pair.split("_")[0], 0.0);
				amount.put(pair.split("_")[1], 1000.0);
				String filename = pair + "_test.txt";
				try {
		            DataInputStream in = new DataInputStream(new FileInputStream(filename));
		            BufferedReader br = new BufferedReader(new InputStreamReader(in));
		            String strLine;
		            while ((strLine = br.readLine()) != null) {
		            	String[] temp = strLine.split(" ");            			
		            	points.add(new Point(temp[0], temp[1], temp[2], temp[3]));
		            }
		            in.close();
		        } catch (Exception e){      
					System.out.println(e.toString());  	
		        }
				Point curPoint;
				Point nextPoint;
				Point lastPoint = points.get(points.size() - 1);
				Point firstPoint = points.get(0);
				for(int i = 0; i < points.size() - 1; i++){
					if(running){
						curPoint = points.get(i);
						nextPoint = points.get(i+1);
						weightedBuy = curPoint.wBuy;
						weightedSell = curPoint.wSell;
						lastSell = curPoint.lSell;
						try {
							long waitTime = (long) ((Math.abs(nextPoint.time - curPoint.time))/ACCELERATION);
							//Thread.sleep(waitTime);
							Thread.sleep(100);
						} catch (Exception e) {
							System.out.println(e.toString());
						}
						int pBarValue = (int) ((curPoint.time - firstPoint.time)*100/(lastPoint.time - firstPoint.time));
						gui.progressBars.get(pair).setValue(pBarValue);
						if(i == points.size() - 2){
							gui.changeText(pair, " End of file");
							gui.progressFrames.get(pair).setVisible(false);
						}
					}
				}
			}			
		});
		threadUpdate.start();
	}
	
	public class Point{
		
		double wBuy = 0;
		double wSell = 0;
		double lSell = 0;
		long time = 0;
		
		Point(String wBuy, String wSell, String lSell, String time){
			this.wBuy = Double.parseDouble(wBuy);
			this.wSell = Double.parseDouble(wSell);
			this.lSell = Double.parseDouble(lSell);
			this.time = Long.parseLong(time);
		}
	}

}




/*public void generate(){
	//124 + 2sin(wt) for buy; w = pi/4 rad/s, t - s
	//123 + 2sin(wt) for sell;
	
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			amount = apiHandler.getBalance(keySet.info_keys.get(pair).get("key"), keySet.info_keys.get(pair).get("secret"));					
			
			amount.put("usd", 1000.0);
			amount.put("btc", 0.0);
			
			while(running){					
				
				double time = System.currentTimeMillis()/1000;
				
				weightedBuy = 124 + 2*Math.cos(Math.PI/4 * time); 
				weightedSell = 123 + 2*Math.cos(Math.PI/4 * time);
				
				//TODO lastSell = ???
				
				/*info.weightedBuy.put("ltc_usd", 2.1 + 0.1*Math.cos(Math.PI/4 * time)); 
				info.weightedSell.put("ltc_usd", 2 + 0.1*Math.cos(Math.PI/4 * time));
				
				info.weightedBuy.put("nvc_usd", 3.4 + 0.1*Math.cos(Math.PI/4 * time)); 
				info.weightedSell.put("nvc_usd", 3.3 + 0.1*Math.cos(Math.PI/4 * time));
				
				info.weightedBuy.put("nmc_usd", 0.4 + 0.1*Math.cos(Math.PI/4 * time)); 
				info.weightedSell.put("nmc_usd", 0.3 + 0.1*Math.cos(Math.PI/4 * time));
									
				try {
					Thread.sleep(1000);//время обновления инфы
				} catch (InterruptedException e) {
				}
			}
		}
	}).start();		
}*/