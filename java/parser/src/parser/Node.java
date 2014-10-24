package parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Stack;

import entities.Allography;
import entities.Sinogram;
import entities.Structure;

public class Node {

	private String character;
	private IDC type;
	private ArrayList<Node> leaves;

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String expression) {
		this.character = expression;
	}

	public IDC getType() {
		return type;
	}

	public ArrayList<Node> getLeaves() {
		return leaves;
	}

	@Override
	public String toString() {
		if (leaves.size() == 0) {
			return character;
		} else {
			String retour = type.toString();
			for (Node n : leaves) {
				retour += n.toString();
			}
			return retour;
		}
	}

	private Node() {
	}

	/***
	 * 
	 * @param character
	 *            can't be null
	 */
	public Node(String character) {
		this.character = character;
		this.type = null;
		this.leaves = new ArrayList<>();
	}

	@SuppressWarnings("unused")
	public void parse(String sequence) {

		if (sequence.contains("&")) {
			Main.ignored++;
			return;
		} else {
			// System.out.println("debug " + sequence);
		}

		Node node = parse(new String(sequence), new ArrayDeque<>());

		// this.character is already defined. Moreover it's not in the ids.
		this.leaves = node.leaves;
		this.type = node.type;
	}

	@SuppressWarnings("unused")
	private Node parse(String sequence, Deque<Node> stack) {
		char current = sequence.charAt(sequence.length() - 1);
		sequence = sequence.substring(0, sequence.length() - 1);

		if (IDC.contains(current)) {
			Node leaf = new Node();
			IDC idc = new IDC(current);
			for (int j = 0; j < idc.getArity(); j++) {
				try {
					leaf = stack.pop();
					type = idc;
					leaves.add(leaf);
				} catch (NoSuchElementException e) {
					System.out.println("Malformed IDS");
					Main.ignored++;
				}
			}
			stack.push(leaf);
		} else {
			Node leaf = new Node();
			leaf.character = Character.toString(current);
			leaf.type = null;
			leaf.leaves = new ArrayList<>();

			stack.push(leaf);
		}

		if (sequence.length() == 0) {
			return stack.pop();
		} else {
			return parse(sequence, stack);
		}
	}
}
