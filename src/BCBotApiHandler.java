import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class BCBotApiHandler {
	
	private static double fee = 0.004;
	private static volatile long _nonce;
	private static String DOMAIN = "btc-e.com";
		
	BCBotApiHandler(){
		_nonce = System.currentTimeMillis()/1000;
	}
	
	public String getHash(String secret, String input){

		Mac mac;
        String _sign = "";
        try {
            byte[] bytesKey = secret.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, "HmacSHA512" );
            mac = Mac.getInstance( "HmacSHA512" );
            mac.init( secretKey );
            final byte[] macData = mac.doFinal(input.getBytes());
            byte[] hex = new Hex().encode(macData);
            _sign = new String( hex, "ISO-8859-1" );
        } catch(Exception e){
        	System.out.println("Hashing exception" + e.toString());
        	return null;
        }
        return _sign;
		
		/*Mac mac = null;
        String _sign = "";
            byte[] bytesKey = secret.getBytes( );
            final SecretKeySpec secretKey = new SecretKeySpec( bytesKey, "HmacSHA512" );
            try {
				mac = Mac.getInstance( "HmacSHA512" );
			} catch (NoSuchAlgorithmException e) {
				System.out.println("1 " + e.toString());
				return null;
			}
            try {
				mac.init( secretKey );
			} catch (InvalidKeyException e) {
				System.out.println("2 " + e.toString());
				return null;
			}
            final byte[] macData = mac.doFinal( input.getBytes( ) );
            byte[] hex = new Hex( ).encode( macData );
            try {
				_sign = new String( hex, "ISO-8859-1" );
			} catch (UnsupportedEncodingException e) {
				System.out.println("3 " + e.toString());
				return null;
			}
        return _sign;*/
	}
	
	public JSONObject getResponseFromServerForPost(String method, Map<String, String> arguments, String _key, String _secret){
				
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if( arguments == null) {    
			arguments = new HashMap<String, String>();	        
		}	       
		arguments.put( "nonce",  "" + ++_nonce); 
		arguments.put( "method", method); 	    
		
		String postData = "";	 
	    
		for( Iterator argumentIterator = arguments.entrySet().iterator(); argumentIterator.hasNext(); ) {	    
			Map.Entry argument = (Map.Entry)argumentIterator.next();     
	        
			if( postData.length() > 0) {	        
				postData += "&";	            
			}	        
			postData += argument.getKey() + "=" + argument.getValue();	
			params.add(new BasicNameValuePair((String)argument.getKey(), (String)argument.getValue()));
		}
		
		String _sign = getHash(_secret, postData);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://" + DOMAIN + "/tapi");
		
		httppost.addHeader("Key", _key);
		httppost.addHeader("Sign", _sign);
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));
		} catch (Exception e) {		
			System.out.println(e.toString());
		}
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {			
			System.out.println( "Executing post error: " + e.toString());
			return null;
		}
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {			
			System.out.println( "Converting to string exception: " + e.toString());
	        return null;
		}	
		
		if( requestResult != null) {      
			JSONObject jsonResult = null;			
			try {
				jsonResult = new JSONObject(requestResult);
				if (jsonResult.getInt("success") == 0){
					
					String errorMessage = jsonResult.getString("error");  					
					//TODO КОСТЫЛЬ
					String s =  "btc-e.com trade API request failed: " + errorMessage;
					//if(!s.contains("orders")){
					//if(!s.contains("no orders")){
					if(!errorMessage.contains("no orders")){
						System.out.println(s);		
						return null;
					}
					return new JSONObject("{}");
					//return null;
					
				}else{
					return new JSONObject(jsonResult.getString("return"));					
				}
			} catch (Exception e) {				
				//System.out.println( "Converting to json exception: " + e.toString());
		        return null;		
			}
		}			
		return null;
	}
	
	public JSONObject getResponseFromPublicServerUrl(String url){
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url); //Example :  "https://btc-e.com/api/2/btc_usd/ticker"
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {			
			System.out.println( "Executing post error: " + e.toString());
			return null;
		}
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {		
			System.out.println( "Converting to string exception: " + e.toString());
	        return null;
		}					
		
		if( requestResult != null) {       
			JSONObject jsonResult = null;			
			try {				
				return new JSONObject(requestResult);					
			} catch (Exception e) {				
				//System.out.println( "Converting to json exception: " + e.toString());
		        return null;		
			}
		}			
		return null;	
	}
	//old one, don't use it
	/*public JSONObject orderList(long from, long count, long from_id, long end_id, String order, long since, long end, String pair, int active, String _key, String _secret){

		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));} //номер ордера, с которого начинать вывод
		if(count != -1){ params.put("count", String.valueOf(count));} //количество ордеров на вывод
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));} //id ордера, с которого начинать вывод
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));} //id ордера, на которого заканчивать вывод
		if(!order.equals("")) {params.put("order", order);} //сортировка
		if(since != -1){ params.put("since", String.valueOf(since));} //с какого времени начинать вывод
		if(end != -1){ params.put("end", String.valueOf(end));} //на каком времени заканчивать вывод
		if(!pair.equals("")) {params.put("pair", pair);} //пара, по которой выводить ордера
		if(active != -1){ params.put("active", String.valueOf(active));} //вывод только активных ордеров? 0 или 1
		
		return getResponseFromServerForPost("OrderList", params, _key, _secret);			
	}*/
	
	public JSONObject activeOrders(String pair, String _key, String _secret){
		Map<String, String> params = new HashMap<String,String>();
		if(!pair.equals("")) {params.put("pair", pair);} //пара, по которой выводить ордера		
		
		return getResponseFromServerForPost("ActiveOrders", params, _key, _secret);
	}
	
	public JSONObject getInfo(String _key, String _secret){
		
		return getResponseFromServerForPost("getInfo", null, _key, _secret);
	}


	public Map<String, Map<String, String>> getOpenOrdersForPair(String pair, String _key, String _secret){		
		
		//JSONObject jsonResult = orderList(-1, -1, -1, -1, "", -1, -1, pair, 1, _key, _secret);			
		JSONObject jsonResult = activeOrders(pair, _key, _secret);
		if(jsonResult == null)
			return null;
		
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			JSONObject value = null;
			try {
				value = jsonResult.getJSONObject(key);
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			Iterator iterator = value.keys();
			while(iterator.hasNext()){
				String k = (String) iterator.next();
				String v = "";
				try {
					v = value.getString(k);
				} catch (Exception e) {
					//System.out.println(e.toString());					
					return null;
				}
				map.put(k, v);
			}
			result.put(key, map);
		}
		return result;
	}
	
	public Map<String, Double> getTicker(String pair){
		String url = "https://btc-e.com/api/2/" + pair + "/ticker";
		JSONObject jsonResult = getResponseFromPublicServerUrl(url);
		
		Map<String, Double> result = new HashMap<String, Double>();
		JSONObject ticker = null;
		
		try{			
			ticker = jsonResult.getJSONObject("ticker");
			
		}catch(Exception e){
			return null;
		}
		
		Iterator keys = ticker.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			Double value = null;
			try {
				value = ticker.getDouble(key);
			} catch (Exception e) {
				return null;
			}
			result.put(key, value);
		}
				
		return result;
	}
	
	public double getLastSell(String pair){
		Map<String, Double> ticker = getTicker(pair);
		if(ticker == null)
			return -1;
		else
			return ticker.get("sell");
	}
	
	public Map<String, Map<Double, Double>> getOrdersForPair(String pair){
		
		int depth = 2000;
		String url = "https://btc-e.com/api/2/" + pair + "/depth/" + String.valueOf(depth);
		
		JSONObject jsonResult = getResponseFromPublicServerUrl(url);
		JSONArray jsonAsks = null;
		JSONArray jsonBids = null;
				
		try {
			jsonAsks = jsonResult.getJSONArray("asks");
			jsonBids = jsonResult.getJSONArray("bids");		
			
		} catch (Exception e) {
			//System.out.println(e.toString());			
			return null;
		}
		
		Map<Double, Double> asks = new LinkedHashMap<Double, Double>();
		Map<Double, Double> bids = new LinkedHashMap<Double, Double>();
		
		for(int i = 0; i < jsonAsks.length(); i++){
			JSONArray values = null;
			try {
				values = jsonAsks.getJSONArray(i);
				asks.put(Double.valueOf(values.getString(0)), Double.valueOf(values.getString(1)));				
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
		}
		
		for(int i = 0; i < jsonBids.length(); i++){
			JSONArray values = null;
			try {
				values = jsonBids.getJSONArray(i);
				bids.put(Double.valueOf(values.getString(0)), Double.valueOf(values.getString(1)));				
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
		}	
		
		Map<String, Map<Double, Double>> result = new HashMap<String, Map<Double, Double>>();
		result.put("asks", asks);
		result.put("bids", bids);
		
		return result;
	}
	
	public Map<String, Double> getBalance(String _key, String _secret){
		
		Map<String, Double> result = new HashMap<String, Double>();
		JSONObject jsonResult = null;
		try {
			jsonResult = getInfo(_key, _secret).getJSONObject("funds");
		} catch (Exception e) {
			//System.out.println(e.toString());
			return null;
		}
		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			Double value = null;
			try {
				value = jsonResult.getDouble(key);
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
			result.put(key, value);
		}		
		return result;
	}	
	
	public JSONObject trade(String pair, String type, double rate, double amount, String _key, String _secret){
		
		Map<String, String> params = new HashMap<String,String>();		
		
		params.put("pair", pair); 
		params.put("type", type); 
		params.put("rate", String.valueOf(rate));
		params.put("amount", String.valueOf(amount));  		
		
		return getResponseFromServerForPost("Trade", params, _key, _secret); 
	}	
	
	public double getWeightedBuy(String pair, double amount){
		//amount - usd
		Map<String, Map<Double, Double>> orders = getOrdersForPair(pair);
		if(orders == null)
			return -1;
		Map<Double, Double> asks = orders.get("asks");
		
		double currentAsks = amount;
		double weightedSumAsks = 0;
		double sumAsks = 0;
		
		Iterator entriesAsks = asks.entrySet().iterator();		
		while(entriesAsks.hasNext()){			
			Entry<Double, Double> entry = (Entry<Double, Double>) entriesAsks.next();
			double key = entry.getKey();	
			double value = entry.getValue();
			if(amount == 0){
				return key*(1 + fee);
			}
			if(currentAsks <= key*value){
				weightedSumAsks += currentAsks;
				sumAsks += currentAsks/key;
				break;
			}
			weightedSumAsks += key*value;
			sumAsks += value;
			currentAsks -= key*value;		
		}
		
		double weightedBuy = (weightedSumAsks/sumAsks)*(1 + fee);
		return weightedBuy;
	}
	
	public double getWeightedSell(String pair, double amount){
		//amount - btc
		Map<String, Map<Double, Double>> orders = getOrdersForPair(pair);
		if(orders == null)
			return -1;
		
		Map<Double, Double> asks = orders.get("bids");
		
		double currentBids = amount;
		double weightedSumBids = 0;
		double sumBids = 0;
		
		Iterator entriesAsks = asks.entrySet().iterator();		
		while(entriesAsks.hasNext()){			
			Entry<Double, Double> entry = (Entry<Double, Double>) entriesAsks.next();
			double key = entry.getKey();	
			double value = entry.getValue();
			if(amount == 0){
				return key*(1 - fee);
			}
			if(currentBids <= value){
				weightedSumBids += currentBids * key;
				sumBids += currentBids;
				break;
			}
			weightedSumBids += key*value;
			sumBids += value;
			currentBids -= value;
		}
		
		double weightedSell = (weightedSumBids/sumBids)*(1 - fee);
		return weightedSell;
	}		
	
	/*public Map<String, Double> getRealPriceFromServerForPair(String pair, double amount){
		//не использовать - неправильно работает, вместо этого использовать getWeightedBuy/Sell
		Map<String, Map<Double, Double>> orders = getOrdersForPair(pair);
		double currentAsks = amount;
		double currentBids = amount;
		double weightedSumAsks = 0.0;
		double weightedSumBids = 0.0;
		double sumAsks = 0.0;
		double sumBids = 0.0;
		
		Map<Double, Double> asks = orders.get("asks");
		Map<Double, Double> bids = orders.get("bids");
		
		
		Iterator entriesAsks = asks.entrySet().iterator();		
		while(entriesAsks.hasNext()){			
			Entry<Double, Double> entry = (Entry<Double, Double>) entriesAsks.next();
			double value = entry.getValue();
			double key = entry.getKey();			
			if(currentAsks <= value){
				weightedSumAsks += key*currentAsks;
				sumAsks += currentAsks;				
				break;
			}			
			weightedSumAsks += key*value;
			sumAsks += value;
			currentAsks -= value;		
		}		
		double weightedBuy = (weightedSumAsks/sumAsks);//*(1 + fee); //*1.002TODO
		
		Iterator entriesBids = bids.entrySet().iterator();
		while(entriesBids.hasNext()){			
			Entry<Double, Double> entry = (Entry<Double, Double>) entriesBids.next();
			double value = entry.getValue();
			double key = entry.getKey();			
			if(currentBids <= value){
				weightedSumBids += key*currentBids;
				sumBids += currentBids;				
				break;
			}			
			weightedSumBids += key*value;
			sumBids += value;
			currentBids -= value;		
		}		
		double weightedSell = (weightedSumBids/sumBids);//*(1 - fee); //* 0.998TODO
		
		Map<String, Double> result = new HashMap<String, Double>();
		result.put("buy", weightedBuy);
		result.put("sell", weightedSell);
		
		return result;
	}*/	
	
	public JSONObject cancelOrder(String order_id, String _key, String _secret){
		
		Map<String, String> params = new HashMap<String,String>();		
		
		params.put("order_id", order_id);
		
		return 	getResponseFromServerForPost("CancelOrder", params, _key, _secret);				
	}
	
	public boolean sellPair(String pair, double amount, String _key, String _secret, BCBotGUI gui){
		//TODO умножить на 1.002 или 0.998
		Map<String, Map<Double, Double>> orders = getOrdersForPair(pair);
		JSONObject tradeResult = null;
		
		if(orders != null){
			Map<Double, Double> bids = orders.get("bids");
			double currentBids = amount;
			Iterator entriesBids = bids.entrySet().iterator();		
			
			while(entriesBids.hasNext()){			
				Entry<Double, Double> entry = (Entry<Double, Double>) entriesBids.next();
				double key = entry.getKey();	
				double value = entry.getValue();		
				if(currentBids <= value){
					tradeResult = trade(pair, "sell", key, cut(currentBids), _key, _secret);
					
					//make sure all operations are done
					/*while(tradeResult == null){
						tradeResult = trade(pair, "sell", key, cut(currentBids), _key, _secret);						
					}*/
					gui.changeText(pair, " Sell for " + pair + " for " + key + " " + cut(currentBids) + " is ok - last trade");
					break;
				}
				tradeResult = trade(pair, "sell", key, value, _key, _secret);
				
				//make sure all operations are done
				/*while(tradeResult == null){
					tradeResult = trade(pair, "sell", key, cut(currentBids), _key, _secret);						
				}*/			
				gui.changeText(pair, " Sell for " + pair + " for " +  key + " " + value + " is ok - left " + cut(currentBids));
				
				currentBids -= value;
			}			
		}
		if(tradeResult == null)
			return false;
		else
			return true;
	}
	
	public boolean buyPair(String pair, double amount, String _key, String _secret, BCBotGUI gui){
		//TODO умножить на 1.002 или 0.998
		Map<String, Map<Double, Double>> orders = getOrdersForPair(pair);
		JSONObject tradeResult = null;
		if(orders != null){
			Map<Double, Double> asks = orders.get("asks");
			double currentAsks = amount;
			Iterator entriesAsks = asks.entrySet().iterator();		
			
			while(entriesAsks.hasNext()){			
				Entry<Double, Double> entry = (Entry<Double, Double>) entriesAsks.next();
				double key = entry.getKey();	
				double value = entry.getValue();		
				if(currentAsks <= key*value){
					tradeResult = trade(pair, "buy", key, cut(currentAsks/key), _key, _secret);					
					
					//to make sure all operations are done
					/*while(tradeResult == null){
						tradeResult = trade(pair, "buy", key, cut(currentAsks/key), _key, _secret);						
					}*/
					gui.changeText(pair, " Buy for " + pair + " for " + key + " " + cut(currentAsks/key) + " is ok - last trade");
					break;
				}
				tradeResult = trade(pair, "buy", key, value, _key, _secret);
				
				//to make sure all operations are done				
				/*while(tradeResult == null){
					tradeResult = trade(pair, "buy", key, value, _key, _secret);					
				}*/
				gui.changeText(pair, " Buy for " + pair + " for " + key + " " + value + " is ok - left " + currentAsks);
				currentAsks -= key*value;
			}				
		}
		
		if(tradeResult == null)
			return false;
		else
			return true;
	}

	public static double cut(double d){
		String s = String.valueOf(d);
		if(!s.contains(".")){
			return d;
		}else{
			if(s.split("\\.")[1].length() <= 8){
				return d;
			}
			else{
				String temp1 = s.split("\\.")[0];
				String temp2 = s.split("\\.")[1];
				String temp3  = temp2.substring(0, 8);
				return Double.parseDouble(temp1 + "." + temp3);
			}
		}
	}
	
	public JSONObject TradeHistory(long from, long count, long from_id, long end_id, String order, long since, long end, String pair, String _key, String _secret){
		
		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));}
		if(count != -1){ params.put("count", String.valueOf(count));}
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));}
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));}
		if(!order.equals("")) {params.put("order", order);}
		if(since != -1){ params.put("since", String.valueOf(since));}
		if(end != -1){ params.put("end", String.valueOf(end));}
		if(!pair.equals("")) {params.put("pair", pair);}
		
		return getResponseFromServerForPost("TradeHistory", params, _key, _secret);		
	}
	
	public Map<String, Map<String, String>> getTradeHistory(String pair, String _key, String _secret){
		
		int count = 2000;
		
		JSONObject jsonResult = TradeHistory(-1, count, -1, -1, "", -1, -1, pair, _key, _secret);
		if(jsonResult == null)			
			return null;
		
		Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();		
		Iterator keys = jsonResult.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			JSONObject value = null;
			try {
				value = jsonResult.getJSONObject(key);
			} catch (Exception e) {
				//System.out.println(e.toString());				
				return null;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			Iterator iterator = value.keys();
			boolean isYourOrder = false;
			while(iterator.hasNext()){
				String k = (String) iterator.next();
				String v = "";
				try {
					v = value.getString(k);
				} catch (Exception e) {
					//System.out.println(e.toString());					
					return null;
				}
				if(k.equals("is_your_order") && v.equals("1")){
					isYourOrder = true;
				}
				map.put(k, v);
			}
			if(isYourOrder){
				result.put(key, map);
			}
			
		}
		
		return sortByTimestamp(result);	
	}
	
	public Map<String, Map<String, String>> sortByTimestamp(Map<String, Map<String, String>> map){
		
		Map<String, Map<String, String>> sorted = new LinkedHashMap<String, Map<String, String>>();
		while(!map.isEmpty()){
			Iterator i = map.keySet().iterator();
			String temp_timestamp = "0";
			String temp_key = "";
			while(i.hasNext()){
				String key = (String)i.next();
				Map<String, String> m = map.get(key);
				String timestamp = m.get("timestamp");
				if(Long.parseLong(timestamp) >= Long.parseLong(temp_timestamp)){
					temp_timestamp = timestamp;
					temp_key = key;
				}
			}
			Map<String, String> t = map.get(temp_key);
			map.remove(temp_key);
			sorted.put(temp_key, t);
		}
		
		return sorted;
		
	}
	
	public static void main(String[] args){
		String key = "ZCTTWEZ4-0MGQDKVR-XFYCC3Y6-IWJGZIAH-W9ZWQQV3";
		String secret = "f0d8a54e27b2075b0ba39a85ea406c07aed27328f12dba70784b60873482d4af";
		//System.out.println(new BCBotApiHandler().getTradeHistory("btc_usd", key, secret));
		BCBotApiHandler api = new BCBotApiHandler();
		System.out.println(api.trade("nmc_btc", "sell", 0.001, 1, key, secret));
		//System.out.println(new BCBotApiHandler().getTradeHistory("btc_usd", key, secret));
	}
}
