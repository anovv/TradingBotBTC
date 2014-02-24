import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class BCBotBrain{
		
	Map<String, BCBotAnalyzer> analyzers = null;
	final BCBotGUI gui;
	
	//set real params here
	public double btc_buyPercentage = 0.2; //0.2
	public double btc_sellPercentage = 0.1; //0.1
	public double btc_safePercentage = 0.05; //Shit it's dangerous - 5%! Just left it for now	0.05
	public double btc_barrierPercentage = 0.01; // Should go 1% up to predict lifting trend 0.01
	
	public double ltc_buyPercentage = 0;
	public double ltc_sellPercentage = 0;
	public double ltc_safePercentage = 0;
	public double ltc_barrierPercentage = 0;
	
	public double nvc_buyPercentage = 0;
	public double nvc_sellPercentage = 0;
	public double nvc_safePercentage = 0;
	public double nvc_barrierPercentage = 0;
	
	public double nmc_buyPercentage = 0;
	public double nmc_sellPercentage = 0;
	public double nmc_safePercentage = 0;
	public double nmc_barrierPercentage = 0;
	
	//set test params here
	
	public double test_btc_buyPercentage = 0.2;
	public double test_btc_sellPercentage = 0.1;
	public double test_btc_safePercentage = 0.05;
	public double test_btc_barrierPercentage = 0.01;

	public double test_ltc_buyPercentage = 0;
	public double test_ltc_sellPercentage = 0;
	public double test_ltc_safePercentage = 0;
	public double test_ltc_barrierPercentage = 0;
	
	public double test_nvc_buyPercentage = 0;
	public double test_nvc_sellPercentage = 0;
	public double test_nvc_safePercentage = 0;
	public double test_nvc_barrierPercentage = 0;

	public double test_nmc_buyPercentage = 0;
	public double test_nmc_sellPercentage = 0;
	public double test_nmc_safePercentage = 0;
	public double test_nmc_barrierPercentage = 0;
	
	Map<String, Map<String, Double>> testParams = new HashMap<String, Map<String, Double>>();
	Map<String, Map<String, Double>> realParams = new HashMap<String, Map<String, Double>>();


	public BCBotBrain(){
		
		gui = new BCBotGUI(this);
		analyzers = new HashMap<String, BCBotAnalyzer>();
		
		//test params
		HashMap<String, Double> btc_test = new HashMap<String, Double>();
		btc_test.put("buyPercentage", test_btc_buyPercentage);
		btc_test.put("sellPercentage", test_btc_sellPercentage);
		btc_test.put("safePercentage", test_btc_safePercentage);
		btc_test.put("barrierPercentage", test_btc_barrierPercentage);		

		HashMap<String, Double> ltc_test = new HashMap<String, Double>();
		ltc_test.put("buyPercentage", test_ltc_buyPercentage);
		ltc_test.put("sellPercentage", test_ltc_sellPercentage);
		ltc_test.put("safePercentage", test_ltc_safePercentage);
		ltc_test.put("barrierPercentage", test_ltc_barrierPercentage);		

		HashMap<String, Double> nvc_test = new HashMap<String, Double>();
		nvc_test.put("buyPercentage", test_nvc_buyPercentage);
		nvc_test.put("sellPercentage", test_nvc_sellPercentage);
		nvc_test.put("safePercentage", test_nvc_safePercentage);
		nvc_test.put("barrierPercentage", test_nvc_barrierPercentage);		

		HashMap<String, Double> nmc_test = new HashMap<String, Double>();
		nmc_test.put("buyPercentage", test_nmc_buyPercentage);
		nmc_test.put("sellPercentage", test_nmc_sellPercentage);
		nmc_test.put("safePercentage", test_nmc_safePercentage);
		nmc_test.put("barrierPercentage", test_nmc_barrierPercentage);
		
		testParams.put("btc_usd", btc_test);
		testParams.put("ltc_usd", ltc_test);
		testParams.put("nvc_usd", nvc_test);
		testParams.put("nmc_usd", nmc_test);
		
		//real params		
		HashMap<String, Double> btc_real = new HashMap<String, Double>();
		btc_real.put("buyPercentage", btc_buyPercentage);
		btc_real.put("sellPercentage", btc_sellPercentage);
		btc_real.put("safePercentage", btc_safePercentage);
		btc_real.put("barrierPercentage", btc_barrierPercentage);
		
		HashMap<String, Double> ltc_real = new HashMap<String, Double>();
		ltc_real.put("buyPercentage", ltc_buyPercentage);
		ltc_real.put("sellPercentage", ltc_sellPercentage);
		ltc_real.put("safePercentage", ltc_safePercentage);
		ltc_real.put("barrierPercentage", ltc_barrierPercentage);		

		HashMap<String, Double> nvc_real = new HashMap<String, Double>();
		nvc_real.put("buyPercentage", nvc_buyPercentage);
		nvc_real.put("sellPercentage", nvc_sellPercentage);
		nvc_real.put("safePercentage", nvc_safePercentage);
		nvc_real.put("barrierPercentage", nvc_barrierPercentage);		

		HashMap<String, Double> nmc_real = new HashMap<String, Double>();
		nmc_real.put("buyPercentage", nmc_buyPercentage);
		nmc_real.put("sellPercentage", nmc_sellPercentage);
		nmc_real.put("safePercentage", nmc_safePercentage);
		nmc_real.put("barrierPercentage", nmc_barrierPercentage);
		
		realParams.put("btc_usd", btc_real);
		realParams.put("ltc_usd", ltc_real);
		realParams.put("nvc_usd", nvc_real);
		realParams.put("nmc_usd", nmc_real);
		
		analyzers.put("btc_usd", new BCBotAnalyzer(gui, null, "btc_usd", 0, 0, 0, 0));
		analyzers.put("ltc_usd", new BCBotAnalyzer(gui, null, "ltc_usd", 0, 0, 0, 0));
		analyzers.put("nvc_usd", new BCBotAnalyzer(gui, null, "nvc_usd", 0, 0, 0, 0));
		analyzers.put("nmc_usd", new BCBotAnalyzer(gui, null, "nmc_usd", 0, 0, 0, 0));	
	}
	
	public void showInfoForPair(final String pair){
		new Thread(new Runnable(){

			@Override
			public void run() {
				double maxPrev = 0; 
				double minPrev = 0;
				double weightedBuy = 0;
				double weightedSell = 0;
				double weightedBuyCur = 0;
				double weightedSellCur = 0;
				double maxCur = 0;
				double minCur = 0;
				double amount1Cur = 0;
				double amount2Cur = 0;
				double amount1 = 0;
				double amount2 = 0;
				boolean state = false;
				boolean statePrev = false;
				while(true){
					
					if(analyzers != null && analyzers.get(pair).info != null && analyzers.get(pair).info.amount != null){
						weightedBuyCur = analyzers.get(pair).info.weightedBuy;
						weightedSellCur = analyzers.get(pair).info.weightedSell;
						amount1Cur = analyzers.get(pair).info.amount.get(pair.split("_")[0]);
						amount2Cur = analyzers.get(pair).info.amount.get(pair.split("_")[1]);
						state = analyzers.get(pair).isBuying;
						if(weightedBuy != weightedBuyCur || weightedSell != weightedSellCur || amount1 != amount1Cur || amount2 != amount2Cur || state != statePrev){
							String temp = (state) ? " buying" : " selling";								
							gui.changeLabel(pair, "buy: " + weightedBuyCur + " sell: " + weightedSellCur + " " + pair.split("_")[0] + ": " + amount1Cur + " " + pair.split("_")[1] + ": " + amount2Cur + temp);
							weightedBuy = weightedBuyCur;
							weightedSell = weightedSellCur;
							amount1 = amount1Cur;
							amount2 = amount2Cur;
							statePrev = state;
						}
						//if(!analyzers.get(pair).info.testing){
							maxCur = analyzers.get(pair).max;
							minCur = analyzers.get(pair).min;
							
							if(maxPrev != maxCur || minPrev != minCur){
								String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
								gui.changeText(pair, " Max: " + maxCur + " Min: " + minCur + " " + time);
								maxPrev = maxCur;
								minPrev = minCur;										
							}
						//}
						/*try {
							Thread.sleep(500);
						} catch (Exception e) {						
						}*/
					}
				}				
			}			
		}).start();		
	}
	
	public void show(){		
				
		new Thread(new Runnable(){

			@Override
			public void run() {
				gui.showGui();
				showInfoForPair("btc_usd");
				showInfoForPair("ltc_usd");
				showInfoForPair("nvc_usd");
				showInfoForPair("nmc_usd");
			}
			
		}).start();		
		fetchOrdersHistoryForPair("btc_usd");		
	}	
	
	public void fetchOrdersHistoryForPair(final String pair){
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				double wBuyPrev = 0;
				double wSellPrev = 0;
				double lSellPrev = 0;
				
				double wBuy = 0;
				double wSell = 0;
				double lSell = 0;
				
				while(true){					
					if(analyzers != null && analyzers.get(pair).info != null){							
						
						wBuy = analyzers.get(pair).info.weightedBuy;
						wSell = analyzers.get(pair).info.weightedSell;
						lSell = analyzers.get(pair).info.lastSell;				
						
						if(wBuyPrev != wBuy || wSellPrev != wSell || lSell != lSellPrev){
							BufferedWriter writer = null;
							try{
								String fileName = pair + "_weighted_price_history.txt";
								File logFile = new File(fileName);
							
								writer = new BufferedWriter(new FileWriter(logFile, true));
								String time = String.valueOf(System.currentTimeMillis());
								writer.write(wBuy + " " + wSell + " " + lSell + " " + time);
								writer.newLine();
							}catch(Exception e){
							
							}finally{
								try{
									writer.close();
								}catch(Exception e){
									
								}
							}
							wBuyPrev = wBuy;
							wSellPrev = wSell;
							lSellPrev = lSell;					
						}
					}				
				}
			}
		}).start();			
	}
		
	public static void main(String[] args){	
		BCBotBrain bot = new BCBotBrain();
		//bot.initTestBrain();
		bot.show();
	}
}

