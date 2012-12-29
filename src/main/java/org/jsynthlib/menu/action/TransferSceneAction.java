package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.SceneFrame;
import org.jsynthlib.tools.ErrorMsg;

public class TransferSceneAction extends AbstractAction {
	public TransferSceneAction(Map<Serializable, Integer> mnemonics) {
		super("Transfer Scene", null); // show a dialog frame???
		// mnemonics.put(this, new Integer('S'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			// ((SceneFrame) Actions.getSelectedFrame()).sendScene();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
		}
	}
}