package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

public class Main {

	public static int main = 0;
	public static int induced = 0;
	public static int parserError = 0;
	public static HashMap<Integer, Node> dictionary = new HashMap<>();
	/***
	 * Implements double-keyed dictionary. I need to consider that a character and its sequence are equivalent.
	 */
	public static HashMap<String, Integer> alias = new HashMap<>();

	public static void main(String[] args) {

		parse(new File("src/test.txt"));
		/*parse(new File("src/IDS-UCS-Basic.txt"));
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
		parse(new File("src/IDS-UCS-Ext-E.txt"));*/

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
		while (deserializer.hasNext()) {
			Row row = deserializer.next();
			row.toEntities();
		}
		deserializer.close(true);
	}
}
