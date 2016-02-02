import java.net.*;
import java.io.*;

public class Server2 {
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
			inLine = in.readLine();
			System.out.println("serv: "+inLine);
			//CONNECTION SETUP PHASE
			String[] parsedMsg = inLine.split(" ");
	
			
			char phase = ' ';
			String measureT = "";
			int numProbes = 0;
			int mesSize = 0;
			int servDelay = 0;
			
			if(parsedMsg.length == 5){
			    phase = parsedMsg[0].toCharArray()[0];
			    measureT = parsedMsg[1];
			    numProbes = Integer.parseInt(parsedMsg[2]);
			    if(parsedMsg[3].contains("k")){
                    parsedMsg[3] = parsedMsg[3].replace("k", "");
			    }else{
			        mesSize = Integer.parseInt(parsedMsg[3])*1000;
			    }
			    servDelay = Integer.parseInt(parsedMsg[4]);
			}else{
				out.println("404 ERROR: Invalid Connection Setup Message1");
			    clientSocket.close();
			}
						
			if(phase == 's' && (measureT.equals("rtt") || measureT.equals("tput"))){
				out.println("200 OK: Ready");
			}else{
				out.println("404 ERROR: Invalid Connection Setup Message2");
				clientSocket.close();
			}
			
			
			//MEASUREMENT PHASE
			int probeCount = 0;
			for(int i = 1; i <= numProbes; i++){
			   inLine = in.readLine();
			   System.out.println("servM: "+inLine);
			
			   parsedMsg = inLine.split(" ");
			    if(parsedMsg.length == 3){
			            probeCount = Integer.parseInt(parsedMsg[1]);
			    }else{
			        out.println("404 ERROR: Invalid Connection Setup Message3");
				    clientSocket.close();
			    }

			    if(probeCount != i){
			        out.println("404 ERROR: Invalid Connection Setup Message4");
				    clientSocket.close();
			    }else{
			        out.println("echo: "+ inLine);
			    }
			}
			
			
            //TERMINATION PHASE
                inLine = in.readLine();
                if(inLine.equals("t")){
                    out.println("200 OK: Closing Connection");
                    clientSocket.close();
                }else{
                    out.println("404 ERROR: Invalid Connection Termination Message5");
                    clientSocket.close();
                }
			
			
		}catch(IOException e){
			System.err.println("Can't listen to port");
		}
	}
}
