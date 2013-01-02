package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.SysexGetDialog;

public class GetAction extends AbstractAction {
	
	public GetAction(Map<Serializable, Integer> mnemonics) {
		super("Get...", null);
		mnemonics.put(this, new Integer('G'));
		this.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
		SysexGetDialog myDialog = new SysexGetDialog(PatchBayApplication.getInstance());
		myDialog.setVisible(true);
	}

} 