import java.net.*;
import java.io.*;
public class client {
	public static void main (String args[]) {
		// arguments supply message and hostname
		Socket s = null;
	String j;
	
	Console console = System.console();
        if (console == null) {
            System.out.println("Unable to fetch console");
            return;
        }
       
		for(;;){
        System.out.println("enter string to socket \n" );
        String line = console.readLine();
        console.printf("written to socket : %s", line);
		
		
		try{
			int serverPort = 7897;
			s = new Socket(line, serverPort);    
			DataInputStream in = new DataInputStream( s.getInputStream());
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
			out.writeUTF(args[0]);      	// UTF is a string encoding see Sn. 4.4
			String data = in.readUTF();	    // read a line of data from the stream
			System.out.println("Received: "+ data) ; 
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}
		System.out.println("wana send more ????yes no ??\n" );	
	 j = console.readLine();
	 
	 if(j=="0")
	 break;
		 
		}
		}
	
	 
	 }