/*public void generate(){
//124 + 2sin(wt) for buy; w = pi/4 rad/s, t - s
//123 + 2sin(wt) for sell;

new Thread(new Runnable() {
	
	@Override
	public void run() {
		while(true){
			
			double time = System.currentTimeMillis()/1000;
			
			analyzer.info.weightedBuy = 124 + 2*Math.cos(Math.PI/4 * time) + Math.random();
			analyzer.info.weightedSell = 123 + 2*Math.cos(Math.PI/4 * time) + Math.random();
			try {
				Thread.sleep(1000);//время обновления инфы
			} catch (InterruptedException e) {
			}
		}
	}
}).start();		
}

BCBotTradeInterface testInterface = new BCBotTradeInterface(){

@Override
public void sell() {
	if(amountBTC != 0){
		amountUSD += amountBTC*analyzer.info.weightedSell;
		amountBTC = 0;
		System.out.println("Sold at: " + analyzer.info.weightedSell);
	}
	else
		System.out.println("No BTC!");
}

@Override
public void buy() {
	if(amountUSD != 0){
		amountBTC += amountUSD/analyzer.info.weightedBuy;
		amountUSD = 0;	
		System.out.println("Bought at: " + analyzer.info.weightedBuy);					
	}
	else
		System.out.println("No USD!");

}			
};*/

