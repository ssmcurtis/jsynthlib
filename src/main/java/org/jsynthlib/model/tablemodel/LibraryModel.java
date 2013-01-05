package org.jsynthlib.model.tablemodel;

import java.util.ArrayList;

import javax.swing.table.TableColumn;

import org.jsynthlib.model.JSynthLibraryColumn;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Refactored from PerformanceListModel
 * 
 * @author Gerrit Gehnen
 */
@SuppressWarnings("serial")
public class LibraryModel extends PatchTableModel {
	private final String[] columnNames = JSynthLibraryColumn.getVisileColumnAsString();

	private ArrayList<Patch> list = new ArrayList<Patch>();

	public LibraryModel() {
	}

	// begin PatchTableModel interface methods
	// It is caller's responsibility to update Table.
	public void addPatch(Patch p) {
//		ErrorMsgUtil.reportStatus("LibraryFrame.addPatch: Patch=" + p);
		list.add(p);
	}
	
	public void addPatch(int position, Patch p) {
//		ErrorMsgUtil.reportStatus("LibraryFrame pos: " + position+" patch: " + p);
		list.add(position, p);
	}
	
	public void addPatch(Patch p, int bankNum, int patchNum) {
//		ErrorMsgUtil.reportStatus("LibraryFrame.addPatch: Patch=" + p);
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

	public ArrayList<Patch> getList() {
		return this.list;
	}

	// TODO ssmCurtis - sort table - look for all references
	public Patch getPatchAt(int row) {
		// table.convertRowIndexToModel(..
		return list.get(row);
	}

	public int getRowCount() {
		return list.size();
	}

	public Object getValueAt(int row, int col) {
		Patch myPatch = (Patch) list.get(row);
		JSynthLibraryColumn column = JSynthLibraryColumn.getLibraryColumn(col);

		try {
			return JSynthLibraryColumn.getPropertyValue(myPatch, column);
		} catch (NullPointerException e) {
			ErrorMsgUtil.reportStatus("LibraryFrame.getValueAt: row=" + row + ", col=" + column + ", Patch=" + myPatch);
			ErrorMsgUtil.reportStatus("row count =" + getRowCount());
			// e.printStackTrace();
			return null;
		}
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return JSynthLibraryColumn.isEditable(JSynthLibraryColumn.getLibraryColumn(col));
	}

	public void removeAt(int row) {
		this.list.remove(row);
	}

	public void setList(ArrayList<Patch> newList) {
		this.list = newList;
	}

	// end PatchTableModel interface methods

	public void setPatchAt(Patch p, int row) {
		ErrorMsgUtil.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
		list.set(row, p);
	}

	public void setPatchAt(Patch p, int row, int bankNum, int patchNum) {
		ErrorMsgUtil.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
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
		Patch myPatch = (Patch) list.get(row);

		JSynthLibraryColumn column = JSynthLibraryColumn.getLibraryColumn(col);

		switch (column) {
		case PATCH_NAME:
			myPatch.setName((String) value);
			break;
		case IMPORT_DATE:
			myPatch.setDate((String) value);
			break;
		case AUTHOR:
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
			ErrorMsgUtil.reportStatus("LibraryFrame.setValueAt: internal error." + column);
		}
		fireTableCellUpdated(row, col);
	}
}