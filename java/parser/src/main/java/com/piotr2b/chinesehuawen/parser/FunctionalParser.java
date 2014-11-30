package com.piotr2b.chinesehuawen.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.EmptyStackException;
import java.util.stream.Stream;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

/**
 * On se cantonne à un analyseur pour Chise qui sort un Node.
 * 
 * @author caocoa
 *
 */
public class FunctionalParser {
	private BufferedReader br;
	private Integer limit;

	private FunctionalParser() {
	}

	public FunctionalParser(ArrayDeque<File> files, Integer limit) throws FileNotFoundException {
		InputStream is;
		switch (files.size()) {
		case 0:
			throw new EmptyStackException();
		case 1:
			is = new FileInputStream(files.removeFirst());
			break;
		default:
			InputStream is1 = new FileInputStream(files.removeFirst());
			SequenceInputStream sis = new SequenceInputStream(is1, new FileInputStream(files.removeFirst()));
			while (!(files.size() < 1)) {
				sis = new SequenceInputStream(sis, new FileInputStream(files.removeFirst()));
			}
			is = sis;
			break;
		}
		this.br = new BufferedReader(new InputStreamReader(is));
		this.limit = limit;
	}

	@SuppressWarnings("unchecked")
	public Stream<RowChise> lines() {
		return br.lines().filter(x -> x.split("\t").length == 3).limit(limit).map(x -> new RowChise(x.split("\t")));
	}
}
