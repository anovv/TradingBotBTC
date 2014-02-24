import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;




public class BCBotClient {

	private Socket clientSocket = null;
	private String host = "127.0.0.1";
	private final int port = 1992;
	private static String _key = "a0e9e441f8024b509eb298cfb45bac6b";
	//_key = UUID.randomUUID().toString().replaceAll("-", ""); keygen	
	//public InputStream inputStream = null;
	//public OutputStream outputStream = null;
	
	public DataInputStream in = null;
	public DataOutputStream out = null;
	
	public void startBot(){
		String command = encrypt("start", _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}
	}
	
	public void stopBot(){
		String command = encrypt("stop", _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {			
		}	
	}
	
	public void killBot(){
		String command = encrypt("kill", _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}
	}
	
	public void sellAll(String pair){
		String command = encrypt("sellAll_" + pair, _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}	
	}
	
	public void buyAll(String pair){
		String command = encrypt("buyAll_" + pair, _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}	
	}
	
	public void sell(String currency, String amount){
		String command = encrypt("sell_" + currency + "_" + amount, _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}
	}
	
	public void buy(String currency, String amount){
		String command = encrypt("buy_" + currency + "_" + amount, _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}
	}
	
	public void cancelOrders(){
		String command = encrypt("cancelOrders", _key);
		try {
			out.writeUTF(command);
		} catch (IOException e) {
		}
	}	
	
	public static String encrypt(String value, String key){
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = null;
        byte[] encrypted = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
			encrypted = cipher.doFinal(value.getBytes());	        
		} catch (Exception e) {			
		}
        return byteArrayToHexString(encrypted);
    }    
	
	private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }
    
    private static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }   
    
    public static String decrypt(String message, String key){
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = null;
        byte[] decrypted = null;
        try{
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        decrypted = cipher.doFinal(hexStringToByteArray(message));
        }catch(Exception e){        	
        }
        return new String(decrypted);
    }   
	
	
	
	public void connect(){
		
		try {
			clientSocket = new Socket(InetAddress.getByName(host), port);
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static void main(String[] args){

		//String s1 = "smtp.qip.ru";
		String s2 = "secretsend@mail15.com";
		String s3 = "secretsend123";
		//String s4 = "2525";
		String s5 = "secretreceive123@mail333.com";
		String s6 = "secretreceive321@mail333.com";
		
		//System.out.println(encrypt(s1, _key));
		System.out.println(encrypt(s2, _key));
		System.out.println(encrypt(s3, _key));
		//System.out.println(encrypt(s4, _key));
		System.out.println(encrypt(s5, _key));
		System.out.println(encrypt(s6, _key));
		
	}
}
