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

public class ClientMachine4 {

	public static void main(String[] args) throws Exception {

		ClientMachine client = new ClientMachine();
		
		client.start();

	}

	public void start() throws Exception {
		try {
			
			DatagramChannel channel = DatagramChannel.open();
		    InetSocketAddress address = new InetSocketAddress(0);
		    DatagramSocket socket = channel.socket();
		    socket.setSoTimeout(5000);
		    socket.bind(address);
			
			
			System.out.print("Enter your name: ");
			
			
			Scanner inp = new Scanner(System.in);
			
			InetSocketAddress serverAddress = new InetSocketAddress("localhost",9999);
			String Name = inp.nextLine();
			channel.send(ByteBuffer.wrap(Name.getBytes()),serverAddress);
			
			int n=0;
			
			String incoming = "";
			ByteBuffer read = ByteBuffer.allocate(1024);
			while(true){
			channel.receive(read);		
				
				incoming = new String(read.array());
				
				if(incoming.contains("Guess: ")){
					System.out.println("here"+incoming.trim());
					Scanner getGuess = new Scanner(System.in);
					String s = getGuess.nextLine();
					channel.send(ByteBuffer.wrap(s.getBytes()),serverAddress);
				}
				else
				{
					System.out.println(incoming.trim());
				}
				read.clear();
				read.put(new byte[1024]);
				read.clear();
				
				if(incoming.contains("winners")){
					break;
				}
					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
