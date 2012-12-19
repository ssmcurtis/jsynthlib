package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.SceneFrame;
import org.jsynthlib.tools.ErrorMsg;

public class UpdateSceneAction extends AbstractAction {// wirski@op.pl
	public UpdateSceneAction(Map<Serializable, Integer> mnemonics) {
		super("Update Scene", null);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			((SceneFrame) Actions.getSelectedFrame()).updateScene();
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
		}
	}
}