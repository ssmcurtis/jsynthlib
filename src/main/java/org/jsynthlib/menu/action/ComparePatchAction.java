package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.window.HexDumpEditorHighlighted;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;

public class ComparePatchAction extends AbstractAction {
	public ComparePatchAction(Map<Serializable, Integer> mnemonics) {
		super("Compare 2 patches");
		mnemonics.put(this, new Integer('S'));

	}

	public void actionPerformed(ActionEvent e) {
		// get patches
		Patch[] patches = Actions.getSelectedFrame().getSelectedPatches();
		if (patches.length > 1) {
			try {

				JSLFrame frm = new HexDumpEditorHighlighted(patches[0], patches[1]);

				if (frm != null) {
					PatchBayApplication.getDesktop().add(frm);
					frm.moveToDefaultLocation();
					frm.setVisible(true);
					try {
						frm.setSelected(true);
					} catch (PropertyVetoException ex) {
						ErrorMsgUtil.reportStatus(ex);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}