package org.jsynthlib.menu;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.tablemodel.PatchTableModel;

@SuppressWarnings("serial")
public class PatchListTransferHandler extends PatchTransferHandler {

	protected boolean storePatch(Patch p, JComponent component) {
		PatchTableModel model = (PatchTableModel) ((JTable) component).getModel();
		
		model.addPatch(p);
		
		// TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
		model.fireTableDataChanged();
		return true;
	}

	// only for debugging
	// protected void exportDone(JComponent source, Transferable data, int action) {
	// ErrorMsg.reportStatus("PatchListTransferHandler.exportDone " + data);
	// }
}