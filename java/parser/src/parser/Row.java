package parser;

import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.config.CsvConfiguration;

@CsvDataType()
public abstract class Row {

	// public String character;

	// public String sequence;

	public static CsvConfiguration getConfiguration() {
		CsvConfiguration conf = new CsvConfiguration();
		conf.setFieldDelimiter('\t');
		conf.setLineFilter(new HeaderAndFooterFilter(1, false, false));
		return conf;
	}
}
