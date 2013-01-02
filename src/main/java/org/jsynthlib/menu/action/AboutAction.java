package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.jsynthlib.JSynthConstants;

// //////////////////////////////////////////////////////////////////////
/*
 * Now we start with the various action classes. Each of these performs one of the menu commands and are called
 * either from the menubar, popup menu or toolbar.
 */
public class AboutAction extends AbstractAction {
	public AboutAction(Map<Serializable, Integer> mnemonics) {
		super("About");
		mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "JSynthLib2 Version " + JSynthConstants.VERSION + "." + JSynthConstants.VERSION_2
				+ "\nCopyright (C) 2012-13 ssmCurtis" + "\n\nJSynthLib Version " + JSynthConstants.VERSION
				+ "\nCopyright (C) 2000-04 Brian Klock et al." + "\n\nSee Help->License for more information.",
				"About JSynthLib2", JOptionPane.INFORMATION_MESSAGE);
		return;
	}
}