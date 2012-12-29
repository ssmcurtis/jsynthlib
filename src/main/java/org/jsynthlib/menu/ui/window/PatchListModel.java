package org.jsynthlib.menu.ui.window;

import java.util.ArrayList;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.model.LibraryColumn;
import org.jsynthlib.tools.ErrorMsg;

/**
 * Refactored from PerformanceListModel
 * 
 * @author Gerrit Gehnen
 */
@SuppressWarnings("serial")
class PatchListModel extends PatchTableModel {
	private final String[] columnNames = LibraryColumn.getVisileColumnNames();

	private ArrayList<IPatch> list = new ArrayList<IPatch>();

	PatchListModel() {
	}

	// begin PatchTableModel interface methods
	// It is caller's responsibility to update Table.
	public void addPatch(IPatch p) {
		ErrorMsg.reportStatus("LibraryFrame.addPatch: Patch=" + p);
		list.add(p);
	}

	public void addPatch(IPatch p, int bankNum, int patchNum) {
		ErrorMsg.reportStatus("LibraryFrame.addPatch: Patch=" + p);
		list.add(p);
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this
	 * method, then the last column would contain text ("true"/"false"), rather than a check box.
	 */
	public Class<String> getColumnClass(int c) {
		return String.class;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public String getCommentAt(int row) {
		return getPatchAt(row).getComment();
	}

	public ArrayList<IPatch> getList() {
		return this.list;
	}

	public IPatch getPatchAt(int row) {
		return list.get(row);
	}

	public int getRowCount() {
		return list.size();
	}

	public Object getValueAt(int row, int col) {
		IPatch myPatch = (IPatch) list.get(row);
		LibraryColumn column = LibraryColumn.getLibraryColumn(col);

		try {
			return LibraryColumn.getPropertyValue(myPatch, column);
		} catch (NullPointerException e) {
			ErrorMsg.reportStatus("LibraryFrame.getValueAt: row=" + row + ", col=" + column + ", Patch=" + myPatch);
			ErrorMsg.reportStatus("row count =" + getRowCount());
			// e.printStackTrace();
			return null;
		}
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return LibraryColumn.isEditable(LibraryColumn.getLibraryColumn(col));
	}

	public void removeAt(int row) {
		this.list.remove(row);
	}

	public void setList(ArrayList<IPatch> newList) {
		this.list = newList;
	}

	// end PatchTableModel interface methods

	public void setPatchAt(IPatch p, int row) {
		ErrorMsg.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
		list.set(row, p);
	}

	public void setPatchAt(IPatch p, int row, int bankNum, int patchNum) {
		ErrorMsg.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
		list.set(row, p);
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		// TODO: Remove these comments after June, 2006
		// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
		// - Emenaker 2006-02-21
		// changed = true;
		IPatch myPatch = (IPatch) list.get(row);

		LibraryColumn column = LibraryColumn.getLibraryColumn(col);

		switch (column) {
		case PATCH_NAME:
			myPatch.setName((String) value);
			break;
		case FIELD1:
			myPatch.setDate((String) value);
			break;
		case FIELD2:
			myPatch.setAuthor((String) value);
			break;
		case COMMENT:
			myPatch.setComment((String) value);
			break;
		case FILENAME:
			myPatch.setFileName((String) value);
			break;
		case PATCHID:
			myPatch.setPatchId((String) value);
			break;
		case INFO:
			myPatch.setInfo((String) value);
			break;
		default:
			ErrorMsg.reportStatus("LibraryFrame.setValueAt: internal error." + column);
		}
		fireTableCellUpdated(row, col);
	}
}