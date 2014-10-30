package com.piotr2b.chinesehuawen.parser;

import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.config.CsvConfiguration;

@CsvDataType()
public abstract class Row {

	public abstract String getCharacter();

	public abstract String getSequence();

	public static CsvConfiguration getConfiguration() {
		CsvConfiguration conf = new CsvConfiguration();
		conf.setFieldDelimiter('\t');
		conf.setLineFilter(new HeaderAndFooterFilter(1, false, false));
		return conf;
	}
}
