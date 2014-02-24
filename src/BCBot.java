import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.json.JSONObject;



public class BCBot {
	

	private static long _nonce;
	private static String DOMAIN = "btc-e.com";
	private static String _key = "FQVKVSPR-PLVZRME8-X1MWGK3X-U1Z0657U-DTYEG8SH";
	private static String _secret = "6fe5a96ce093f27825ee9bda0dff1106506a456917b11d99dd4a5d79bbd70933";
	
	private static volatile JSONObject orders;
	private static volatile JSONObject info;
	private static volatile JSONObject orderList;
	
	private static BCBot _instance = null;
	
	private BCBot(){
		_nonce = System.currentTimeMillis()/1000 + 10000;		
	}
	
	public static synchronized BCBot getInstance(){
		if(_instance == null)
			_instance = new BCBot();
		return _instance;
	}
	
	public void update(){
		Thread updateThread = new Thread(new Runnable(){
			@Override
			public void run() {
				updateHelper();
			}			
		});
		updateThread.start();
	}
	
	public void upd(){
		info = getInfo();
	}
	
	public void updateHelper(){
		//System.out.println("running...");
		
		Thread ordersThread = new Thread(new Runnable(){
			@Override
			public void run() {
				orders = getOrders("btc_usd", 100);
			}			
		});
		
		Thread infoThread = new Thread(new Runnable(){
			@Override
			public void run() {
				info = getInfo();
			}			
		});
		
		Thread orderListThread = new Thread(new Runnable(){
			@Override
			public void run() {
				orderList = OrderList(-1,-1,-1,-1,"",-1,-1,"",-1);
			}
		});
		
		ordersThread.start();
		infoThread.start();
		orderListThread.start();
		updateHelper();		
	}
	
	private final JSONObject HTTPRequest( String method, Map<String, String> arguments) {
	
		//System.out.println("Nonce: " + _nonce);		
		HashMap<String, String> headerLines = new HashMap<String, String>();  // Create a new map for the header lines.	    

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if( arguments == null) {  // If the user provided no arguments, just create an empty argument array.	  
			arguments = new HashMap<String, String>();	        
		}	       
		arguments.put( "nonce",  "" + ++_nonce);  // Add the dummy nonce.		
		arguments.put( "method", method);  // Add the method to the post data.	    
		
		String postData = "";	 
	    
		for( Iterator argumentIterator = arguments.entrySet().iterator(); argumentIterator.hasNext(); ) {	    
			Map.Entry argument = (Map.Entry)argumentIterator.next();     
	        
			if( postData.length() > 0) {	        
				postData += "&";	            
			}	        
			postData += argument.getKey() + "=" + argument.getValue();	
			params.add(new BasicNameValuePair((String)argument.getKey(), (String)argument.getValue()));
		} 
				
		//System.out.println("Postdata: " + postData);
		
		// Encode the post data by the secret and encode the result as base64.	
		
		String _sign = getHash(_secret, postData);
			    
		// Now do the actual request
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://" + DOMAIN + "/tapi");
		
		httppost.addHeader("Key", _key);
		httppost.addHeader("Sign", _sign);
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
		
			System.out.println(e.toString());
		}
		
		//System.out.println("Key: " + _key);
		//System.out.println("Sign: " + _sign);
		
		
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
		
		
		if( requestResult != null) {   // The request worked    
			JSONObject jsonResult = null;			
			try {
				jsonResult = new JSONObject(requestResult);
				if (jsonResult.getInt("success") == 0){
					
					String errorMessage = jsonResult.getString( "error");            
					System.out.println( "btc-e.com trade API request failed: " + errorMessage);	                
					return null;
				}else{
					
					return new JSONObject(jsonResult.getString("return"));
				}
			} catch (Exception e) {
				
				System.out.println( "Converting to json exception: " + e.toString());
		        return null;		
			}
		}			
		return null;			
	}	

	public String getHash(String secret, String input){

		Mac mac;
        String _sign = "";
        try {
            byte[] bytesKey = secret.getBytes( );
            final SecretKeySpec secretKey = new SecretKeySpec( bytesKey, "HmacSHA512" );
            mac = Mac.getInstance( "HmacSHA512" );
            mac.init( secretKey );
            final byte[] macData = mac.doFinal( input.getBytes( ) );
            byte[] hex = new Hex( ).encode( macData );
            _sign = new String( hex, "ISO-8859-1" );
        } catch(Exception e){
        	System.out.println("Hashing exception" + e.toString());
        	return null;
        }
        return _sign;	
	}
	
	/*public static void main(String[] args){

		BCBot test = getInstance();
		test.update();
		//test.info = test.getInfo();
		//test.upd();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		if (test.info == null)
			System.out.println("null");
		else
			System.out.println(test.info.toString());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		if (test.info == null)
			System.out.println("null");
		else
			System.out.println(test.info.toString());
		}*/
	
	public JSONObject getInfo(){
		return HTTPRequest("getInfo", null);
	}
	
	//all params are optional
	public JSONObject TransHistory(long from, long count, long from_id, long end_id, String order, long since, long end){
		
		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));}
		if(count != -1){ params.put("count", String.valueOf(count));}
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));}
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));}
		if(!order.equals("")) {params.put("order", order);}
		if(since != -1){ params.put("since", String.valueOf(since));}
		if(end != -1){ params.put("end", String.valueOf(end));}
		
		return HTTPRequest("TransHistory", params);		
	}
	
	//all params are optional
	public JSONObject TradeHistory(long from, long count, long from_id, long end_id, String order, long since, long end, String pair){
	
		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));}
		if(count != -1){ params.put("count", String.valueOf(count));}
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));}
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));}
		if(!order.equals("")) {params.put("order", order);}
		if(since != -1){ params.put("since", String.valueOf(since));}
		if(end != -1){ params.put("end", String.valueOf(end));}
		if(!pair.equals("")) {params.put("pair", pair);}
		
		return HTTPRequest("TradeHistory", params);		
	}
	
	public JSONObject OrderList(long from, long count, long from_id, long end_id, String order, long since, long end, String pair, int active){

		Map<String, String> params = new HashMap<String,String>();
		
		if(from != -1){ params.put("from", String.valueOf(from));}
		if(count != -1){ params.put("count", String.valueOf(count));}
		if(from_id != -1){ params.put("from_id", String.valueOf(from_id));}
		if(end_id != -1){ params.put("end_id", String.valueOf(end_id));}
		if(!order.equals("")) {params.put("order", order);}
		if(since != -1){ params.put("since", String.valueOf(since));}
		if(end != -1){ params.put("end", String.valueOf(end));}
		if(!pair.equals("")) {params.put("pair", pair);}
		if(active != -1){ params.put("active", String.valueOf(active));}
		
		return HTTPRequest("OrderList", params);			
	}
	
	public JSONObject getOrders(String pair, int depth){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://" + DOMAIN + "/api/2/" + pair + "/depth/" + String.valueOf(depth));
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
		
		if( requestResult != null) {   // The request worked    
			JSONObject jsonResult = null;			
			try {
				return new JSONObject(requestResult);				
			} catch (Exception e) {				
				System.out.println( "Converting to json exception: " + e.toString());
		        return null;		
			}
		}			
		return null;
	}
	
	public JSONObject getTicker(String pair){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://" + DOMAIN + "/api/2/" + pair + "/ticker");
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
		
		if( requestResult != null) {   // The request worked    
			JSONObject jsonResult = null;			
			try {
				return new JSONObject(requestResult);				
			} catch (Exception e) {				
				System.out.println( "Converting to json exception: " + e.toString());
		        return null;		
			}
		}			
		return null;
	}	
}

