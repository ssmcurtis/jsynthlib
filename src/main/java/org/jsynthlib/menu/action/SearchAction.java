package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.ui.window.SearchDialog;
import org.jsynthlib.tools.ErrorMsg;

public class SearchAction extends AbstractAction {
	public SearchAction(Map<Serializable, Integer> mnemonics) {
		super("Search...", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('E'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getSearchDialog() == null)
				Actions.setSearchDialog(new SearchDialog(PatchBayApplication.getInstance()));
			Actions.getSearchDialog().setVisible(true);
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
		}
	}
}