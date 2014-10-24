package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

public class Main {

	public static int main = 0;
	public static int all = 0;
	public static int structures = 0;

	public static void main(String[] args) {

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

		int totalNodes = Main.main + Main.all + Main.structures;

		System.out.println();
		System.out.println(format("All nodes   : ", Main.all, totalNodes));
		System.out.println(format("Main nodes  : ", Main.main, totalNodes));
		System.out.println(format("Structures  : ", Main.structures,
				Main.structures));
		System.exit(0);
	}

	private static String format(String label, int field, int total) {
		return label + ": " + field + " (" + (double) (100 * field / total)
				+ ")";
	}

	public static void parse(File file) {
		FileReader reader;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
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
