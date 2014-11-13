/**
 * This class is generated by jOOQ
 */
package com.piotr2b.chinesehuawen.entities;

/**
 * This class is generated by jOOQ.
 *
 * A class modelling foreign key relationships between tables of the <code>chinese</code> 
 * schema
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.AllographyRecord> KEY_ALLOGRAPHY_PRIMARY = UniqueKeys0.KEY_ALLOGRAPHY_PRIMARY;
	public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> KEY_SINOGRAM_PRIMARY = UniqueKeys0.KEY_SINOGRAM_PRIMARY;
	public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord> KEY_STRUCTURE_PRIMARY = UniqueKeys0.KEY_STRUCTURE_PRIMARY;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------

	public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.AllographyRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> ALLOGRAPHY_IBFK_1 = ForeignKeys0.ALLOGRAPHY_IBFK_1;
	public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> STRUCTURE_IBFK_1 = ForeignKeys0.STRUCTURE_IBFK_1;
	public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> STRUCTURE_IBFK_2 = ForeignKeys0.STRUCTURE_IBFK_2;

	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class UniqueKeys0 extends org.jooq.impl.AbstractKeys {
		public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.AllographyRecord> KEY_ALLOGRAPHY_PRIMARY = createUniqueKey(com.piotr2b.chinesehuawen.entities.tables.Allography.ALLOGRAPHY, com.piotr2b.chinesehuawen.entities.tables.Allography.ALLOGRAPHY.CP, com.piotr2b.chinesehuawen.entities.tables.Allography.ALLOGRAPHY.CLASS);
		public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> KEY_SINOGRAM_PRIMARY = createUniqueKey(com.piotr2b.chinesehuawen.entities.tables.Sinogram.SINOGRAM, com.piotr2b.chinesehuawen.entities.tables.Sinogram.SINOGRAM.CP);
		public static final org.jooq.UniqueKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord> KEY_STRUCTURE_PRIMARY = createUniqueKey(com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.FATHER_CP, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.SON_CP, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.IDC, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.ORDINAL);
	}

	private static class ForeignKeys0 extends org.jooq.impl.AbstractKeys {
		public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.AllographyRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> ALLOGRAPHY_IBFK_1 = createForeignKey(com.piotr2b.chinesehuawen.entities.Keys.KEY_SINOGRAM_PRIMARY, com.piotr2b.chinesehuawen.entities.tables.Allography.ALLOGRAPHY, com.piotr2b.chinesehuawen.entities.tables.Allography.ALLOGRAPHY.CP);
		public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> STRUCTURE_IBFK_1 = createForeignKey(com.piotr2b.chinesehuawen.entities.Keys.KEY_SINOGRAM_PRIMARY, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.FATHER_CP);
		public static final org.jooq.ForeignKey<com.piotr2b.chinesehuawen.entities.tables.records.StructureRecord, com.piotr2b.chinesehuawen.entities.tables.records.SinogramRecord> STRUCTURE_IBFK_2 = createForeignKey(com.piotr2b.chinesehuawen.entities.Keys.KEY_SINOGRAM_PRIMARY, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE, com.piotr2b.chinesehuawen.entities.tables.Structure.STRUCTURE.SON_CP);
	}
}