package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.tools.ErrorMsg;

public class HomePageAction extends AbstractAction {
	public HomePageAction(Map<Serializable, Integer> mnemonics) {
		super("JSynthLib Home Page", null);
		this.setEnabled(true);
		mnemonics.put(this, new Integer('P'));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (Actions.getHpWin() == null) {
				// www.gnu.org is simple enough for J2SE 1.4.2.
				// hpWin = new DocumentationWindow("text/html",
				// "http://www.gnu.org/");
				// hpWin = new DocumentationWindow("text/html",
				// "http://www.jsynthlib.org/");
				// hpWin.setVisible(true);
			}
		} catch (Exception ex) {
			ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
		}
	}
}