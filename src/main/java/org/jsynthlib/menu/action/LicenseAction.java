package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.JSynthResource;
import org.jsynthlib.menu.ui.window.DocumentationWindow;
import org.jsynthlib.tools.ErrorMsg;

public class LicenseAction extends AbstractAction {
	public LicenseAction(Map<Serializable, Integer> mnemonics) {
		super("License", null);
		this.setEnabled(true);
		mnemonics.put(this, new Integer('L'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getLicWin() == null)
				Actions.setLicWin(new DocumentationWindow("text/plain", Actions.class
						.getResource(JSynthResource.RESOURCE_NAME_LICENSE.getUri())));
			Actions.getLicWin().setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
		}
	}
}