/*public void show(){
	
	new Thread(new Runnable(){

		@Override
		public void run() {
			double wBuyPrev = 0;
			double wSellPrev = 0;
			double usdPrev = 0;
			double btcPrev = 0;
			double maxPrev = 0;
			double minPrev = 0;
			while(true){
				/*if(analyzer != null && analyzer.info != null && analyzer.info.amount != null){
					if(btcPrev != analyzer.info.amount.get("btc") || usdPrev != analyzer.info.amount.get("usd") || maxPrev != analyzer.max || minPrev != analyzer.min){
						String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
						System.out.println("Max: " + analyzer.max + " Min: " + analyzer.min + " Btc: " + analyzer.info.amount.get("btc")+ " Usd: " + analyzer.info.amount.get("usd") + " " + time);
						
						//System.out.println("Weighted buy: " + analyzer.info.weightedBuy + " Weighted sell: " + analyzer.info.weightedSell + " Max: " + analyzer.max + " Min: " + analyzer.min);
						wBuyPrev = analyzer.info.weightedBuy;
						wSellPrev = analyzer.info.weightedSell;
						btcPrev = analyzer.info.amount.get("btc");
						usdPrev = analyzer.info.amount.get("usd");
						maxPrev = analyzer.max;
						minPrev = analyzer.min;						
					}
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
						//System.out.println(e.toString());							
					}
				}
			}	
		}
	}).start();		
}*/

