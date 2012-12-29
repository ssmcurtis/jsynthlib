package org.jsynthlib.menu.ui.window;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.patch.Scene;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.menu.ui.PatchesAndScenes;
import org.jsynthlib.tools.ErrorMsg;

/**
 * SceneListTransferHandler
 * 
 * This class extends PatchTransferHandler by allowing scenes to be placed into the Transferable and also allowing
 * scenes to be inserted into the JTable as *scenes* and not just as the inner patch data (which is the default
 * behavior of PatchTransferHandler) - Emenaker - 2006-02-27
 */
public class SceneListTransferHandler {// extends PatchTransferHandler {

//	protected Transferable createTransferable(JComponent c) {
//		PatchesAndScenes patchesAndScenes = new PatchesAndScenes();
//		if (c instanceof JTable) {
//			JTable table = (JTable) c;
//			SceneListModel slm = (SceneListModel) table.getModel();
//			int[] rowIdxs = table.getSelectedRows();
//			for (int i = 0; i < rowIdxs.length; i++) {
//				Scene scene = slm.getSceneAt(rowIdxs[i]);
//				patchesAndScenes.add(scene);
//			}
//		} else {
//			ErrorMsg.reportStatus("PatchTransferHandler.createTransferable doesn't recognize the component it was given");
//		}
//		return (patchesAndScenes);
//	}
//
//	protected boolean storeScene(Scene s, JComponent c) {
//		SceneListModel model = (SceneListModel) ((JTable) c).getModel();
//		model.addScene(s);
//		ErrorMsg.reportStatus("Stored a Scene into a SceneList");
//		// TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
//		model.fireTableDataChanged();
//		return true;
//	}
//
//	protected boolean storePatch(IPatch p, JComponent c) {
//		SceneListModel model = (SceneListModel) ((JTable) c).getModel();
//		model.addPatch(p);
//		// TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
//		model.fireTableDataChanged();
//		return true;
//		// return false;
//	}
}