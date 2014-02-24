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

public class BCBotTestInterface extends BCBotTradeInterface{

	String pair;
	BCBotBrain bot;
	
	String cur1;
	String cur2;
	
	BCBotTestInterface(String pair, BCBotBrain bot){
		this.pair = pair;
		this.bot = bot;
		cur1 = pair.split("_")[0];
		cur2 = pair.split("_")[1];
	}
	
	@Override
	public void sell() {
		if(bot.analyzers.get(pair).info.amount.get(cur1) != 0){
			double temp = bot.analyzers.get(pair).info.amount.get(cur2);
			bot.analyzers.get(pair).info.amount.put(cur2, temp + bot.analyzers.get(pair).info.amount.get(cur1)*bot.analyzers.get(pair).info.weightedSell);
			bot.analyzers.get(pair).info.amount.put(cur1, 0.0);
			
			double relativeProfit = (bot.analyzers.get(pair).info.weightedSell - bot.analyzers.get(pair).min)*100/bot.analyzers.get(pair).min;
			double roundedProfit = new BigDecimal(relativeProfit).setScale(3, RoundingMode.UP).doubleValue();
			
			String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
			String amountInfo = " " + cur1 + ": " + bot.analyzers.get(pair).info.amount.get(cur1) + " " + cur2 + ": " + bot.analyzers.get(pair).info.amount.get(cur2);
			
			String template1 = "Profit " + roundedProfit + " %" + amountInfo + " " + time;		
			String template2 = "Loss " + roundedProfit + " %" + amountInfo + " " + time;
			
			String info = (relativeProfit > 0) ? template1 : template2;
			
			bot.gui.changeProfitInfo(pair, info);
			bot.gui.changeText(pair, " Sell for " + bot.analyzers.get(pair).info.weightedSell);
		}else{
			bot.gui.changeText(pair, " No " + cur1 + "!");
		}
	}

	@Override
	public void buy() {			
		if(bot.analyzers.get(pair).info.amount.get(cur2) != 0){
			double temp = bot.analyzers.get(pair).info.amount.get(cur1);
			bot.analyzers.get(pair).info.amount.put(cur1, temp + bot.analyzers.get(pair).info.amount.get(cur2)/bot.analyzers.get(pair).info.weightedBuy);
			bot.analyzers.get(pair).info.amount.put(cur2, 0.0);	
			bot.gui.changeText(pair, " Buy for " + bot.analyzers.get(pair).info.weightedBuy);
		}else{
			bot.gui.changeText(pair, " No " + cur2 + "!");
		}			
	}		
}

/*BCBotTradeInterface testInterface_btc = new BCBotTradeInterface(){
@Override
public void sell() {
	if(bot.analyzers.get(pair).info.amount.get(cur1) != 0){
		double temp = bot.analyzers.get(pair).info.amount.get(cur2);
		bot.analyzers.get(pair).info.amount.put(cur2, temp + bot.analyzers.get(pair).info.amount.get(cur1)*bot.analyzers.get(pair).info.weightedSell);
		bot.analyzers.get(pair).info.amount.put(cur1, 0.0);
		
		//Gui stuff
		/*String pair = "btc_usd";					
		double relativeProfit = (analyzers.get("btc_usd").info.weightedSell - analyzers.get("btc_usd").min)*100/analyzers.get("btc_usd").min;
		double roundedProfit = new BigDecimal(relativeProfit).setScale(3, RoundingMode.UP).doubleValue();
		
		String time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(Calendar.getInstance().getTime());
		String amountInfo = " " + pair.split("_")[0] + ": " + analyzers.get("btc_usd").info.amount.get(pair.split("_")[0]) + " " + pair.split("_")[1] + ": " + analyzers.get("btc_usd").info.amount.get(pair.split("_")[1]);
		
		String template1 = "Profit " + roundedProfit + " %" + amountInfo + " " + time;		
		String template2 = "Loss " + roundedProfit + " %" + amountInfo + " " + time;
		
		String info = (relativeProfit > 0) ? template1 : template2;
		
		gui.changeProfitInfo(pair, info);
		gui.saveProfitInfo(pair, info);

	}else{
		System.out.println("No BTC!");
	}
}
	
@Override
public void buy() {
	if(bot.analyzers.get(pair).info.amount.get(cur2) != 0){
		double temp = bot.analyzers.get(pair).info.amount.get(cur1);
		bot.analyzers.get(pair).info.amount.put(cur1, temp + bot.analyzers.get(pair).info.amount.get(cur2)/bot.analyzers.get(pair).info.weightedBuy);
		bot.analyzers.get(pair).info.amount.put(cur2, 0.0);	
	}else{
		System.out.println("No USD!");
	}
}
};*/
