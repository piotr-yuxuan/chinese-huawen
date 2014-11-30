package com.piotr2b.chinesehuawen.parser;

import static com.piotr2b.chinesehuawen.entities.Tables.SINOGRAM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.piotr2b.chinesehuawen.entities.tables.Sinogram;
import com.piotr2b.chinesehuawen.parser.Alias.UndefinedAliasException;

public class Main {

	public static int main = 0;
	public static int induced = 0;
	public static int parserError = 0;

	/***
	 * Implements double-keyed dictionary. I need to consider that a character
	 * and its sequence are equivalent. We have the chain String → Integer →
	 * Node so we can query a Node by Integer or by String.
	 */
	// public static HashMap<String, Integer> alias = new HashMap<>();
	// public static HashMap<Integer, Node> dictionary = new HashMap<>();
	public static AliasMap<Integer, String, Node> aliasMap = new AliasMap<>(Integer.class, String.class, Node.class);
	public static int errorType1 = 0;
	public static int errorType2 = 0;
	public static int errorType3 = 0;
	public static int errorType4 = 0;

	public static Node getTransitiveReductionTree(String x) {
		return new Node("a", "b");
	}

	public static String[] parseRow(String x) {
		return x.split("\t");
	}

	// public static Node getParsedTree(String x) {
	// Node node;
	// return node;
	// }
	//
	// public static Node getEtymonTree(String x) {
	// Node node;
	// return node;
	// }

	public static void main(String[] args) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			new org.mariadb.jdbc.Driver();

			String connectionUrl = "jdbc:mariadb://localhost:3306/huawen";
			String connectionUser = "huawen";
			String connectionPassword = "huawen";
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);

			DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
			Result<Record> result = create.select().from(SINOGRAM).fetch();

			for (Record r : result) {
				String codePoint = r.getValue(SINOGRAM.CP);
				System.out.println("ID: " + codePoint);
				Sinogram sinogram = new Sinogram();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		String idsPath = "../../data/ids/chise/ids/";
		String exportPath = "../../gephi/files/";

		try {
			// On ne regarde que les IDS donc on n'a pas besoin du PairMap.
			HashMap<Integer, Node> map = new HashMap<>();
			InputStream is = new FileInputStream(new File(idsPath + "IDS-UCS-Basic.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			// stateless (their result should not depend on any state that might
			// change during
			// execution of the stream pipeline).

			// br.lines()//
			// .parallel() //
			// .map(Main::parseRow) // stateless
			// // parallèle ici ?
			// .forEach(Node::x -> { // can be stateful
			// String codepoint = x[0];
			// String character = x[1];
			// String sequence = x[2];
			// System.out.print(x);
			//
			// if (map.containsKey(sequence.hashCode()))
			// return;
			// else {
			// Node node = new Node(character, sequence);
			// map.put(node.getId(), node);
			// }
			// });

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayDeque<File> files = new ArrayDeque<File>();

		// More to be found here: http://www.chise.org/ids/index.html or here
		// http://git.chise.org/gitweb/?p=chise/ids.git;a=tree
		// Order should better not matter ~
		// files.addLast(new File("test.txt"));

		files.addLast(new File(idsPath + "IDS-UCS-Basic.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-A.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Compat.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-1.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-2.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-3.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-4.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-5.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-B-6.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-C.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-D.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Ext-E.txt"));
		// files.addLast(new File(idsPath + "IDS-UCS-Compat-Supplement.txt"));

		Parser<Node, RowChise> parser;
		parser = new Parser<>(files, 25000);
		Iterator<RowChise> iterator = parser.iterator();

		while (iterator.hasNext()) {
			RowChise row = iterator.next();

			if (row.getCharacter().contains("灣") || row.getCharacter().contains("䜌")) {
				System.out.print("");
			}

			Node node = new Node(row.getCharacter(), row.getSequence());

			// alias.put(node.getCharacter(), node.getId());
			// dictionary.put(node.getId(), node);
			try {
				aliasMap.put(new Alias<Integer, String>(node.getId(), node.getCharacter()), node);
			} catch (UndefinedAliasException e) {
				e.printStackTrace();
			}
			main++;
		}

		int cardinality = 0;
		for (Node n : aliasMap.values()) {
			cardinality += n.getCardinality();
		}

		// 灣 is used as example in README.md but currently seems not correctly
		// processed. To be investigated ~

		System.out.println();
		System.out.println(format("Main nodes  ", Main.main, Main.main));
		System.out.println(format("Set size    ", Main.aliasMap.size(), Main.main));
		System.out.println(format("Cardinalité ", cardinality, Main.aliasMap.size()));
		System.out.println(format("Exception   ", Main.parserError, Main.main));
		System.out.println(format("    Type 1  ", Main.errorType1, Main.parserError));
		System.out.println(format("    Type 2  ", Main.errorType2, Main.parserError));
		System.out.println(format("    Type 3  ", Main.errorType3, Main.parserError));
		System.out.println(format("    Type 4  ", Main.errorType4, Main.parserError));

		// On prend un cas simple avec K1 puis Pair<K1, K2>.
		// Sans fonctionnel
		// for (Map.Entry<Integer, Node> entry : dictionary.entrySet()) {
		// Integer key = entry.getKey();
		// Node value = entry.getValue();
		// pairMap.put(key, value);
		// }

		// Avec fonctionnel
		// dictionary.entrySet().stream().forEach(entry -> {
		// try {
		// aliasMap.put(entry.getKey(), entry.getValue());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// });
		//
		// // check
		// aliasMap.entrySet().stream().forEach(entry ->
		// System.out.println(Integer.toString(entry.getKey().getK1()) + " " +
		// entry.getValue().getIDS()));

		// printDictionaries();
		exportFinalGraph(exportPath);

		System.exit(0);
	}

	public static void exportFinalGraph(String path) {

		File outputNode = new File(path + "graphNode.txt");
		File outputEdge = new File(path + "graphEdge.txt");
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

		Object[] graph = aliasMap.entryKMainSet().toArray();
		HashMap<Integer, Integer> indexTranslation = new HashMap<Integer, Integer>(); // translation

		for (Object root : graph) {
			@SuppressWarnings("unchecked")
			Node node = ((Map.Entry<Integer, Node>) root).getValue();
			if (!indexTranslation.containsKey(node.getId())) {
				indexTranslation.put(node.getId(), indexTranslation.size());
				printerNode.write(indexTranslation.get(node.getId()) + sep + node.getCharacter() + "\n");
			}

			ArrayList<Node> leaves = node.getLeaves();
			for (Node leaf : leaves) {
				if (!indexTranslation.containsKey(leaf.getId())) {
					indexTranslation.put(leaf.getId(), indexTranslation.size());
					printerNode.write(indexTranslation.get(leaf.getId()) + sep + leaf.getCharacter() + "\n");
				}
				String printedEdge = indexTranslation.get(leaf.getId()) + sep // Source
						+ indexTranslation.get(node.getId()) + sep// Target
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
