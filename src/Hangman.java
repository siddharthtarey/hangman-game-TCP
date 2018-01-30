/**
 * 
 * Hangman.java (Model for game server)
 * 
 * Version 2.0
 * 
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class acts as a model for hangman game server
 * 
 * @author Swapnil Kamat (snk6855@rit.edu)
 * @author Siddharth Tarey (st2476@rit.edu)
 * 
 *
 */

public class Hangman {
	
	String name;
	boolean winner;
	int points;
	DatagramChannel sock;
	SocketAddress client;

	/*
	 * Default constructor
	 */
	Hangman() {

	}

	/*
	 * Parameterized constructor, to initialize the player name, points, socket and winner flag.
	 */
	Hangman(String name, int points, DatagramChannel sock, boolean winner,SocketAddress client) {

		this.name = name;
		this.points = points;
		this.sock = sock;
		this.winner = winner;
		this.client = client;
	}
	
	
	/**
	 * Displays the guessed letters and the pending letters of the word
	 * 
	 * @param word:
	 *            The word that has to be guessed
	 * @param check:
	 *            the boolean array which tells you which letter has been
	 *            guessed
	 */
	public String displayword(char[] word, boolean[] check) {

		String result = "";
		for (int index = 0; index < check.length; index++) {
			
			if (check[index] == false) {
				result += "_ ";
			} else {
				result += word[index] + " ";
			}
		}
		return result;
	}
	
	/**
	 * Matches the letter with the word that has to be guessed
	 * 
	 * @param word:
	 *            the word that has to be guessed
	 * @param check:
	 *            the boolean array which tells you which letter has been
	 *            guessed
	 * @param input:
	 *            the letter that has been entered by the user.
	 * @param player:
	 *            the player that is playing currently
	 * @return: returns 1 if the player makes a correct guess, returns 2 if the
	 *          letter has already been guessed
	 * @return: returns 0 if a wrong guess has been made
	 */
	public int matchletter(char[] word, boolean[] check, char input, Hangman player) {

		int flag = 0;
		// loop through all the characters
		for (int index = 0; index < word.length; index++) {

			// checks if the word has already been guessed by the player
			if (input == word[index] && check[index] == true) {
				flag = 2;

			}
			// checks if the word is correctly guessed by the player.
			else if (input == word[index]) {

				check[index] = true;
				flag = 1;
			}

		}

		return flag;
	}
	
	/**
	 * Print the letters that have been guessed wrong by the player
	 * 
	 * @param misses:
	 *            The array that contains the letters that have been guessed
	 *            wrong
	 */
	public String printMisses(char[] misses) {
		int index = 0;
		String result="";
		result += "\nMisses: ";
		while (misses[index] != '\0') {

			result += " " + misses[index];
			index++;
		}
		return result;
	}

	/**
	 * Sorts the players based on the scores, then returns the name and score of
	 * the players
	 * 
	 * @param players:
	 *            The list of layers that have played the game
	 * @return:
	 */
	public String displayWinner(ArrayList<Hangman> players) {

		int count = 0;
		boolean flag = true;
		String result = "";
		// this loop arranges the players in descending order of their points
		// scored
		while (flag) {

			flag = false;
			for (int index = 0; index < players.size() - 1; index++) {

				if (players.get(index).points < players.get(index + 1).points) {

					Collections.swap(players, index, index + 1);
					flag = true;

				}
			}
		}

		result += "\n\nWinners\n";
		for (Hangman p : players) {
			// returns winners if p.winner flag is true
			if (p.winner == true) {
				result += p.name + "\t\t" + p.points+"\n";
			} else {
				count++;
			}
		}
		// if there are no winners, return this statement
		if (count == players.size()) {
			result += "No one is the winner\n";
		}
		
		return result;
	}

	/**
	 * Checks if the input entered by the user is missed previously
	 * 
	 * @param miss:
	 *            the letters that have been guessed wrong by the user
	 * @param input:
	 *            the letter that has been entered by the user
	 * @return: Returns false if the letter has already been missed by the user
	 * 
	 */
	public boolean checkMisses(char[] miss, char input) {

		int index = 0;
		// iterates through the miss array
		while (miss[index] != '\0') {
			// compares the input to the element of the array
			if (miss[index] == input) {
				// returns false if the element is found in the array
				return false;
			}

			index++;

		}
		return true;
	}

	/**
	 * This function returns true if the string is an alphabet in lower case
	 * 
	 * @param line:
	 *            string that has to be verified
	 * @return: returns true if the all the characters in string is a lower case
	 *          alphabets
	 */
	public boolean checkcharacters(String line) {
		// checks if the incoming string contains only alphabets
		char[] mychar = line.toCharArray();
		
		for(int index =0 ; index < mychar.length ; index++) {

			if (!(mychar[index] == 'a' || mychar[index] == 'b' || mychar[index] == 'c' || mychar[index] == 'd'
					|| mychar[index] == 'e' || mychar[index] == 'f' || mychar[index] == 'g' || mychar[index] == 'h'
					|| mychar[index] == 'i' || mychar[index] == 'j' || mychar[index] == 'k' || mychar[index] == 'l'
					|| mychar[index] == 'm' || mychar[index] == 'n' || mychar[index] == 'o' || mychar[index] == 'p'
					|| mychar[index] == 'q' || mychar[index] == 'r' || mychar[index] == 's' || mychar[index] == 't'
					|| mychar[index] == 'u' || mychar[index] == 'v' || mychar[index] == 'w' || mychar[index] == 'x'
					|| mychar[index] == 'y' || mychar[index] == 'z')) {

				return false;
			}
			
		}

		// returns true if all characters are alphabets
		return true;
	}
	
	/**
	 * Checks if all the letters have been guessed
	 * 
	 * @param check:
	 *            the boolean array which tells you which letter has been
	 *            guessed
	 * @param player:
	 *            the current player
	 * @return: returns true if all the letters of the word have been guessed
	 */
	public boolean checkComplete(boolean[] check, Hangman player) {
		// iterates through the entire check array
		for (int index = 0; index < check.length; index++) {

			// checks if the the value is false at that index
			if (check[index] == false) {
				// if it returns false then not all letter have been guessed.
				return false;
			}
		}
		// if it dosent return false, the player is declared as winner and
		// "true" is returned
		player.winner = true;
		return true;
	}

}
