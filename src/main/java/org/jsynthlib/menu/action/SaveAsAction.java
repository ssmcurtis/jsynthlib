package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;


public class SaveAsAction extends AbstractAction {
	public SaveAsAction(Map<Serializable, Integer> mnemonics) {
		super("Save As...", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
		Actions.saveFrameAs();
	}
}