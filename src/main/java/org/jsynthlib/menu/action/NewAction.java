package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.LibraryFrame;

public class NewAction extends AbstractAction {
	public NewAction(Map<Serializable, Integer> mnemonics) {
		super("New Library", null);
		mnemonics.put(this, new Integer('N'));
	}

	public void actionPerformed(ActionEvent e) {
		Actions.addLibraryFrame(new LibraryFrame());
	}
}