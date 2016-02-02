import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Client2 {

	public static void main(String args[]){

		String host;
		int port;
		
		//Takes given commandLine args and stores in Host and Port respectively
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
			
		//Connection Setup Phase	
		    //<PROTOCOL PHASE><WS><MEASUREMENT TYPE><WS><NUMBER OF PROBES><WS><MESSAGE SIZE><WS><SERVER DELAY>\n
			
			System.out.println("Please enter the number, char, or string with spaces in place of <WS> corresponding to:\n"
					+ "<PROTOCOL PHASE><WS><MEASUREMENT TYPE><WS><NUMBER OF PROBES><WS><MESSAGE SIZE><WS><SERVER DELAY>");
			
			userIn = stdIn.readLine();
	
	        //Takes userIn and extracts and initializes variables used later for Measurement Phase
			String [] split = userIn.split(" ");		
			int numProbes = 0, mesSize = 0, servDelay = 0;
			String measureT = "";
			if(split.length == 5){
				measureT = split[1];
			    numProbes = Integer.parseInt(split[2]);
			    if(split[3].contains("k")){
                    split[3] = split[3].replace("k", "");
                    mesSize = Integer.parseInt(split[3])*1000;
			    }else{
			        mesSize = Integer.parseInt(split[3]);
			    }
			    servDelay = Integer.parseInt(split[4]);
			}
			
			//Creates pay load based on userIn
			//   since a char is 2bytes in java i halve the message size given from input
			String payload = "";
			String tmp = "";
			if(mesSize == 1){
			    payload = "o";
			}else if(mesSize <= 1000){
			    for(int i = 0; i < mesSize/2; i++){
			        payload = payload.concat("o");
			    }
			}else if(mesSize > 1000){
			    for(int i = 0; i < 500; i++){
			        tmp = tmp.concat("o");
			    }
			    
			    for(int j = 0; j < (mesSize)/1000; j++ ){
			    	payload = payload.concat(tmp);
			    }
			    
			}
			
			
		    //Fixes userIn that has 1k,2k etc and changes to 1000,2000 etc.
		    userIn = "";
		    for(int i = 0; i < split.length; i++){
		    	userIn = userIn.concat(split[i]);
		    	if(i != split.length-1){
		    	    userIn = userIn.concat(" ");
		    	}
            }
          
			out.println(userIn);
			String reply = in.readLine();
			if(reply.equalsIgnoreCase("200 OK: Ready") || reply.equalsIgnoreCase("200 OK:Ready")){
				System.out.println("Starting Measurement Phase");
			}else{
				mySock.close();
				System.out.println(reply);
				System.out.println("Invalid or Incomplete Message, Closing Connection");
				return;
			}
			
			
	    //Measurement Phase
			
			//<PROTOCOL PHASE><WS><PROBE SEQUENCE NUMBER><WS><PAYLOAD>


            //Starts Measurement Phase by sending payload with user given
            //  delay, marking the time sent(start) and time recieved(end)
            //  and stores end-start time for later calulation
			long start = 0, end = 0;
			long times [] = new long [numProbes];
			for(int i = 1; i <= numProbes; i++){			
			    userIn = "";
			    userIn = userIn.concat("m "+i+" "+payload);
			    start = System.currentTimeMillis();
			    out.println(userIn);
			    TimeUnit.MILLISECONDS.sleep(servDelay);
			    System.out.println(in.readLine()+"\n");
			    end = System.currentTimeMillis();
			    times[i-1] = end-start;
			}
			
			
			//Calculates RTT or TPUT from timestamps saved in times[]
			float rtt = 0;
			float tput [] = new float[times.length];
			float tpSum = 0;
			if(measureT.equals("rtt")){
				for(int i = 0; i < times.length; i++){
					rtt += times[i];
				}
				rtt = rtt/(float)times.length;
				System.out.println("\nMean RTT is: "+rtt+" milliseconds");
			}else if(measureT.equals("tput")){
				for(int i = 0; i < times.length; i++){
					tput[i] = mesSize/(float)times[i];
				}
				
				for(int i = 0; i < tput.length; i++){
					tpSum += tput[i];
				}
				
				tpSum = tpSum/(float)tput.length;
				tpSum = tpSum*8;
				System.out.println("\nMean TPUT is: "+ tpSum+" bits per millisecond");				
			}
			
			System.out.println("Based on "+numProbes+" probes, "+mesSize+" byte messageSize, with "+servDelay+" millisecond server delay");
						
		//Connection Termination Phase
		    //sends 't' to server to mark termination phase
		    //prints out message recieved, but closes either way
		    //whether given 200 OK, or 404 Error
			out.println("t");
		
			//System.out.println(in.readLine());
			mySock.close();
			
			
		}catch(IOException | InterruptedException e){
			System.err.println("Couldn't get io from server");
		}
	}
	
}

