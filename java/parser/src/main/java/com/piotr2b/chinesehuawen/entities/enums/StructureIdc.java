/**
 * This class is generated by jOOQ
 */
package com.piotr2b.chinesehuawen.entities.enums;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public enum StructureIdc implements org.jooq.EnumType {

	_2ff0("⿰"),

	_2ff1("⿱"),

	_2ff2("⿲"),

	_2ff3("⿳"),

	_2ff4("⿴"),

	_2ff5("⿵"),

	_2ff6("⿶"),

	_2ff7("⿷"),

	_2ff8("⿸"),

	_2ff9("⿹"),

	_2ffa("⿺"),

	_2ffb("⿻");

	private final java.lang.String literal;

	private StructureIdc(java.lang.String literal) {
		this.literal = literal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Schema getSchema() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String getName() {
		return "structure_idc";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String getLiteral() {
		return literal;
	}
}
