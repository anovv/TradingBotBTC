
public class BCBotTrades extends BCBotTradeInterface{
	
	BCBotBrain bot;
	String pair;
	
	BCBotTrades(String pair, BCBotBrain bot){
		this.pair = pair;
		this.bot = bot;
	}

	@Override
	public void sell() {
		bot.analyzers.get(pair).info.sell(bot.gui);	
	}

	@Override
	public void buy() {
		bot.analyzers.get(pair).info.buy(bot.gui);			
	}

}
