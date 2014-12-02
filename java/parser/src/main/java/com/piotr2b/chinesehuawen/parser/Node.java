package com.piotr2b.chinesehuawen.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import com.piotr2b.chinesehuawen.parser.Pair.UndefinedAliasException;

public class Node {

	public enum TreeType {
		/**
		 * Just the basic tree.
		 */
		Parsed,
		/**
		 * Transitive reduction of the basic tree. It ensures path unicity from
		 * a sinogram to a component.
		 */
		TransitiveReduction,
		/**
		 * Remove transitional etymons, just keep the radicals.
		 */
		Radical; // Careful when ensuring consistency.

		public Node export(Node node) {
			Node ret = new Node();
			ret.character = node.character;
			ret.idc = node.idc;
			ret.leaves = new ArrayList<Node>();
			switch (this) {
			case Radical:
				ArrayList<Node> radicals = new ArrayList<Node>();
				for (Node n : node.leaves) {
					if (n.getLeaves().size() == 0 && n.getCharacter() != null) {
						radicals.add(n);
					}
					if (n.getLeaves().size() != 0) {
						radicals.addAll(n.getFinalLeaves());
					}
				}
				ret.leaves = radicals;
				break;
			case TransitiveReduction:
				break;
			default:
			case Parsed:
				ret.leaves = node.leaves;
			}
			return ret;
		}
	}

	public String character;
	public IDC idc;
	public ArrayList<Node> leaves;

	public int getId() {
		String ids = getIDS();
		return ids == null ? 0 : ids.hashCode();
	}

	/**
	 * Tous, lui inclus
	 * 
	 * @return
	 */
	public Set<Node> getNodeSet() {
		HashSet<Node> set = new HashSet<Node>();
		set.add(this);
		for (Node n : leaves) {
			set.addAll(n.getNodeSet());
		}
		return set;
	}

	public Set<Pair<Node, Node>> getEdgeSet() {
		HashSet<Pair<Node, Node>> set = new HashSet<>();
		try {
			for (Node n : leaves) {
				set.add(new Pair<Node, Node>(this, n));
				set.addAll(n.getEdgeSet());
			}
		} catch (UndefinedAliasException e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * Don't use exact object but IDS
	 * 
	 * @param node
	 * @return
	 */
	public boolean contains(Node node) {
		for (Node n : getNodeSet()) {
			if (n.getId() == node.getId()) {
				return true;
			}
		}
		return false;
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
		return idc;
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
			String retour = idc.toString();
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
			String retour = this.getId() + idc.toString() + "(";
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
		if (this.character != null && !this.character.equals("")) {
			return character;
		} else {
			String retour = idc.toString() + "(";
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

	// should be private
	public Node() {
		this.character = null;
		this.idc = null;
		this.leaves = new ArrayList<>();
	}

	/***
	 * 
	 * @param character
	 *            Null if it's a anonymous character
	 * @param sequence
	 *            If character is a radical, repeat character in the sequence.
	 */
	public Node(String character, String sequence) {

		Deque<String> seq = Node.split(sequence);
		if (!validate(seq)) {
		}

		Node node = Node.parse(seq, new ArrayDeque<>());

		this.character = character;
		this.leaves = node.leaves;
		this.idc = node.idc;
	}

	@Override
	public boolean equals(Object o) {
		return (o == null || !o.getClass().isAssignableFrom(Node.class)) ? false : ((Node) o).getId() == this.getId();
	}

	// Non recursive method, use a stack instead.
	protected static Deque<String> split(String sequence) {

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

	public static Node parse(Deque<String> sequence, Deque<Node> stack) {
		String current = sequence.removeLast();

		// Is it a control character or a basic sinogram?
		if (current.length() == 1 && IDC.contains(current.charAt(0)))
		// It's a control character which belongs to IDC.
		{
			IDC idc = new IDC(current.charAt(0));

			Node node = new Node();
			node.leaves = new ArrayList<Node>();
			node.idc = idc;

			for (int j = 0; j < idc.getArity(); j++) {
				try {
					Node leaf = stack.removeLast();
					node.leaves.add(leaf);
				} catch (NoSuchElementException e) {
				}
			}
			stack.addLast(node);
		} else
		// It's a sinogram.
		{
			Node leaf = new Node();
			leaf.character = current;
			leaf.idc = null;
			leaf.leaves = new ArrayList<>();

			stack.addLast(leaf);
		}

		if (sequence.size() == 0) {
			if (stack.size() != 1) {
			}
			return stack.removeLast();
		} else {
			return parse(sequence, stack);
		}
	}
}
