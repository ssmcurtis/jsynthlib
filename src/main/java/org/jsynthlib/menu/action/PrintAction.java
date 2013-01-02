package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.BankEditorFrame;
import org.jsynthlib.tools.ErrorMsgUtil;

// Added by Joe Emenaker - 2005-10-24
public class PrintAction extends AbstractAction {
	public PrintAction(Map<Serializable, Integer> mnemonics) {
		super("Print", null);
		// mnemonics.put(this, new Integer(''));
		this.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getSelectedFrame() instanceof BankEditorFrame) {
				((BankEditorFrame) Actions.getSelectedFrame()).printPatch();
			} else {
				ErrorMsgUtil.reportError("Error", "You can only print a Bank window.");
			}
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to Play must be highlighted in the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}