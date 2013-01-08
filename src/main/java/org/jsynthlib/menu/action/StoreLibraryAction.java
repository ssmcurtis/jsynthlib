package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.window.StoreLibraryWindow;

public class StoreLibraryAction extends AbstractAction {
	public StoreLibraryAction(Map<Serializable, Integer> mnemonics) {
		super("Store libray", null);
		// mnemonics.put(this, new Integer('S'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		
		StoreLibraryWindow action = new StoreLibraryWindow();
		action.launchFrame();
	}
}