package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.preferences.DeviceDialog;

// //////////////////////////////////////////////////////////////////////
/*
 * Now we start with the various action classes. Each of these performs one of the menu commands and are called
 * either from the menubar, popup menu or toolbar.
 */
public class ShowDevicesAction extends AbstractAction {
	public ShowDevicesAction(Map<Serializable, Integer> mnemonics) {
		super("Supported synthesizer");
		mnemonics.put(this, new Integer('S'));
	}

	public void actionPerformed(ActionEvent e) {
		DeviceDialog dad = new DeviceDialog(null, true);
		dad.setVisible(true);
	}
}