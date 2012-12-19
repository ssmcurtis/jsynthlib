package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.ui.window.SysexGetDialog;

public class GetAction extends AbstractAction {
	// -----------------------------------------------------------------
	// Constructor: GetAction
	// -----------------------------------------------------------------
	public GetAction(Map<Serializable, Integer> mnemonics) {
		super("Get...", null);
		mnemonics.put(this, new Integer('G'));
		this.setEnabled(false);
	}

	// -----------------------------------------------------------------
	// GetAction->actionPerformed
	// -----------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		SysexGetDialog myDialog = new SysexGetDialog(PatchBayApplication.getInstance());
		myDialog.setVisible(true);
	}

} // End SubClass: GetAction