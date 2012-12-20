package org.jsynthlib.menu.ui.window;

// column indices
public enum LibraryColumn {
	SYNTH,
	TYPE,
	PATCH_NAME,
	FIELD1,
	FIELD2,
	FILENAME,
	PATCHID,
	INFO,
	COMMENT;

	public static int getOrdinal(LibraryColumn column) {
		for (LibraryColumn col : LibraryColumn.values()) {
			if (col.equals(column)) {
				return col.ordinal();
			}
		}
		return -1;
	}

	public static LibraryColumn getLibraryColumn(int ordinal) {
		for (LibraryColumn col : LibraryColumn.values()) {
			if (col.ordinal() == ordinal) {
				return col;
			}
		}
		return LibraryColumn.TYPE;
	}
	
	public static String[] getColumNames() {
		return new String[]{ "Synth", "Type", "Patch Name", "Date", "Author", "File name",
			"Patch ID", "Info", "Comment" };
	}
}