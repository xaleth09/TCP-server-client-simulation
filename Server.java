import java.net.*;
import java.io.*;

public class Server {
	public static void main(String args []){
		
		int port; 

		if(args.length > 0){
			port = Integer.parseInt(args[0]);
		}else{
		    port = 58345;
			System.out.println("Choosing Default port: " + port);
		}
		
		
		try{
			ServerSocket servSock = new ServerSocket(port);
			Socket clientSocket = servSock.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String inLine;
			while( (inLine = in.readLine()) != null ){
				System.out.println("sSide: "+inLine);				
				out.println(inLine.toUpperCase());
			}
		}catch(IOException e){
			System.err.println("Can't listen to port");
		}
	}
}
