import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;


public class BCBotGUI {
	
	public JFrame mainFrame = new JFrame("Trading Console");
	public JFrame profitFrame = new JFrame("Profit/Loss");
	
	public Map<String, JFrame> progressFrames = new HashMap<String, JFrame>();
	
	final BCBotBrain bot;
				
	Map<String, JTextArea> windows = new HashMap<String, JTextArea>();
	Map<String, JLabel> tags = new HashMap<String, JLabel>();	
	Map<String, Map<String, JButton>> buttons = new HashMap<String, Map<String, JButton>>(); 
	Map<String, JScrollPane> scrolls = new HashMap<String, JScrollPane>();
	
	Map<String, JScrollPane> profitScrolls = new HashMap<String, JScrollPane>();	
	Map<String, JTextArea> profitWindows = new HashMap<String, JTextArea>();	
	Map<String, JCheckBox> checkBoxes = new HashMap<String, JCheckBox>();
	Map<String, JProgressBar> progressBars = new HashMap<String, JProgressBar>();
	
	BCBotGUI(final BCBotBrain bot){
		
		this.bot = bot;	
						
		Map<String, JButton> buttons_btc = new HashMap<String, JButton>();
		buttons_btc.put("start_analyzer", new JButton("Start An"));
		buttons_btc.put("start_updater", new JButton("Start Upd"));
		buttons_btc.put("stop_analyzer", new JButton("Stop An"));
		buttons_btc.put("stop_updater", new JButton("Stop Upd"));
		buttons_btc.put("sell", new JButton("Sell"));
		buttons_btc.put("buy", new JButton("Buy"));
		
		Map<String, JButton> buttons_ltc = new HashMap<String, JButton>();
		buttons_ltc.put("start_analyzer", new JButton("Start An"));
		buttons_ltc.put("start_updater", new JButton("Start Upd"));
		buttons_ltc.put("stop_analyzer", new JButton("Stop An"));
		buttons_ltc.put("stop_updater", new JButton("Stop Upd"));
		buttons_ltc.put("sell", new JButton("Sell"));
		buttons_ltc.put("buy", new JButton("Buy"));
		
		Map<String, JButton> buttons_nvc = new HashMap<String, JButton>();
		buttons_nvc.put("start_analyzer", new JButton("Start An"));
		buttons_nvc.put("start_updater", new JButton("Start Upd"));
		buttons_nvc.put("stop_analyzer", new JButton("Stop An"));
		buttons_nvc.put("stop_updater", new JButton("Stop Upd"));
		buttons_nvc.put("sell", new JButton("Sell"));
		buttons_nvc.put("buy", new JButton("Buy"));
		
		Map<String, JButton> buttons_nmc = new HashMap<String, JButton>();
		buttons_nmc.put("start_analyzer", new JButton("Start An"));
		buttons_nmc.put("start_updater", new JButton("Start Upd"));
		buttons_nmc.put("stop_analyzer", new JButton("Stop An"));
		buttons_nmc.put("stop_updater", new JButton("Stop Upd"));
		buttons_nmc.put("sell", new JButton("Sell"));
		buttons_nmc.put("buy", new JButton("Buy"));
		
		buttons.put("btc_usd", buttons_btc);
		buttons.put("ltc_usd", buttons_ltc);
		buttons.put("nvc_usd", buttons_nvc);
		buttons.put("nmc_usd", buttons_nmc);
		
		windows.put("btc_usd", new JTextArea());
		windows.put("ltc_usd", new JTextArea());
		windows.put("nvc_usd", new JTextArea());
		windows.put("nmc_usd", new JTextArea());
		
		profitWindows.put("btc_usd", new JTextArea());
		profitWindows.put("ltc_usd", new JTextArea());
		profitWindows.put("nvc_usd", new JTextArea());
		profitWindows.put("nmc_usd", new JTextArea());
		
		
		tags.put("btc_usd", new JLabel("btc_usd"));
		tags.put("ltc_usd", new JLabel("ltc_usd"));
		tags.put("nvc_usd", new JLabel("nvc_usd"));
		tags.put("nmc_usd", new JLabel("nmc_usd"));
		
		checkBoxes.put("btc_usd", new JCheckBox("Test"));
		checkBoxes.put("ltc_usd", new JCheckBox("Test"));
		checkBoxes.put("nvc_usd", new JCheckBox("Test"));
		checkBoxes.put("nmc_usd", new JCheckBox("Test"));		
		
		progressBars.put("btc_usd", new JProgressBar());
		progressBars.put("ltc_usd", new JProgressBar());
		progressBars.put("nvc_usd", new JProgressBar());
		progressBars.put("nmc_usd", new JProgressBar());
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(920, 600);		
		profitFrame.setSize(600, 600);	
		
		JTabbedPane tabbedPane = new JTabbedPane();		
		profitFrame.add(tabbedPane);
		
		JPanel panel = new JPanel(); 
        mainFrame.add(panel); 
        
        //
        Iterator ii = progressBars.entrySet().iterator();
        while(ii.hasNext()){
        	Entry entry = (Entry) ii.next();
        	String pair = (String) entry.getKey();
        	JProgressBar pBar = (JProgressBar) entry.getValue();
        	pBar.setStringPainted(true);
            pBar.setMinimum(0);
            pBar.setMaximum(100);  
            pBar.setPreferredSize(new Dimension(400, 20));
        	JPanel p = new JPanel();
        	
        	p.add(pBar);
        	JFrame progressFrame = new JFrame(pair + " testing...");
        	progressFrame.setSize(450, 70);
        	progressFrame.add(p);
        	progressFrames.put(pair, progressFrame);
        }
        //
        
        panel.setLayout(new GridLayout(2, 2));
        
        Iterator i = profitWindows.entrySet().iterator();
        while(i.hasNext()){
        	Entry entry = (Entry) i.next();
        	String pair = (String) entry.getKey();
        	
        	JTextArea display = (JTextArea) entry.getValue();
        	display.setEditable(false);
        	display.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        	JScrollPane scroll = new JScrollPane(display);
        	scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        	profitScrolls.put(pair, scroll);
        	tabbedPane.addTab(pair, scroll);       	
        }
        
        Iterator iterator = windows.entrySet().iterator();
        while(iterator.hasNext()){
        	Entry entry = (Entry) iterator.next();
        	String pair = (String) entry.getKey();
        	JTextArea display = (JTextArea) entry.getValue();
        	display.setEditable(false);
        	display.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        	
        	JScrollPane scroll = new JScrollPane(display);
        	scrolls.put(pair, scroll);
            scrolls.get(pair).setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            //scroll.setSize(new Dimension(1000, 1000));
            JPanel pair_panel = new JPanel();
            pair_panel.setLayout(new BoxLayout(pair_panel, BoxLayout.Y_AXIS)); 
            
            tags.get(pair).setFont(new Font("Lucida Console", Font.PLAIN, 10));
            tags.get(pair).setPreferredSize(new Dimension(30, 30));
        	scroll.setPreferredSize(new Dimension(scroll.WIDTH, scroll.HEIGHT));
        	
            JPanel t = new JPanel();
            t.setLayout(new BoxLayout(t, BoxLayout.X_AXIS));
            t.add(tags.get(pair));
            pair_panel.add(t);            
            pair_panel.add(scroll);
                        
            JPanel bh = new JPanel();
            bh.setLayout(new BoxLayout(bh, BoxLayout.X_AXIS));
            
            buttons.get(pair).get("start_analyzer").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            buttons.get(pair).get("start_updater").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            buttons.get(pair).get("stop_analyzer").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            buttons.get(pair).get("stop_updater").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            buttons.get(pair).get("sell").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            buttons.get(pair).get("buy").setFont(new Font("Lucida Console", Font.PLAIN, 9));
            
            //bh.add(buttons.get(pair).get("start"));
            //bh.add(buttons.get(pair).get("stop"));
            //bh.add(buttons.get(pair).get("sell"));
            //bh.add(buttons.get(pair).get("buy"));
            

            bh.add(buttons.get(pair).get("start_updater"));
            bh.add(buttons.get(pair).get("start_analyzer"));
            bh.add(buttons.get(pair).get("stop_updater"));
            bh.add(buttons.get(pair).get("stop_analyzer"));
            bh.add(buttons.get(pair).get("sell"));
            bh.add(buttons.get(pair).get("buy"));
            
            bh.add(checkBoxes.get(pair));

            buttons.get(pair).get("start_analyzer").setEnabled(false);
            buttons.get(pair).get("stop_analyzer").setEnabled(false);
            buttons.get(pair).get("stop_updater").setEnabled(false);
            buttons.get(pair).get("sell").setEnabled(false);
            buttons.get(pair).get("buy").setEnabled(false);
            
            pair_panel.add(bh);
            
            panel.add(pair_panel);           
        }         
        //add listeners
        
        final BCBotTradeInterface testInterface_btc = new BCBotTradeInterface(){
			@Override
			public void sell() {
				if(bot.analyzers.get("btc_usd").info.amount.get("btc") != 0){
					double temp = bot.analyzers.get("btc_usd").info.amount.get("usd");
					bot.analyzers.get("btc_usd").info.amount.put("usd", temp + bot.analyzers.get("btc_usd").info.amount.get("btc")*bot.analyzers.get("btc_usd").info.weightedSell);
					bot.analyzers.get("btc_usd").info.amount.put("btc" ,0.0);
					System.out.println("Sold at: " + bot.analyzers.get("btc_usd").info.weightedSell);
				}
				else
					System.out.println("No BTC!");
			}

			@Override
			public void buy() {
				if(bot.analyzers.get("btc_usd").info.amount.get("usd") != 0){
					double temp = bot.analyzers.get("btc_usd").info.amount.get("btc");
					bot.analyzers.get("btc_usd").info.amount.put("btc", temp + bot.analyzers.get("btc_usd").info.amount.get("usd")/bot.analyzers.get("btc_usd").info.weightedBuy);
					bot.analyzers.get("btc_usd").info.amount.put("usd", 0.0);	
					System.out.println("Bought at: " + bot.analyzers.get("btc_usd").info.weightedBuy);					
				}
				else
					System.out.println("No USD!");

			}			
		};
        
        Iterator iter = buttons.entrySet().iterator();
        while(iter.hasNext()){
        	Entry entry = (Entry) iter.next();
        	final String pair = (String) entry.getKey();
        	Map<String, JButton> buttons_pair = (Map<String, JButton>) entry.getValue();
        	Iterator it = buttons_pair.entrySet().iterator();
        	while(it.hasNext()){
        		Entry e = (Entry) it.next();
        		final String type = (String) e.getKey();
        		JButton button = (JButton) e.getValue();
        		ActionListener listener = new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent event) {
						if(type.equals("start_updater")){
							
							if(checkBoxes.get(pair).isSelected()){
								bot.analyzers.get(pair).info.testing = true;								
							}else{
								bot.analyzers.get(pair).info.testing = false;								
							}
							
							if(bot.analyzers.get(pair).info.testing){
								bot.analyzers.get(pair).info.generateFromFile(pair);
								
								bot.analyzers.get(pair).tradeInterface = new BCBotTestInterface(pair, bot);
								
								bot.analyzers.get(pair).buyPercentage = bot.testParams.get(pair).get("buyPercentage");
								bot.analyzers.get(pair).sellPercentage = bot.testParams.get(pair).get("sellPercentage");;
								bot.analyzers.get(pair).safePercentage = bot.testParams.get(pair).get("safePercentage");;
								bot.analyzers.get(pair).barrierPercentage = bot.testParams.get(pair).get("barrierPercentage");;

							}else{
								bot.analyzers.get(pair).info.update();	
								
								bot.analyzers.get(pair).tradeInterface = new BCBotTrades(pair, bot);
								
								bot.analyzers.get(pair).buyPercentage = bot.realParams.get(pair).get("buyPercentage");
								bot.analyzers.get(pair).sellPercentage = bot.realParams.get(pair).get("sellPercentage");;
								bot.analyzers.get(pair).safePercentage = bot.realParams.get(pair).get("safePercentage");;
								bot.analyzers.get(pair).barrierPercentage = bot.realParams.get(pair).get("barrierPercentage");;
							
							}
							//bot.analyzers.get(pair).info.generate();
							buttons.get(pair).get("start_analyzer").setEnabled(true);
							buttons.get(pair).get("stop_updater").setEnabled(true);
							
							buttons.get(pair).get("start_updater").setEnabled(false);
							buttons.get(pair).get("sell").setEnabled(true);
							buttons.get(pair).get("buy").setEnabled(true);
							bot.gui.changeText(pair, " Updater started...");
						}
						if(type.equals("start_analyzer")){						
							
							if(bot.analyzers.get(pair).info.testing){
								progressFrames.get(pair).setVisible(true);
							}
						
							bot.analyzers.get(pair).analyze();
							
							buttons.get(pair).get("start_analyzer").setEnabled(false);
							buttons.get(pair).get("stop_analyzer").setEnabled(true);

							bot.gui.changeText(pair, " Analyzer started...");
						}
						
						if(type.equals("stop_updater")){
							
							new Thread(new Runnable(){

								@Override
								public void run() {
									bot.analyzers.get(pair).info.terminate();
									bot.analyzers.get(pair).terminate();
								}
								
							}).start();
							
							buttons.get(pair).get("start_updater").setEnabled(true);
							buttons.get(pair).get("start_analyzer").setEnabled(false);
							buttons.get(pair).get("stop_analyzer").setEnabled(false);
							buttons.get(pair).get("stop_updater").setEnabled(false);
							buttons.get(pair).get("sell").setEnabled(false);
							buttons.get(pair).get("buy").setEnabled(false);		
							
							bot.gui.changeText(pair, " Updater stoped");					
							
						}
						
						if(type.equals("stop_analyzer")){
							
							new Thread(new Runnable(){

								@Override
								public void run() {
									bot.analyzers.get(pair).terminate();
								}
								
							}).start();

							buttons.get(pair).get("start_analyzer").setEnabled(true);
							buttons.get(pair).get("stop_analyzer").setEnabled(false);
							
							bot.gui.changeText(pair, " Analyzer stoped");						
						}
						
						if(type.equals("sell")){
							new Thread(new Runnable(){

								@Override
								public void run() {
									
									buttons.get(pair).get("sell").setEnabled(false);
									buttons.get(pair).get("buy").setEnabled(false);
									//sell procedure
									//bot.analyzers.get(pair).info.sell(BCBotGUI.this);									
									//testInterface_btc.sell();
									bot.analyzers.get(pair).tradeInterface.sell();
									bot.analyzers.get(pair).max = 0;				
									bot.analyzers.get(pair).min = 0;								
									bot.analyzers.get(pair).isBuying = true;
									//sell procedure end
									
									buttons.get(pair).get("sell").setEnabled(true);
									buttons.get(pair).get("buy").setEnabled(true);
								}								
							}).start();
						}
						if(type.equals("buy")){
							new Thread(new Runnable(){

								@Override
								public void run() {
									
									buttons.get(pair).get("buy").setEnabled(false);
									buttons.get(pair).get("sell").setEnabled(false);
									
									//buy procedure
									//bot.analyzers.get(pair).info.buy(BCBotGUI.this);									
									//testInterface_btc.buy();
									bot.analyzers.get(pair).tradeInterface.buy();
									bot.analyzers.get(pair).min = bot.analyzers.get(pair).info.weightedBuy;
									bot.analyzers.get(pair).max = bot.analyzers.get(pair).info.weightedSell;
									bot.analyzers.get(pair).isBuying = false;									
									//buy procedure end									
									buttons.get(pair).get("buy").setEnabled(true);
									buttons.get(pair).get("sell").setEnabled(true);
								}								
							}).start();
						}
					}        			
        		};
        		button.addActionListener(listener);
        	}
        }
	}
	
	public void showGui(){
		mainFrame.setVisible(true);	
		profitFrame.setVisible(true);
	}
	
	public void changeText(String pair, String text){
		int maxRecs = 500;
		String prevText = windows.get(pair).getText();
		String[] strings = prevText.split("\n");
		if(strings.length > maxRecs){
			
			String temp = "\n";
			for(int i = 2; i < strings.length; i++){
				temp = temp + strings[i] + "\n";
			}
			windows.get(pair).setText(temp + text + "\n");	
			scrolls.get(pair).getVerticalScrollBar().setValue(scrolls.get("btc_usd").getVerticalScrollBar().getMaximum());

		}else{
			windows.get(pair).setText(prevText + text + "\n");	
			scrolls.get(pair).getVerticalScrollBar().setValue(scrolls.get("btc_usd").getVerticalScrollBar().getMaximum());
		}
	}
	
	public void changeLabel(String pair, String text){
		tags.get(pair).setText(pair + " " + text);
	}
	
	public void changeProfitInfo(String pair, String text){
		int maxRecs = 500;
		String prevText = profitWindows.get(pair).getText();
		String[] strings = prevText.split("\n");
		if(strings.length > maxRecs){
			
			String temp = "\n";
			for(int i = 2; i < strings.length; i++){
				temp = temp + strings[i] + "\n";
			}
			profitWindows.get(pair).setText(temp + text + "\n");	
			profitScrolls.get(pair).getVerticalScrollBar().setValue(profitScrolls.get("btc_usd").getVerticalScrollBar().getMaximum());

		}else{
			profitWindows.get(pair).setText(prevText + text + "\n");	
			profitScrolls.get(pair).getVerticalScrollBar().setValue(profitScrolls.get("btc_usd").getVerticalScrollBar().getMaximum());
		}
	}
	
	public void saveProfitInfo(String pair, String text){
		BufferedWriter writer = null;
		try{
			String fileName = pair + "_profit_loss.txt";
			File logFile = new File(fileName);
			
			writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.write(text);
			writer.newLine();
		}catch(Exception e){
			
		}finally{
			try{
				writer.close();
			}catch(Exception e){
				
			}
		}
	}	
}
