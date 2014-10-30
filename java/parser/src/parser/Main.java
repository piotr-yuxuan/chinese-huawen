package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {

	public static int main = 0;
	public static int induced = 0;
	public static int parserError = 0;

	/***
	 * Implements double-keyed dictionary. I need to consider that a character
	 * and its sequence are equivalent. We have the chain String → Integer →
	 * Node so we can query a Node by Integer or by String.
	 */
	public static HashMap<String, Integer> alias = new HashMap<>();
	public static HashMap<Integer, Node> dictionary = new HashMap<>();
	public static int errorType1 = 0;
	public static int errorType2 = 0;
	public static int errorType3 = 0;
	public static int errorType4 = 0;

	public static void main(String[] args) {

		ArrayDeque<File> files = new ArrayDeque<File>();

		// More to be found here: http://www.chise.org/ids/index.html or here
		// http://git.chise.org/gitweb/?p=chise/ids.git;a=tree
		// Order should better not matter ~
		files.addLast(new File("src/IDS-UCS-Basic.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-A.txt"));
		files.addLast(new File("src/IDS-UCS-Compat.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-1.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-2.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-3.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-4.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-5.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-B-6.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-C.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-D.txt"));
		files.addLast(new File("src/IDS-UCS-Ext-E.txt"));
		files.addLast(new File("src/IDS-UCS-Compat-Supplement.txt"));

		Parser<Node, RowChise> parser = new Parser<>(files);
		Iterator<RowChise> iterator = parser.iterator();

		while (iterator.hasNext()) {
			RowChise row = iterator.next();
			Node node = new Node(row.getCharacter(), row.getSequence());

			alias.put(node.getCharacter(), node.getId());
			dictionary.put(node.getId(), node);
			main++;
		}

		int cardinality = 0;
		for (Node n : dictionary.values()) {
			cardinality += n.getCardinality();
		}

		System.out.println();
		System.out.println(format("Main nodes  ", Main.main, Main.main));
		System.out.println(format("Set size    ", Main.dictionary.size(), Main.main));
		System.out.println(format("Cardinalité ", cardinality, Main.dictionary.size()));
		System.out.println(format("Exception   ", Main.parserError, Main.main));
		System.out.println(format("    Type 1  ", Main.errorType1, Main.parserError));
		System.out.println(format("    Type 2  ", Main.errorType2, Main.parserError));
		System.out.println(format("    Type 3  ", Main.errorType3, Main.parserError));
		System.out.println(format("    Type 4  ", Main.errorType4, Main.parserError));

		// printDictionaries();
		exportFinalGraph();

		System.exit(0);
	}

	// Should be polymorph
	public static void printDictionariesId() {
		System.out.println(" — Alias — ");
		for (Map.Entry<String, Integer> g : alias.entrySet()) {
			System.out.println(g.getKey() + "\t" + g.getValue());
		}
		System.out.println(" — Dico — ");
		for (Map.Entry<Integer, Node> g : dictionary.entrySet()) {
			System.out.println(g.getKey() + "\t" + g.getValue().getLink());
			System.out.println("");
		}
	}

	public static void printDictionaries() {
		System.out.println(" — Alias — ");
		for (Map.Entry<String, Integer> g : alias.entrySet()) {
			System.out.println(g.getKey() + "\t" + g.getValue());
		}
		System.out.println(" — Dico — ");
		for (Map.Entry<Integer, Node> g : dictionary.entrySet()) {
			System.out.println(g.getValue().getCharacter() + "\t" + g.getValue().getIDS());
		}
		System.out.println(" — Final leaves — ");
		for (Map.Entry<Integer, Node> g : dictionary.entrySet()) {
			System.out.println(g.getValue().getCharacter() + "\t" + g.getValue().getFinalLeaves());
		}
	}

	public static void exportFinalGraph() {

		File outputNode = new File("graphNode.txt");
		File outputEdge = new File("graphEdge.txt");
		PrintWriter printerNode = null;
		PrintWriter printerEdge = null;

		String sep = "\t";

		try {
			printerNode = new PrintWriter(outputNode);
			printerEdge = new PrintWriter(outputEdge);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println(" — Graph — ");
		printerEdge.write("Source" + sep + "Target" + sep + "Type" + sep + "Weight\n");
		printerNode.write("Id" + sep + "Label" + "\n");

		Object[] graph = dictionary.entrySet().toArray();
		HashMap<Integer, Integer> t = new HashMap<Integer, Integer>(); // translation

		for (Object o : graph) {
			Map.Entry<Integer, Node> a = (Map.Entry<Integer, Node>) o;
			Node node = a.getValue();
			if (!t.containsKey(node.getId())) {
				t.put(node.getId(), t.size());
				printerNode.write(t.get(node.getId()) + sep + node.getCharacter() + "\n");
			}

			ArrayList<Node> leaves = node.getFinalLeaves();
			for (Node leaf : leaves) {
				if (!t.containsKey(leaf.getId())) {
					t.put(leaf.getId(), t.size());
					printerNode.write(t.get(leaf.getId()) + sep + leaf.getCharacter() + "\n");
				}
				String printedEdge = t.get(leaf.getId()) + sep // Source
						+ t.get(node.getId()) + sep// Target
						+ "Directed" + sep// Type
						+ "1"// Weight
						+ "\n";
				printerEdge.write(printedEdge);
				printerEdge.flush();

			}
		}

		System.out.println(" — Done — ");
	}

	private static String format(String label, int field, int total) {
		Double percentage = 100 * field / (double) total;
		return label + ": " + field + " (" + (new DecimalFormat("#.##")).format(percentage) + " %)";
	}
}
