package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.tools.ErrorMsgUtil;

public class SelectClearAction extends AbstractAction {
	public SelectClearAction(Map<Serializable, Integer> mnemonics) {
		super("Clear selection", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('R'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			((LibraryFrame)Actions.getSelectedFrame()).selectClear();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Library must be focused "+ getClass().getSimpleName(), ex);
		}
	}
}