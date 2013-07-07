package org.jsynthlib.synthdrivers.roland.mks80;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.tools.ErrorMsgUtil;

public class Mks80ActionTonePatch extends AbstractAction {

	public Mks80ActionTonePatch(Map<Serializable, Integer> mnemonics) {
		super("MKS 80 Tone Patch", null);
		this.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		ErrorMsgUtil.reportError("NOT IMPLEMENTED", "Tone/Patch");
	}
}