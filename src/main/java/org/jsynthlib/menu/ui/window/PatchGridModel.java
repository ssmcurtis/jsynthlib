package org.jsynthlib.menu.ui.window;

import javax.swing.table.AbstractTableModel;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.tools.ErrorMsg;

public class PatchGridModel extends AbstractTableModel {

	/**
	 * 
	 */
	private final BankEditorFrame bankEditorFrame;

	public PatchGridModel(BankEditorFrame bankEditorFrame) {
		super();
		this.bankEditorFrame = bankEditorFrame;
		ErrorMsg.reportStatus("PatchGridModel");
	}

	public int getColumnCount() {
		return this.bankEditorFrame.bankData.getNumColumns();
	}

	public int getRowCount() {
		return this.bankEditorFrame.bankData.getNumPatches() / this.bankEditorFrame.bankData.getNumColumns();
	}

	public String getColumnName(int col) {
		return "";
	}

	public Object getValueAt(int row, int col) {
		String[] patchNumbers = this.bankEditorFrame.bankData.getDriver().getPatchNumbers();
		int i = this.bankEditorFrame.getPatchNum(row, col);
		return (patchNumbers[i] + " " + this.bankEditorFrame.bankData.getName(i));
	}

	public Class getColumnClass(int c) {
		return String.class;
	}

	public boolean isCellEditable(int row, int col) {
		// ----- Start phil@muqus.com (allow patch name editing from a bank
		// edit window)
		// return false;
		return true;
		// ----- End phil@muqus.com
	}

	public void setValueAt(Object value, int row, int col) { // not used
		int patchNum = this.bankEditorFrame.getPatchNum(row, col);
		String[] patchNumbers = this.bankEditorFrame.bankData.getDriver().getPatchNumbers();
		this.bankEditorFrame.bankData.setName(patchNum,
				((String) value).substring((patchNumbers[patchNum] + " ").length()));
	}

	public IPatch getPatchAt(int row, int col) {
		return this.bankEditorFrame.bankData.get(this.bankEditorFrame.getPatchNum(row, col));
	}

	public void setPatchAt(IPatch p, int row, int col) {
		this.bankEditorFrame.bankData.put(p, this.bankEditorFrame.getPatchNum(row, col));
	}
}