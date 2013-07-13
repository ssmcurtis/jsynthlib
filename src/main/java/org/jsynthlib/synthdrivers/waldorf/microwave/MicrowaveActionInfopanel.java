package org.jsynthlib.synthdrivers.waldorf.microwave;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsgUtil;

public class MicrowaveActionInfopanel extends AbstractAction {

	public MicrowaveActionInfopanel(Map<Serializable, Integer> mnemonics) {
		super("Microwave Infopanel", null);
		this.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		ErrorMsgUtil.reportError("NOT IMPLEMENTED", "Tone/Patch");
	}
}