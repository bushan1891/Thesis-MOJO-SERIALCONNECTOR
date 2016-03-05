

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import com.diegocueva.serial.datain.*;
import com.diegocueva.serial.datain.Log;


/**
 * Stand alone starter
 * 
 * @author Diego Cueva
 *
 */
public class DataIn {
	
	public static void main(String arg[]) throws Exception{
		Log.debug("Ports found:");
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()){
			CommPortIdentifier port = ports.nextElement(); 
			Log.debug("\tPORT: "+port.getName());
		}
		
		Log.debug( "Starting console..." );
		Console console = new Console();

		console.begin();
	}		

}

/*
java.lang.UnsatisfiedLinkError: D:\java16\jre\bin\rxtxSerial.dll: Can't load IA 32-bit .dll on a AMD 64-bit platform thrown while loading gnu.io.RXTXCommDriver
Exception in thread "main" java.lang.UnsatisfiedLinkError: D:\java16\jre\bin\rxtxSerial.dll: Can't load IA 32-bit .dll on a AMD 64-bit platform
*/