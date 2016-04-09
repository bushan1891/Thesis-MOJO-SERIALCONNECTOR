import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.ui.bean.Map;

public class SerialTest implements SerialPortEventListener {
	static SerialPort serialPort;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/**
	 * A BufferedReader which will be fed by a InputStreamReader converting the
	 * bytes into characters making the displayed results codepage independent
	 */
	Map m;
	Envelope e;
	Integer fieldLevel; // field of operation
	double z;

	static String inputLine = "";
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize(Map map, Envelope env, Integer fl) {
		// the next line is for Raspberry Pi and
		// gets us into the while loop and was suggested here was suggested
		// http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
		// System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		m = map;
		e = env;
		fieldLevel = fl;
		double z = 0;
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(
					serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e1) {
			System.err.println(e1.toString());
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	public synchronized static void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				inputLine = input.readLine();
				System.out.println(inputLine);
				int n = Integer.parseInt(inputLine);
				System.out.println("Serial in data" + n);
				System.out.println("Level of operaton" + fieldLevel);
				// m.zoom(n);

				if (n > (1 + fieldLevel) && n < (5 + fieldLevel))
					z = 0.2;
				else if (n > (5 + fieldLevel) && n < (10 + fieldLevel))
					z = 0.4;
				else if (n > (10 + fieldLevel) && n < (15 + fieldLevel))
					z = 0.6;
				else if (n > (15 + fieldLevel) && n < (20 + fieldLevel))
					z = 0.8;
				else if (n > (20 + fieldLevel) && n < (25 + fieldLevel))
					z = 1;
				else if (n > (25 + fieldLevel) && n < (30 + fieldLevel))
					z = 2;
				/*else if (n > (30 + fieldLevel) && n < (35 + fieldLevel))
					z = 7;*/
				
				m.setExtent(e);
				m.zoom(z); // scale is scale factor in pixels
				m.redraw();

			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	public static void test(Map map, Envelope env, Integer fieldL)
			throws Exception {
		SerialTest main = new SerialTest();
		if(fieldL==0)
			fieldL=0;
		else if (fieldL==1)
			fieldL=20;
		else if(fieldL==2)
			fieldL=40;
		if(fieldL==100){
		main.close();
		}
		System.out.println("**************Refreshed*******************");
		System.out.println("value of field " +fieldL );
		main.fieldLevel=fieldL;
		main.initialize(map, env, fieldL);
		Thread t = new Thread() {
			public void run() {
				// the following line will keep this app alive for 1000 seconds,
				// waiting for events to occur and responding to them (printing
				// incoming messages to console).
				try {
					Thread.sleep(1000000);
					System.out.println("i am called too ");

				} catch (InterruptedException ie) {
				}
			}
		};
		t.start();
		System.out.println("Started");
		// int i = Integer.parseInt(inputLine);

	}
}
