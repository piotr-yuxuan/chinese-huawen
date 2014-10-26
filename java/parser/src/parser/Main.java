package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

public class Main {

	public static int main = 0;
	public static int induced = 0;
	public static int parserError = 0;
	public static final int maxParsedLineNumber = 3 * 1000;
	public static int currentParsedLineNumber = 0;
	/***
	 * Implements double-keyed dictionary. I need to consider that a character
	 * and its sequence are equivalent. We have the chain String → Integer →
	 * Node so we can query a Node by Integer or by String.
	 */
	public static HashMap<String, Integer> alias = new HashMap<>();
	public static HashMap<Integer, Node> dictionary = new HashMap<>();

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
			System.out.println(g.getValue().getCharacter() + "\t"
					+ g.getValue().getIDS());
		}
		System.out.println(" — Final leaves — ");
		for (Map.Entry<Integer, Node> g : dictionary.entrySet()) {
			System.out.println(g.getValue().getCharacter() + "\t"
					+ g.getValue().getFinalLeaves());
		}
	}

	// bugged
	public static void exportGraph() {

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
		printerEdge.write("Source" + sep + "Target" + sep + "Type" + sep + "Id"
				+ sep + "Label" + sep + "Weight\n");
		printerNode.write("Nodes" + sep + "Id" + sep + "Label" + "\n");

		Object[] graph = dictionary.entrySet().toArray();
		HashMap<Integer, Integer> t = new HashMap<Integer, Integer>(); // translation

		for (Object o : graph) {
			Map.Entry<Integer, Node> a = (Map.Entry<Integer, Node>) o;
			Node node = a.getValue();

			t.put(node.getId(), t.size());
			printerNode.write(node.getIDS() + sep// Nodes
					+ t.get(node.getId()) + sep// Id
					+ node.getIDS()// Label
					+ "\n");
			printerNode.flush();

			ArrayList<Node> leaves = node.getFinalLeaves();
			for (Node leaf : leaves) {
				t.put(leaf.getId(), t.size());
				String printedEdge = t.get(node.getId())
						+ sep // Source
						+ t.get(leaf.getId())
						+ sep// Target
						+ "Undirected"
						+ sep// Type
						+ Integer.toString(t.get(node.getId()))
						+ Integer.toString(t.get(leaf.getId())) + sep// Id
						+ "" + sep// Label
						+ "1"// Weight
						+ "\n";
				printerEdge.write(printedEdge);
				printerEdge.flush();
				String printedNodes = leaf.getIDS() + sep// Nodes
						+ t.get(leaf.getId()) + sep// Id
						+ leaf.getIDS()// Label
						+ "\n";
				printerNode.write(printedNodes);
				printerNode.flush();
			}
		}
		System.out.println(" — Done — ");
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
		printerEdge.write("Source" + sep + "Target" + sep + "Type" + sep
				+ "Weight\n");
		printerNode.write("Id" + sep + "Label" + "\n");

		Object[] graph = dictionary.entrySet().toArray();
		HashMap<Integer, Integer> t = new HashMap<Integer, Integer>(); // translation

		for (Object o : graph) {
			Map.Entry<Integer, Node> a = (Map.Entry<Integer, Node>) o;
			Node node = a.getValue();
			if (!t.containsKey(node.getId())) {
				t.put(node.getId(), t.size());
				printerNode.write(t.get(node.getId()) + sep
						+ node.getCharacter() + "\n");
			}

			ArrayList<Node> leaves = node.getFinalLeaves();
			for (Node leaf : leaves) {
				if (!t.containsKey(leaf.getId())) {
					t.put(leaf.getId(), t.size());
					printerNode.write(t.get(leaf.getId()) + sep
							+ leaf.getCharacter() + "\n");
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

		// for (Map.Entry<Integer, Integer> a : t.entrySet()) {

		// }
		System.out.println(" — Done — ");
	}

	public static void main(String[] args) {

		// parse(new File("src/test.txt"));
		parse(new File("src/IDS-UCS-Basic.txt"));
		parse(new File("src/IDS-UCS-Compat-Supplement.txt"));
		parse(new File("src/IDS-UCS-Compat.txt"));
		parse(new File("src/IDS-UCS-Ext-A.txt"));
		parse(new File("src/IDS-UCS-Ext-B-1.txt"));
		parse(new File("src/IDS-UCS-Ext-B-2.txt"));
		parse(new File("src/IDS-UCS-Ext-B-3.txt"));
		parse(new File("src/IDS-UCS-Ext-B-4.txt"));
		parse(new File("src/IDS-UCS-Ext-B-5.txt"));
		parse(new File("src/IDS-UCS-Ext-B-6.txt"));
		parse(new File("src/IDS-UCS-Ext-C.txt"));
		parse(new File("src/IDS-UCS-Ext-D.txt"));
		parse(new File("src/IDS-UCS-Ext-E.txt"));

		int cardinality = 0;
		for (Node n : dictionary.values()) {
			cardinality += n.getCardinality();
		}

		System.out.println();
		System.out.println(format("Main nodes  ", Main.main, Main.main));
		System.out.println(format("Set size    ", Main.dictionary.size(),
				Main.main));
		System.out.println(format("Cardinalité ", cardinality,
				Main.dictionary.size()));
		System.out.println(format("Exception   ", Main.parserError, Main.main));

		// printDictionaries();
		exportFinalGraph();

		System.exit(0);
	}

	private static String format(String label, int field, int total) {
		return label + ": " + field + " (" + ((double) 100 * field / total)
				+ ")";
	}

	public static void parse(File file) {

		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		CsvConfiguration conf = new CsvConfiguration();
		conf.setFieldDelimiter('\t');
		conf.setLineFilter(new HeaderAndFooterFilter(1, false, false));

		Deserializer deserializer = CsvIOFactory.createFactory(conf, Row.class)
				.createDeserializer();

		deserializer.open(reader);
		while (deserializer.hasNext()
				&& currentParsedLineNumber < maxParsedLineNumber) {
			currentParsedLineNumber++;
			Row row = deserializer.next();
			row.toEntities();
		}
		deserializer.close(true);
	}
}
