package org.jsynthlib.menu.ui.window;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.ui.PatchTransferHandler;

@SuppressWarnings("serial")
class PatchListTransferHandler extends PatchTransferHandler {

	protected boolean storePatch(IPatch p, JComponent c) {
		PatchTableModel model = (PatchTableModel) ((JTable) c).getModel();
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