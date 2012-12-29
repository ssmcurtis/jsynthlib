package org.jsynthlib.model;

import java.util.Arrays;
import java.util.List;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.tools.ErrorMsg;

public enum LibraryColumn {
	SYNTH("synth", "S", true),
	TYPE("type", "T", true),
	PATCH_NAME("patch name", "P", true),
	FIELD1("date", "D", true),
	FIELD2("author", "A", true),
	FILENAME("filename", "F", true),
	INFO("info", "I", true),
	COMMENT("comment", "C", true),
	PATCHID("patchid", "I", false);

	// not used yet
	private final String title;
	private final String actionCommand;
	private final boolean visible;

	LibraryColumn(String title, String actionCommand, boolean visible) {
		this.visible = visible;
		this.title = title;
		this.actionCommand = actionCommand;

	}

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

	public static String[] getVisileColumnNames() {
		return new String[] { LibraryColumn.SYNTH.title, LibraryColumn.TYPE.title, LibraryColumn.PATCH_NAME.title,
				LibraryColumn.FIELD1.title, LibraryColumn.FIELD2.title, LibraryColumn.FILENAME.title, LibraryColumn.INFO.title,
				LibraryColumn.COMMENT.title, };
	}

	public static List<LibraryColumn> getSeachableColumn() {
		return Arrays.asList(LibraryColumn.SYNTH, LibraryColumn.TYPE, LibraryColumn.PATCH_NAME, LibraryColumn.FIELD1, LibraryColumn.FIELD2,
				LibraryColumn.FILENAME, LibraryColumn.INFO, LibraryColumn.COMMENT);
	}

	public static boolean isEditable(LibraryColumn column) {
		switch (column) {
		case SYNTH:
		case TYPE:
		case INFO:
		case PATCHID:
			return false;
		case PATCH_NAME:
		case FIELD1:
		case FIELD2:
		case COMMENT:
		case FILENAME:
			return true;
		default:
			ErrorMsg.reportStatus("LibraryFrame.getValueAt: internal error.");
			return false;
		}
	}

	public static LibraryColumn getLibraryColumnForActionCommand(String actionCommand) {
		for (LibraryColumn col : LibraryColumn.values()) {
			if (col.getActionCommand().equals(actionCommand)) {
				return col;
			}
		}
		return null;
	}

	public static String getPropertyValue(IPatch patch, LibraryColumn column) throws NullPointerException {
		switch (column) {
		case SYNTH:
			return patch.getDevice().getSynthName();
		case TYPE:
			return patch.getType();
		case PATCH_NAME:
			return patch.getName();
		case FIELD1:
			return patch.getDate();
		case FIELD2:
			return patch.getAuthor();
		case COMMENT:
			return patch.getComment();
		case FILENAME:
			return patch.getFileName();
		case PATCHID:
			return patch.getPatchId();
		case INFO:
			return patch.getInfo();
		default:
			return null;
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public String getTitle() {
		return title;
	}

	public String getActionCommand() {
		return actionCommand;
	}

}