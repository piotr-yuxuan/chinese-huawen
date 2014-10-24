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

	public void setType(IDC node) {
		this.type = node;
	}

	public ArrayList<Node> getLeaves() {
		return leaves;
	}

	public void setLeaves(ArrayList<Node> leaves) {
		this.leaves = leaves;
	}

	private Node() {
		this.character = "Ø";
	}

	public Node(String character, String ids) {
		this.character = character;
		try {
			parse(ids);
		} catch (Exception e) {
		}
	}

	private void parse(String expression) {

		// ⿱氏巾
		// ⿱⿰⿱丿幺幺糸
		// ⿰糹⿱氏巾
		// ⿰糹⿰⿻卩丿⿱龴又

		/* ⿱(⿰(⿱上夕)又)力 */

		Deque<Node> stack = new ArrayDeque<>();

		if (expression.equals("⿱⿰⿱上夕又力")) {
			int a;
			a = 3;
			a = a + 1;
		}

		for (int i = expression.length() - 1; i >= 0; i--) {
			char got = expression.charAt(i);
			if (IDC.contains(got)) {
				IDC idc = new IDC(got);
				if (stack.size() >= idc.getArity()) {
					// iff no &;
					Node node = new Node();
					node.type = idc;
					node.leaves = new ArrayList<>();

					for (int j = 0; j < idc.getArity(); j++) {
						node.leaves.add(stack.pop());
					}

					stack.push(node);

				} else {
					// Uh oh, trouble. Misformed expression.
				}
			} else {
				// d'autres tests pour &;
				Node n = new Node();
				n.character = Character.toString(got);
				n.type = null;
				n.leaves = new ArrayList<>();

				stack.push(n);
			}
		}

		try {
			Node node = stack.pop();
			this.leaves = node.leaves;
			this.type = node.type;
		} catch (Exception e) {
			throw new NoSuchElementException();
		}
	}
}
