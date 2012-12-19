package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;

public class PrefsAction extends AbstractAction {
	public PrefsAction(Map<Serializable, Integer> mnemonics) {
		super("Preferences...", null);
		mnemonics.put(this, new Integer('P'));
	}

	public void actionPerformed(ActionEvent e) {
		PatchBayApplication.showPrefsDialog();
	}
}