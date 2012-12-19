package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;

public class ExitAction extends AbstractAction {
	public ExitAction(Map<Serializable, Integer> mnemonics) {
		super("Exit", null);
		mnemonics.put(this, new Integer('X'));
	}

	public void actionPerformed(ActionEvent e) {
		PatchBayApplication.getDesktop().closingProc();
	}
}