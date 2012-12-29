package org.jsynthlib.menu.ui.window;

import java.util.ArrayList;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.patch.Scene;
import org.jsynthlib.tools.ErrorMsg;

/**
 * Refactored from PerformanceListModel
 * 
 * @author Gerrit Gehnen
 */
class SceneListModel  { //extends PatchTableModel {
//	private final String[] columnNames = { "Synth", "Type", "Patch Name", "Bank Number", "Patch Number", "Comment" };
//
//	// TODO: Remove these comments after June, 2006
//	// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
//	// - Emenaker 2006-02-21
//	// private boolean changed;
//
//	private ArrayList list = new ArrayList();
//
//	public SceneListModel(/* boolean c */) {
//		// TODO: Remove these comments after June, 2006
//		// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
//		// - Emenaker 2006-02-21
//		// changed = c;
//	}
//
//	public int getRowCount() {
//		return list.size();
//	}
//
//	public int getColumnCount() {
//		return columnNames.length;
//	}
//
//	public String getColumnName(int col) {
//		return columnNames[col];
//	}
//
//	public Object getValueAt(int row, int col) {
//		Scene myScene = (Scene) list.get(row);
//		IPatch myPatch = myScene.getPatch();
//		try {
//			switch (col) {
//			case SceneFrame.SYNTH:
//				return myPatch.getDevice().getSynthName();
//			case SceneFrame.TYPE:
//				return myPatch.getType();
//			case SceneFrame.PATCH_NAME:
//				return myPatch.getName();
//			case SceneFrame.BANK_NUM:
//				// generic driver returns null
//				String[] bn = myPatch.getDriver().getBankNumbers();
//				if (bn != null)
//					return bn[myScene.getBankNumber()];
//				else
//					return String.valueOf(myScene.getBankNumber());
//			case SceneFrame.PATCH_NUM:
//				String[] pn = myPatch.getDriver().getPatchNumbers();
//				if (pn != null)
//					return pn[myScene.getPatchNumber()];
//				else
//					return String.valueOf(myScene.getPatchNumber());
//			case SceneFrame.COMMENT:
//				return myScene.getComment();
//			default:
//				ErrorMsg.reportStatus("SceneFrame.getValueAt: internal error.");
//				return null;
//			}
//		} catch (NullPointerException e) {
//			ErrorMsg.reportStatus("SceneFrame.getValueAt: row=" + row + ", col=" + col + ", Patch=" + myPatch);
//			ErrorMsg.reportStatus("row count =" + getRowCount());
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/*
//	 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this
//	 * method, then the last column would contain text ("true"/"false"), rather than a check box.
//	 */
//	public Class getColumnClass(int c) {
//		return String.class;
//	}
//
//	public boolean isCellEditable(int row, int col) {
//		return (col > SceneFrame.PATCH_NAME && !((col == SceneFrame.BANK_NUM || col == SceneFrame.PATCH_NUM) && ((Scene) list.get(row)).getPatch()
//				.hasNullDriver()));
//	}
//
//	public void setValueAt(Object value, int row, int col) {
//		// ErrorMsg.reportStatus("SetValue at "+row+" "+col+"
//		// Value:"+value);
//		// TODO: Remove these comments after June, 2006
//		// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
//		// - Emenaker 2006-02-21
//		// changed = true;
//		Scene myScene = getSceneAt(row);
//		switch (col) {
//		case SceneFrame.BANK_NUM:
//			myScene.setBankNumber(((Integer) value).intValue());
//			break;
//		case SceneFrame.PATCH_NUM:
//			myScene.setPatchNumber(((Integer) value).intValue());
//			break;
//		case SceneFrame.COMMENT:
//			myScene.setComment((String) value);
//			break;
//		default:
//			ErrorMsg.reportStatus("SceneFrame.setValueAt: internal error.");
//		}
//		list.set(row, myScene);
//	}
//
//	// begin PatchTableModel interface methods
//	// It is caller's responsibility to update Table.
//	public void addPatch(IPatch p) {
//		list.add(new Scene(p));
//	}
//
//	public void addPatch(IPatch p, int bankNum, int patchNum) { 
//		list.add(new Scene(p, bankNum, patchNum));
//	}
//
//	public void setPatchAt(IPatch p, int row, int bankNum, int patchNum) { 
//		list.set(row, new Scene(p, bankNum, patchNum));
//	}
//
//	public void setPatchAt(IPatch p, int row) {
//		list.set(row, new Scene(p));
//	}
//
//	public IPatch getPatchAt(int row) {
//		// return ((Scene) list.get(row)).getPatch();
//		return ((Scene) list.get(row)).getPatch();
//	}
//
//	public String getCommentAt(int row) {
//		return ((Scene) list.get(row)).getComment();
//	}
//
//	public void removeAt(int row) {
//		this.list.remove(row);
//	}
//
//	public ArrayList getList() {
//		return this.list;
//	}
//
//	public void setList(ArrayList newList) {
//		this.list = newList;
//	}
//
//	// end PatchTableModel interface methods
//
//	public Scene getSceneAt(int row) {
//		return (Scene) list.get(row);
//	}
//
//	public void addScene(Scene s) {
//		list.add(s);
//	}
}