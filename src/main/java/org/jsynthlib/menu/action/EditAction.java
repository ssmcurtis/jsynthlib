package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;


public class EditAction extends AbstractAction {
	public EditAction(Map<Serializable, Integer> mnemonics) {
		super("Edit...", null);
		mnemonics.put(this, new Integer('E'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		Actions.EditActionProc();
	}
}