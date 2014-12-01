package com.piotr2b.chinesehuawen.parser;

import static com.piotr2b.chinesehuawen.entities.Tables.SINOGRAM;

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
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.piotr2b.chinesehuawen.entities.Tables;
import com.piotr2b.chinesehuawen.entities.tables.Sinogram;

/**
 * Il n'y a qu'une base de données qui ne change pas donc cet objet simpliste
 * est justifié.
 * 
 * @author caocoa
 *
 */
public abstract class Database {
	// Static class
	private Database() {
	}

	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;

	public static Result<Record> select(Function<? super DSLContext, ? extends Result<Record>> function) {

		Result<Record> result = null;
		try {
			new org.mariadb.jdbc.Driver();

			String connectionUrl = "jdbc:mariadb://localhost:3306/huawen";
			String connectionUser = "huawen";
			String connectionPassword = "huawen";
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);

			DSLContext context = DSL.using(conn, SQLDialect.MARIADB);
			result = function.apply(context);

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
		return result;
	}

	public static Result<Record> insert(Node node) {

		Result<Record> result = null;
		try {
			new org.mariadb.jdbc.Driver();

			String connectionUrl = "jdbc:mariadb://localhost:3306/huawen";
			String connectionUser = "huawen";
			String connectionPassword = "huawen";
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);

			DSLContext create = DSL.using(conn, SQLDialect.MARIADB);

			// create.insertInto(SINOGRAM)//
			// .set(SINOGRAM.CP, Integer.toString(node.getId())) //
			// .set(SINOGRAM.SEMANTICS, node.getIDS()) //
			// .set(SINOGRAM.CONSONANTS, "") //
			// .set(SINOGRAM.RHYME, "")//
			// .set(SINOGRAM.TONE, 1)//
			// .set(SINOGRAM.STROKE, new Byte("1"))//
			// .set(SINOGRAM.FREQUENCY, new Byte("1"));
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
		return result;
	}
}