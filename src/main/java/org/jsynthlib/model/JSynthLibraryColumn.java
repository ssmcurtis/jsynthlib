package org.jsynthlib.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;

public enum JSynthLibraryColumn {
	SCORE("score", "U", true, 50),
	SYNTH("synth", "S", true, 50),
	TYPE("type", "T", true, 40),
	PATCH_NAME("patch name", "P", true, 50),
	IMPORT_DATE("date", "D", true, 50),
	AUTHOR("author", "A", true, 50),
	FILENAME("filename", "F", true, 100),
	INFO("info", "I", true, 50),
	COMMENT("comment", "C", true, 50),
	PATCHID("patchid", "I", false, 0);

	// not used yet
	private final String title;
	private final String actionCommand;
	private final int preferredWidth;
	private final boolean visible;

	JSynthLibraryColumn(String title, String actionCommand, boolean visible, int preferredWidth) {
		this.title = title;
		this.actionCommand = actionCommand;
		this.visible = visible;
		this.preferredWidth = preferredWidth;

	}

	public static int getOrdinal(JSynthLibraryColumn column) {
		for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
			if (col.equals(column)) {
				return col.ordinal();
			}
		}
		return -1;
	}

	public static JSynthLibraryColumn getLibraryColumn(int ordinal) {
		for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
			if (col.ordinal() == ordinal) {
				return col;
			}
		}
		return JSynthLibraryColumn.TYPE;
	}

	public static String[] getVisileColumnAsString() {
		List<String> li = new ArrayList<String>();
		for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
			if (col.isVisible()) {
				li.add(col.getTitle());
			}
		}
		return li.toArray(new String[] {});
	}

	public static List<JSynthLibraryColumn> getSeachableColumn() {
		return Arrays.asList(JSynthLibraryColumn.SYNTH, JSynthLibraryColumn.TYPE, JSynthLibraryColumn.PATCH_NAME,
				JSynthLibraryColumn.IMPORT_DATE, JSynthLibraryColumn.AUTHOR, JSynthLibraryColumn.FILENAME, JSynthLibraryColumn.INFO,
				JSynthLibraryColumn.COMMENT);
	}

	public static boolean isEditable(JSynthLibraryColumn column) {
		switch (column) {
		case SYNTH:
		case SCORE:
		case TYPE:
		case INFO:
		case PATCHID:
			return false;
		case PATCH_NAME:
		case IMPORT_DATE:
		case AUTHOR:
		case COMMENT:
		case FILENAME:
			return true;
		default:
			ErrorMsgUtil.reportStatus("LibraryFrame.getValueAt: internal error.");
			return false;
		}
	}

	public static JSynthLibraryColumn getLibraryColumnForActionCommand(String actionCommand) {
		for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
			if (col.getActionCommand().equals(actionCommand)) {
				return col;
			}
		}
		return null;
	}

	public static String getPropertyValue(Patch patch, JSynthLibraryColumn column) throws NullPointerException {
		switch (column) {
		case SYNTH:
			return patch.getDevice().getSynthName();
		case TYPE:
			return patch.getType();
		case PATCH_NAME:
			return patch.getName();
		case IMPORT_DATE:
			return patch.getDate();
		case AUTHOR:
			return patch.getAuthor();
		case COMMENT:
			return patch.getComment();
		case FILENAME:
			return patch.getFileName();
		case PATCHID:
			return patch.getPatchId();
		case INFO:
			return patch.getInfo();
		case SCORE:
			if (patch.getSelected()) {
				return "x";
			} else {
				if (patch.getScore() == 0) {
					return "";
				} else {
					return patch.getScore().toString();
				}
			}
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

	public int getPreferredWidth() {
		return preferredWidth;
	}

}