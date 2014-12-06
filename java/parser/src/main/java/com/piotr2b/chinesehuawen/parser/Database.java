package com.piotr2b.chinesehuawen.parser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Il n'y a qu'une base de données qui ne change pas donc cet objet simpliste
 * est justifié.
 * 
 * @author caocoa
 *
 */
public abstract class Database {
	// Static final class
	private Database() {
	}
}