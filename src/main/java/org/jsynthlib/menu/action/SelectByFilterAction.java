package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.AbstractLibraryFrame;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.menu.window.SelectByFilterDialog;
import org.jsynthlib.menu.window.SortDialog;
import org.jsynthlib.tools.ErrorMsgUtil;

public class SelectByFilterAction extends AbstractAction {
	public SelectByFilterAction(Map<Serializable, Integer> mnemonics) {
		super("Select by filter", null);
		this.setEnabled(false);
		mnemonics.put(this, new Integer('R'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getSelectByFilterDialog() == null) {
				Actions.setSelectByFilterDialog(new SelectByFilterDialog(PatchBayApplication.getInstance()));
			}
			Actions.getSelectByFilterDialog().setVisible(true);
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Library to Sort must be Focused" + getClass().getSimpleName(), ex);
		}
	}
}