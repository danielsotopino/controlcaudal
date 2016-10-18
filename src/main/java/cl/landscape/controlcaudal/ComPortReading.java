package cl.landscape.controlcaudal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class ComPortReading implements Runnable, SerialPortEventListener {
	static CommPortIdentifier portId;
	static Enumeration portList;

	InputStream inputStream;
	SerialPort serialPort;
	Thread readThread;

	/**
	 * pi@raspberrypi:~/projects/control-caudal $ javac -cp
	 * /usr/share/java/RXTXcomm.jar:. ComPortReading.java
	 * pi@raspberrypi:~/projects/control-caudal $ java
	 * -Djava.library.path=/usr/lib/jni -cp /usr/share/java/RXTXcomm.jar:.
	 * ComPortReading RXTX Warning: Removing stale lock file.
	 * /var/lock/LCK..ttyUSB0
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				// if (portId.getName().equals("COM1")) {
				if (portId.getName().equals("/dev/ttyUSB0")) {
					ComPortReading reader = new ComPortReading();

				}
			}
		}
	}

	private BufferedReader input;

	public ComPortReading() {

		try {
			serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
		} catch (PortInUseException e) {
			System.out.println(e);
		}
		try {
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
		} catch (IOException e) {
			System.out.println(e);
		}
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			System.out.println(e);
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

		} catch (UnsupportedCommOperationException e) {
			System.out.println(e);
		}
		readThread = new Thread(this);
		readThread.start();
	}

	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			// byte[] readBuffer = new byte[20];

			try {
				if (input.ready()) {
					System.out.println(input.readLine());
				}
				// while (inputStream.available() > 0) {
				// int numBytes = inputStream.read(readBuffer);
				// }
				// System.out.print(new String(readBuffer));
			} catch (IOException e) {
				System.out.println(e);
			}
			break;
		}
	}
}
