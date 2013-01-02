package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.SearchDialog;
import org.jsynthlib.tools.ErrorMsgUtil;

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
			ErrorMsgUtil.reportError("Error", "Library to Sort must be Focused"+ getClass().getSimpleName(), ex);
		}
	}
}