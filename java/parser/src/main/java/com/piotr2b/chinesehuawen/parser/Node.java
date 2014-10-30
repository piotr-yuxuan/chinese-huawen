package com.piotr2b.chinesehuawen.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Node {

	private String character;
	private IDC type;
	private ArrayList<Node> leaves;

	public int getId() {
		String ids = getIDS();
		return ids == null ? 0 : ids.hashCode();
	}

	public ArrayList<Node> getFinalLeaves() {
		ArrayList<Node> back = new ArrayList<Node>();
		for (Node n : leaves) {
			if (n.getLeaves().size() == 0 && n.getCharacter() != null) {
				back.add(n);
			}
			if (n.getLeaves().size() != 0) {
				back.addAll(n.getFinalLeaves());
			}
		}
		return back;
	}

	public int getCardinality() {
		if (leaves.size() == 0) {
			return 1;
		} else {
			int sum = 1;
			for (Node n : leaves) {
				sum += n.getCardinality();
			}
			return sum;
		}
	}

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

	public String getIDS() {
		if (leaves == null) {
			return null;
		}
		if (leaves.size() == 0) {
			return character;
		} else {
			String retour = type.toString();
			for (int i = 0; i < leaves.size(); i++) {
				retour += leaves.get(i).toString();
			}
			return retour;
		}
	}

	public String getLink() {
		if (leaves.size() == 0) {
			return this.getId() + character;
		} else {
			String retour = this.getId() + type.toString() + "(";
			for (int i = 0; i < leaves.size(); i++) {
				Node vanish = leaves.get(i);
				retour += vanish.getId() + vanish.toString();
				if (i < leaves.size() - 1) {
					retour += ",";
				}
			}
			retour += ")";
			return retour;
		}
	}

	@Override
	public String toString() {
		if (leaves.size() == 0) {
			return character;
		} else {
			String retour = type.toString() + "(";
			for (int i = 0; i < leaves.size(); i++) {
				retour += leaves.get(i).toString();
				if (i < leaves.size() - 1) {
					retour += ",";
				}
			}
			retour += ")";
			return retour;
		}
	}

	private Node() {
	}

	/***
	 * 
	 * @param character
	 *            If it's a anonymous character
	 * @param sequence
	 *            If character is a radical, repeat character in the sequence.
	 */
	public Node(String character, String sequence) {

		Deque<String> seq = Node.split(sequence);
		if (!validate(seq)) {
			Main.parserError++;
			Main.errorType1++;
		}

		Node node = parse(seq, new ArrayDeque<>());

		this.character = character;
		this.leaves = node.leaves;
		this.type = node.type;
	}

	// Non recursive method, use a stack instead.
	public static Deque<String> split(String sequence) {

		Deque<String> queue = new ArrayDeque<String>();

		while (sequence.length() != 0) {
			String current = sequence.substring(0, 1);
			sequence = sequence.substring(1);
			if (current.charAt(0) == '&') {
				while (current.charAt(current.length() - 1) != ';') {
					current += sequence.substring(0, 1);
					sequence = sequence.substring(1);
				}
			}
			queue.addLast(current);
		}

		return queue;
	}

	private boolean validate(Deque<String> queue) {
		boolean validate = true;
		// Tests about IDS length and syntax
		{
			// To be done;
		}

		// Tests about file consistency (a sinogram must be defined before to be
		// used and you can't use its IDS instead of it once it's been defined)
		{
			// To be done;
		}

		return validate;
	}

	private Node parse(Deque<String> sequence, Deque<Node> stack) {
		String current = sequence.removeLast();

		// Is it a control character or a basic sinogram?
		if (current.length() == 1 && IDC.contains(current.charAt(0)))
		// It's a control character which belongs to IDC.
		{
			IDC idc = new IDC(current.charAt(0));

			Node node = new Node();
			node.leaves = new ArrayList<Node>();
			node.type = idc;

			for (int j = 0; j < idc.getArity(); j++) {
				try {
					Node leaf = stack.removeLast();
					node.leaves.add(leaf);
				} catch (NoSuchElementException e) {
					Main.parserError++;
					Main.errorType4++;
				}
			}
			// Ici on crée tous les nœuds : celui que l'on renvoit et ceux qu'il
			// contient.
			try {
				if (Main.dictionary.containsKey(node.getId())) {
					node = Main.dictionary.get(node.getId());
				} else {
					Main.dictionary.put(node.getId(), node);
				}
			} catch (NullPointerException e) {
				Main.parserError++;
				Main.errorType3++;
			}

			Main.induced++;

			stack.addLast(node);
		} else
		// It's a sinogram.
		{
			Node leaf = new Node();
			leaf.character = current;
			leaf.type = null;
			leaf.leaves = new ArrayList<>();

			if (Main.alias.containsKey(leaf.getCharacter())) {
				int id = Main.alias.get(leaf.getCharacter());
				leaf = Main.dictionary.get(id);
			} else
			// I feel like this may better seldom occur (just for radicals):
			// it
			// means that use an character (that leaf) which has never been
			// described before.
			{
				// It's not perfect : Main should not be modified outside of
				// itself.
				Main.alias.put(leaf.getCharacter(), leaf.getId());
				Main.dictionary.put(leaf.getId(), leaf);
			}

			stack.addLast(leaf);
		}

		if (sequence.size() == 0) {
			if (stack.size() != 1) {
				Main.parserError++;
				Main.errorType2++;
			}
			return stack.removeLast();
		} else {
			return parse(sequence, stack);
		}
	}
}
