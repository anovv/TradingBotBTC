import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BCBotServer {
	
	public BCBotBrain BCBot;
	private String _key = "a0e9e441f8024b509eb298cfb45bac6b";
	private final int port = 1992;
	
	private ServerSocket serverSocket = null;
	
	public BCBotServer(){
		BCBot = new BCBotBrain();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
		}
	}	
	
	public class Session implements Runnable{
		
		Socket connectionSocket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		public Session(Socket connectionSocket){
			
			this.connectionSocket = connectionSocket;
		}
		
		@Override
		public void run() {
			try {
				inputStream = connectionSocket.getInputStream();
				outputStream = connectionSocket.getOutputStream();				
			} catch (IOException e) {
			}
			
			DataInputStream in = new DataInputStream(inputStream);
			DataOutputStream out = new DataOutputStream(outputStream);
			String res = null;
			while(true){
				try {
					res = in.readUTF();
					if(res != null){
						String command = decrypt(res, _key);
						String[] params = command.split("_"); 
						String id = params[0];
						if(id.equals("start")){
							System.out.println("Bot started");
							//TODO launch bot
						}
						
						if(id.equals("stop")){
							System.out.println("Bot stopped");
							/*if(BCBot.analyzer.info.threadUpdate != null && BCBot.analyzer.threadAnalyze != null){
								BCBot.analyzer.info.threadUpdate.stop();
								BCBot.analyzer.threadAnalyze.stop();
							}*/						
						}
						
						if(id.equals("sellAll")){
							//TODO
							System.out.println(id);
						}
						
						if(id.equals("buyAll")){
							//TODO
							System.out.println(id);
						}
						
						if(id.equals("sell")){
							//TODO
							System.out.println(id);
						}
						
						if(id.equals("buy")){
							//TODO
							System.out.println(id);
						}
						
						if(id.equals("cancelOrders")){
							//TODO
							System.out.println(id);
						}
						
						if(id.equals("kill")){
							System.exit(0);
						}
					}
				} catch (IOException e) {
				}
			}
		}		
	}
	
	public void startSession(Socket connectionSocket){
		new Thread(new Session(connectionSocket)).start();
	}
	
	public void launch(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					try {
						System.out.println("Listening for incoming...");
						Socket connectionSocket = serverSocket.accept();
						System.out.println("Successful connection");
						startSession(connectionSocket);
					} catch (IOException e) {
					}
				}
			}
			
		}).start();		
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
	
	private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }
}
