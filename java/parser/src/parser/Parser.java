package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

public class Parser<S extends Node, T extends Row> implements Iterable<T> {

	private ArrayDeque<File> files;

	public final int maxParsedLineNumber;
	private int currentParsedLineNumber;

	private CsvConfiguration conf;
	private Deserializer deserializer;

	Parser(ArrayDeque<File> files) {

		this.maxParsedLineNumber = (int) Double.POSITIVE_INFINITY;
		coreConstructor(files);
	}

	Parser(ArrayDeque<File> files, int maxParsedLineNumber) {

		this.maxParsedLineNumber = maxParsedLineNumber;
		coreConstructor(files);
	}

	private void coreConstructor(ArrayDeque<File> files) {
		this.currentParsedLineNumber = 0;
		this.files = files;

		// Class<?> classT = this.getClass().getTypeParameters()[1].getClass();
		Class<?> classT = RowChise.class; // How to do it properly?
		this.conf = RowChise.getConfiguration();

		this.deserializer = CsvIOFactory.createFactory(conf, classT).createDeserializer();
	}

	@Override
	public Iterator<T> iterator() {
		return new ParserIterator<T>();
	}

	private class ParserIterator<U extends Row> implements Iterator<U> {

		Reader reader;

		ParserIterator() {
		}

		@Override
		public U next() {
			if (reader == null) {
				if (files.size() == 0) {
					throw new NoSuchElementException();
				} else {
					setReader();
					deserializer.open(reader);
					return deserializer.next();
				}
			} else {
				if (deserializer.hasNext()) {
					return deserializer.next();
				} else {
					deserializer.close(true);
					reader = null;
					return next();
				}
			}
		}

		private void setReader() {
			try {
				reader = new InputStreamReader(new FileInputStream(files.removeFirst()), "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean hasNext() {
			if (currentParsedLineNumber++ < maxParsedLineNumber) {
				if (files.size() != 0) {
					return true;
				} else {
					return deserializer.hasNext();
				}
			} else {
				return false;
			}
		}
	}
}
