package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.JSynthResource;
import org.jsynthlib.menu.ui.window.DocumentationWindow;
import org.jsynthlib.tools.ErrorMsg;

public class DocsAction extends AbstractAction {
	public DocsAction(Map<Serializable, Integer> mnemonics) {
		super("Help", null);
		this.setEnabled(true);
		mnemonics.put(this, new Integer('H'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getDocWin() == null)
				Actions.setDocWin(new DocumentationWindow("text/plain",
						Actions.class.getResource(JSynthResource.RESOURCE_NAME_DOCUMENTATION.getUri())));
			Actions.getDocWin().setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
		}
	}
}