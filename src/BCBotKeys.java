import java.util.HashMap;
import java.util.Map;


public class BCBotKeys {
	
	public static String btc_usd_info_key = "VFONV7X5-B6WXUTO2-IZEYY9OK-YY32C1SQ-5D81PJ1L";
	public static String btc_usd_info_secret = "b89cd75b02bd4eef6122de4f74ebc403d60453f3f1a6be567fecf106071bd04b";	
	
	public static String ltc_usd_info_key = "";
	public static String ltc_usd_info_secret = "";	
	
	public static String nvc_usd_info_key = "";
	public static String nvc_usd_info_secret = "";	
	
	public static String nmc_usd_info_key = "";
	public static String nmc_usd_info_secret = "";	
	
	public static String btc_usd_trade_key = "AZ85EVSK-0JAVZUV0-4WYCCPES-TQL8H5OK-3BAAISJ6";
	public static String btc_usd_trade_secret = "600c3915f168e66059582d8de62ca7974e97c0cd1610f624fa922f8b60f77e57";
	
	public static String ltc_usd_trade_key = "";
	public static String ltc_usd_trade_secret = "";
	
	public static String nvc_usd_trade_key = "";
	public static String nvc_usd_trade_secret = "";
	
	public static String nmc_usd_trade_key = "";
	public static String nmc_usd_trade_secret = "";
	
	public Map<String, Map<String, String>> trade_keys;
	public Map<String, Map<String, String>> info_keys;
	
	public BCBotKeys(){
		trade_keys = new HashMap<String, Map<String, String>>();
		
		Map<String, String> btc_usd_trade = new HashMap<String, String>();
		Map<String, String> ltc_usd_trade = new HashMap<String, String>();
		Map<String, String> nvc_usd_trade = new HashMap<String, String>();
		Map<String, String> nmc_usd_trade = new HashMap<String, String>();
		
		btc_usd_trade.put("key", btc_usd_trade_key);
		btc_usd_trade.put("secret", btc_usd_trade_secret);
		
		ltc_usd_trade.put("key", ltc_usd_trade_key);
		ltc_usd_trade.put("secret", ltc_usd_trade_secret);
		
		nvc_usd_trade.put("key", nvc_usd_trade_key);
		nvc_usd_trade.put("secret", nvc_usd_trade_secret);
		
		nmc_usd_trade.put("key", nmc_usd_trade_key);
		nmc_usd_trade.put("secret", nmc_usd_trade_secret);
		
		trade_keys.put("btc_usd", btc_usd_trade);
		trade_keys.put("ltc_usd", ltc_usd_trade);
		trade_keys.put("nvc_usd", nvc_usd_trade);
		trade_keys.put("nmc_usd", nmc_usd_trade);
		
		info_keys = new HashMap<String, Map<String, String>>();
		
		Map<String, String> btc_usd_info = new HashMap<String, String>();
		Map<String, String> ltc_usd_info = new HashMap<String, String>();
		Map<String, String> nvc_usd_info = new HashMap<String, String>();
		Map<String, String> nmc_usd_info = new HashMap<String, String>();
		
		btc_usd_info.put("key", btc_usd_info_key);
		btc_usd_info.put("secret", btc_usd_info_secret);
		
		ltc_usd_info.put("key", ltc_usd_info_key);
		ltc_usd_info.put("secret", ltc_usd_info_secret);
		
		nvc_usd_info.put("key", nvc_usd_info_key);
		nvc_usd_info.put("secret", nvc_usd_info_secret);
		
		nmc_usd_info.put("key", nmc_usd_info_key);
		nmc_usd_info.put("secret", nmc_usd_info_secret);
		
		info_keys.put("btc_usd", btc_usd_info);
		info_keys.put("ltc_usd", ltc_usd_info);
		info_keys.put("nvc_usd", nvc_usd_info);
		info_keys.put("nmc_usd", nmc_usd_info);		
	}
}
