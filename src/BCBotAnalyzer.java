public class BCBotAnalyzer {
	BCBotDataManager info = null;
	public volatile double min = 0;
	public volatile double max = 0;
	public volatile boolean isBuying = true;
	double buyPercentage = 0;
	double sellPercentage = 0;
	double safePercentage = 0;
	double barrierPercentage = 0; 
		
	String pair;
	
	public Thread threadAnalyze = null;
	
	public volatile boolean running = true;
	
	//final BCBotTradeInterface tradeInterface;
	BCBotTradeInterface tradeInterface;
	public BCBotGUI gui = null;
	
	public BCBotAnalyzer(BCBotGUI gui, BCBotTradeInterface tradeInterface, String pair, double buyPercentage, double sellPercentage, double safePercentage, double barrierPercentage){
		this.gui = gui;
		this.tradeInterface = tradeInterface;
		this.pair = pair;
		this.buyPercentage = buyPercentage;
		this.sellPercentage = sellPercentage;
		this.safePercentage = safePercentage;
		this.barrierPercentage = barrierPercentage;
		info = new BCBotDataManager(pair, gui, this);
	}	 	
	
	public void terminate(){
		running = false;
		try {
			if (threadAnalyze != null)
				threadAnalyze.join();
		} catch (Exception e) {
			
		}
		running = true;
	}
	
	public void analyzeHelper(BCBotTradeInterface tradeInterface){
		if (isBuying) {
			// Initialize variables
			if (max == 0 && min == 0) {
				max = info.weightedBuy;
				min = info.weightedBuy;
			}
			
			// If weightedBuy is bigger than max, lift both min and max
			if (info.weightedBuy > max) {
				max = info.weightedBuy;
				min = info.weightedBuy;
			}
					
			// If weightedBuy is smaller than min, lower min value
			if (info.weightedBuy < min) {
				min = info.weightedBuy;
			}
			
			boolean isDeltaBiggerThanBarrier = (max - min) > 1.5;//max * barrierPercentage;
			boolean isWeightedBuyConformingToPercentage = info.weightedBuy > min + buyPercentage*(max - min);
					
			// Check when to buy
			if (isDeltaBiggerThanBarrier && isWeightedBuyConformingToPercentage){
				tradeInterface.buy();				
				min = info.weightedBuy;
				max = info.weightedSell;
				isBuying = false;
			}
		} else {
			// If weightedSell is bigger than max, lift max
			if (info.weightedSell > max) {
				max = info.weightedSell;				
			}				
			
			// Check for safe sell
			if (max < min) {			
				//if (max - info.weightedSell > max * safePercentage) {	
				//comparing with max order
				if (max - info.lastSell > max * safePercentage) {	
					tradeInterface.sell();					
					max = 0;					
					min = 0;					
					isBuying = true;
					info.safeSellHistory.add(System.currentTimeMillis());
					gui.changeText(pair, " Safe sell");				
				}				
			} else {				
				//if (min - info.weightedSell > min * safePercentage) {
				//comparing with max order				
				if (min - info.lastSell > min * safePercentage) {
					tradeInterface.sell();					
					max = 0;					
					min = 0;					
					isBuying = true;
					info.safeSellHistory.add(System.currentTimeMillis());
					gui.changeText(pair, " Safe sell");					
				}								
			}
			
			boolean isDeltaPositive = max > min;
			boolean isWeightedSellConformingToPercentage = info.weightedSell < max - sellPercentage*(max - min);
				
			// Check when to sell
			if (isDeltaPositive && isWeightedSellConformingToPercentage) {			
				tradeInterface.sell();				
				max = 0;				
				min = 0;				
				isBuying = true;			
			}	
		}
	}
	
	public void analyze(){

		//info.update();
		threadAnalyze = new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("Analyzer started...");
				while(running){
					if(info != null)
						analyzeHelper(tradeInterface);
					else
						System.out.println("No info");
				}
			}			
		});
		threadAnalyze.start();
	}
}
