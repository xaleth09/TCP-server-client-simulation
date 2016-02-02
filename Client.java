import java.net.*;
import java.io.*;

public class Client {

	public static void main(String args[]){

		String host;
		int port;
		if(args.length == 1){
			host = args[0];
			port = 58345;
		}else if(args.length == 2){
			host = args[0];
			port = Integer.parseInt(args[1]);
		}else{
			host = "localhost";
			port = 58345;
		}

		try{
			Socket mySock = new Socket(host, port);
			PrintWriter out = new PrintWriter(mySock.getOutputStream(),true);
			BufferedReader in = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			String userIn;
			System.out.printf("Send: ");
			while((userIn = stdIn.readLine()) != null){
				out.println(userIn);
				
				if(userIn.toLowerCase().equals("close")){
					mySock.close();
					return;
				}
				
				System.out.println("echo: " + in.readLine());
				System.out.printf("Send: ");
			}
			
		}catch(IOException e){
			System.err.println("Couldn't get io from server");
		}
	}
	
}
