package org.jsynthlib.menu.ui.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.action.MenuDesktop;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.JSLDesktop;
import org.jsynthlib.menu.ui.JSLFrame;

/** JSLDesktop with Menu support. */
public class MenuFrame extends JSLFrame {
	private MenuDesktop desktop;

	public MenuFrame(MenuDesktop desktop, String title, boolean addToolBar, boolean resizable, boolean closable,
			boolean maximizable, boolean iconifiable) {
		super(desktop, title, resizable, closable, maximizable, iconifiable);

		this.desktop = desktop;
		if (addToolBar) {
			JToolBar tb = Actions.createToolBar();
			getContentPane().add(tb, BorderLayout.NORTH);
			tb.setVisible(true);
		}

		if (!JSLDesktop.useMDI()) {
			JMenuBar mb = Actions.createMenuBar();
			setJMenuBar(mb);
			Actions.frames.put(this, mb);
		}
	}

	/**
	 * create a resizable, closable, maximizable, and iconifiable frame with toolbar.
	 */
	public MenuFrame(MenuDesktop desktop, String title) {
		this(desktop, title, AppConfig.getToolBar(), true, true, true, true);
	}

	public void setVisible(boolean b) {
		if (Actions.isMac && !JSLDesktop.useMDI()) {
			JFrame frame = getJFrame();
			if (b) {
				if (frame.getJMenuBar() == null)
					setJMenuBar(Actions.frames.get(this) == null ? Actions.createMenuBar() : (JMenuBar) Actions.frames.get(this));
			} else {
				// Remove menubar and change focus once so frame can be
				// disposed.
				// http://lists.apple.com/archives/java-dev/2003/Dec/msg00122.html
				// Java 1.4.2 still has this bug.
				setJMenuBar(null);
				desktop.getInvisible().requestFocus();
				frame.requestFocus();
			}
		}
		super.setVisible(b);
	}
}