/*BCBotTradeInterface tradeInterface_btc = new BCBotTradeInterface(){

@Override
public void sell() {
	analyzers.get("btc_usd").info.sell(gui);
}

@Override
public void buy() {
	analyzers.get("btc_usd").info.buy(gui);
}

};

BCBotTradeInterface tradeInterface_ltc = new BCBotTradeInterface(){

@Override
public void sell() {
	analyzers.get("ltc_usd").info.sell(gui);
}

@Override
public void buy() {
	analyzers.get("ltc_usd").info.buy(gui);
}

};

BCBotTradeInterface tradeInterface_nvc = new BCBotTradeInterface(){

@Override
public void sell() {
	analyzers.get("nvc_usd").info.sell(gui);
}

@Override
public void buy() {
	analyzers.get("nvc_usd").info.buy(gui);
}

};

BCBotTradeInterface tradeInterface_nmc = new BCBotTradeInterface(){

@Override
public void sell() {
	analyzers.get("nmc_usd").info.sell(gui);
}

@Override
public void buy() {
	analyzers.get("nmc_usd").info.buy(gui);
}

};

BCBotTradeInterface testInterface_btc = new BCBotTradeInterface(){
@Override
public void sell() {
	if(analyzers.get("btc_usd").info.amount.get("btc") != 0){
		double temp = analyzers.get("btc_usd").info.amount.get("usd");
		analyzers.get("btc_usd").info.amount.put("usd", temp + analyzers.get("btc_usd").info.amount.get("btc")*analyzers.get("btc_usd").info.weightedSell);
		analyzers.get("btc_usd").info.amount.put("btc" ,0.0);
		
		//Gui stuff
		String pair = "btc_usd";					
		double relativeProfit = (analyzers.get("btc_usd").info.weightedSell - analyzers.get("btc_usd").min)*100/analyzers.get("btc_usd").min;
		double roundedProfit = new BigDecimal(relativeProfit).setScale(3, RoundingMode.UP).doubleValue();
		
		String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
		String amountInfo = " " + pair.split("_")[0] + ": " + analyzers.get("btc_usd").info.amount.get(pair.split("_")[0]) + " " + pair.split("_")[1] + ": " + analyzers.get("btc_usd").info.amount.get(pair.split("_")[1]);
		
		String template1 = "Profit " + roundedProfit + " %" + amountInfo + " " + time;		
		String template2 = "Loss " + roundedProfit + " %" + amountInfo + " " + time;
		
		String info = (relativeProfit > 0) ? template1 : template2;
		
		gui.changeProfitInfo(pair, info);
		gui.saveProfitInfo(pair, info);

	}
	else
		System.out.println("No BTC!");
}

@Override
public void buy() {
	if(analyzers.get("btc_usd").info.amount.get("usd") != 0){
		double temp = analyzers.get("btc_usd").info.amount.get("btc");
		analyzers.get("btc_usd").info.amount.put("btc", temp + analyzers.get("btc_usd").info.amount.get("usd")/analyzers.get("btc_usd").info.weightedBuy);
		analyzers.get("btc_usd").info.amount.put("usd", 0.0);	
		//System.out.println("Bought at: " + analyzers.get("btc_usd").info.weightedBuy);					
	}
	else
		System.out.println("No USD!");
}			
};*/

	


