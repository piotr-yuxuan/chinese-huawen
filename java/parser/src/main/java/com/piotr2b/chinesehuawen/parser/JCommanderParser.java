package com.piotr2b.chinesehuawen.parser;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class JCommanderParser {

	public JCommanderParser() {
	}

	@Parameter
	private List<String> parameters = new ArrayList<String>();

	@Parameter(names = "-direct", description = "Input an IDS and parse it")
	private String direct;

	@Parameter(names = "-files", description = "Comma-separated list of files to be parsed")
	private String files;

	@Parameter(names = "-output", description = "The format output may be released")
	private String output;

	public final List<String> getParameters() {
		return parameters;
	}

	public final String getDirect() {
		return direct;
	}

	public final String getFiles() {
		return files;
	}

	public final String getOutput() {
		return output;
	}
}