package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.MidiMonitorDialog;
import org.jsynthlib.tools.ErrorMsgUtil;

public class MonitorAction extends AbstractAction {
	public MonitorAction(Map<Serializable, Integer> mnemonics) {
		super("MIDI Monitor", null);
		this.setEnabled(true);
		mnemonics.put(this, new Integer('M'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getMidiMonitor() == null)
				Actions.setMidiMonitor(new MidiMonitorDialog());
			Actions.getMidiMonitor().setVisible(true);
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Unable to show MIDI Monitor)", ex);
		}
	}
}