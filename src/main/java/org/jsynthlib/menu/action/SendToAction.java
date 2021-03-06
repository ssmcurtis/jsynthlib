package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class SendToAction extends AbstractAction {
	public SendToAction(Map<Serializable, Integer> mnemonics) {
		super("Send to...", null);
		// mnemonics.put(this, new Integer('S'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Actions.getSelectedFrame().sendToSelectedPatch();
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Patch to 'Send to...' must be highlighted in the focused Window."+ getClass().getSimpleName(), ex);
		}
	}
}