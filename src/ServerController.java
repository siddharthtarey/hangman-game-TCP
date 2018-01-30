/**
 * 
 * ServerControler.java (Controller for game server)
 * 
 * Version 2.0
 * 
 */

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class acts as a controller for hangman game server
 * 
 * @author Swapnil Kamat (snk6855@rit.edu)
 * @author Siddharth Tarey (st2476@rit.edu)
 *
 */

public class ServerController implements Runnable {

	public Hangman player;
	public static String line = new String();

	// sockets for 2 players
	 public DatagramChannel sock1,sock2;

	// ArrayList of all the players
	public static ArrayList<Hangman> players = new ArrayList<Hangman>();

	public ServerController() {

	}

	/*
	 * Parameterized constructor, to initialize the the hangman player
	 */
	public ServerController(Hangman player) {
		this.player = player;
	}

	public static void main(String[] args) throws IOException {

		String filename = "D:/workspace/homework11/src/words.txt";
		SocketAddress client1 = null,client2 = null;
		
		Random random = new Random();
		ServerController s = new ServerController();
		File textfile = new File(filename);
		s.sock1 = DatagramChannel.open();
		DatagramSocket socket = s.sock1.socket();
	    SocketAddress address = new InetSocketAddress(9999);
	    socket.bind(address);
	    
//	    s.sock2 = DatagramChannel.open();
//		DatagramSocket socket1 = s.sock2.socket();
//	    SocketAddress address1 = new InetSocketAddress(9998);
//	    socket1.bind(address1);
		
	    
		boolean wordflag = true;

		Hangman h = new Hangman();


		Thread t1 = new Thread();
		Thread t2 = new Thread();

		try {
			Scanner in = new Scanner(textfile);

			// this loop executes till a correct word(without special
			// characters) is selected.
			while (wordflag == true) {
				// randomly selects an integer
				int select = random.nextInt(3);
				// selects a word based on the random integer.
				for (int index = 0; index <= select; index++) {
					line = in.nextLine();
					line = line.toLowerCase();
				}
				if (h.checkcharacters(line)) {
					wordflag = false;
				}
			}
			in.close();

		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			System.exit(0);
		}
	 
		
	
		// this initializes players and starts play the game thread for each
		// player
		for (int index = 1; index <= 2; index++) {
			
			// Initializing player 1, adding to the arraylist of players and
			// starting thread for player 1
			if (index == 1) {
				System.out.println("Waiting for player 1 to connect.. ");
								
				ByteBuffer read = ByteBuffer.allocate(1024);
				
				int n = 0;

				client1 = s.sock1.receive(read);
				String name = new String(read.array());
				System.out.println("name is" +name);				
				
				players.add(new Hangman(name, 0, s.sock1, false,client1));
				t1 = new Thread(new ServerController(players.get(0)));
				
			}
			// Initializing player 2, adding to the arraylist of players and
			// starting thread for player 2
			else if (index == 2) {
				System.out.println("Waiting for player 2 to connect.. ");

				int n = 0;
				ByteBuffer read = ByteBuffer.allocate(1024);
		
				client2 = s.sock1.receive(read);
				String name = new String(read.array());
				
				System.out.println("name is" +name);
				
				players.add(new Hangman(name, 0, s.sock1, false,client2));
				t2 = new Thread(new ServerController(players.get(1)));
				
			}
		}
		t1.start();
		t2.start();
		while (t1.isAlive() == true || t2.isAlive()) {
		}

		// Determine the winner and write out to the clients
		String winner = h.displayWinner(players);
		
		byte[] senddata = new byte[1024];
		senddata = winner.getBytes();
		
		s.sock1.send(ByteBuffer.wrap(senddata),client1);
		s.sock1.send(ByteBuffer.wrap(senddata),client2);
	}

	/**
	 * This function starts the Hangman game for each individual player
	 * 
	 * @param line:
	 *            the word that has to be guessed by the player
	 * @param player:
	 *            the player that is currently playing the game
	 * @throws IOException
	 */
	public void play(String line, Hangman player) throws IOException {

		
		char[] word = new char[line.length()];

		word = line.toCharArray();

		boolean[] check = new boolean[line.length()];

		int guess = 0;

		char[] miss = new char[8];

		SocketAddress saddress = player.client;
		
		Hangman play = new Hangman();
		
		
		do {
			
			String displayWord = play.displayword(word, check);
			System.out.println("display"+displayWord);
			player.sock.send(ByteBuffer.wrap(displayWord.getBytes()),saddress);
			// Prints the letters that have been guessed wrong
			String printMisses = play.printMisses(miss);
			System.out.println("printMisses: "+printMisses);
			player.sock.send(ByteBuffer.wrap(printMisses.getBytes()),saddress);
			
		
			player.sock.send(ByteBuffer.wrap("\nGuess: ".getBytes()),saddress);
			
			ByteBuffer receivepacket = ByteBuffer.allocate(1024);
			player.sock.receive(receivepacket);
			
			int n = 0;

			String charGuess = "";
			// Takes the input of letter from the player
			if (receivepacket.array() != null) {
				charGuess = new String(receivepacket.array());
			}

			char input = charGuess.charAt(0);

			Character ch1 = new Character(input);

			String mystring = ch1.toString().toLowerCase();

			// if the character entered by the user is a alphabet, the condition
			// will be true
			if (play.checkcharacters(mystring)) {

				input = mystring.toCharArray()[0];
				// if the player makes a correct guess 10 points are added
				if (play.matchletter(word, check, input, player) == 1) {

					player.points += 10;
				}
				// if letter has already been guessed, print the below message
				else if (play.matchletter(word, check, input, player) == 2) {
					
					player.sock.send(ByteBuffer.wrap("This letter has already been played\n".getBytes()),saddress);
	
				}

				// if the guess is wrong, execute the else statement.
				else {

					// if the letter has already been missed by the user, print
					// the below message
					if (!play.checkMisses(miss, input)) {
						player.sock.send(ByteBuffer.wrap("This letter has already been played\n".getBytes()),saddress);

					}

					// deduct 5 points if the above condition is false.
					else {
						player.points -= 5;
						miss[guess] = input;
						guess += 1;
					}

				}
				
				player.sock.send(ByteBuffer.wrap(("Score: " + player.points + "\n").getBytes()),saddress);
		
			}

			else {
				player.sock.send(ByteBuffer.wrap("Enter only lower case alphabets\n".getBytes()),saddress);
				continue;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (guess <= 7 && !play.checkComplete(check, this.player));
		
		player.sock.send(ByteBuffer.wrap("\nWaiting for other players to finish their play..\nWinning player will be announced soon..\n".getBytes()),saddress);

		
		
	}

	@Override
	public void run() {

		try {
			//starts the play for each player
			this.play(line, player);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
