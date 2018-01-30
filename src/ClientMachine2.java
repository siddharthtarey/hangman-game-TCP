/**
 * 
 * ClientMachine.java (client module for the hangman game)
 * 
 * Version 1.0
 * 
 */

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

/**
 * This class acts as a client module for the hangman multiplayer game
 * 
 * @author Swapnil Kamat (snk6855@rit.edu)
 * @author Siddharth Tarey (st2476@rit.edu)
 * 
 *
 */

public class ClientMachine2 {

	public static void main(String[] args) throws Exception {

		ClientMachine client = new ClientMachine();
		
		client.start();

	}

	public void start() throws Exception {
		try {
			//Socket sock = new Socket("localhost", 2304);
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			
			DatagramSocket sock = new DatagramSocket();
			
			
//			DatagramChannel os = DatagramChannel.open();
//			os.connect(new InetSocketAddress("localhost", 2304));
			
			System.out.print("Enter your name: ");
			
			
			Scanner inp = new Scanner(System.in);
			
			String Name = inp.nextLine();
			sendData = Name.getBytes();
			DatagramPacket packet = new DatagramPacket(sendData,sendData.length,IPAddress,9867);
			//OutputStream os = sock.getOutputStream();
			
			
			sock.send(packet);
			
			//os.flush();
			
			//InputStream in = sock.getInputStream();
			
			//byte[] buffer = new byte[1024];
			//ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			int n=0;
			
			String incoming = "";
			byte[] receiveData = new byte[1024];
			DatagramPacket rpacket = new DatagramPacket(receiveData,receiveData.length,IPAddress,9867);
			sock.receive(rpacket);		
			{
				incoming = new String(rpacket.getData(),0,packet.getLength());
				
				if(incoming.contains("Guess: ")){
					System.out.print(incoming);
					Scanner getGuess = new Scanner(System.in);
					String s = getGuess.nextLine();
					packet.setData(s.getBytes());
					sock.send(packet);
					//os.flush();
				}
				else
				{
					System.out.println("here in client1"+incoming);
				}
					
			}
			
		} catch (Exception e) {

		}
	}

}
