package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;


public class SaveAction extends AbstractAction {
	public SaveAction(Map<Serializable, Integer> mnemonics) {
		super("Save", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('S'));
	}

	public void actionPerformed(ActionEvent e) {
		Actions.saveFrame();
	}
}