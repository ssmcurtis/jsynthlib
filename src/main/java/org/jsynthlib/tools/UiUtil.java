package org.jsynthlib.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.window.BankEditorFrame;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.menu.window.PatchEditorFrame;

/**
 * Various utility functions.
 * 
 * @author phil@muqus.com - 07/2001
 * @author Hiroo Hayashi
 * @version $Id$
 */
public class UiUtil extends Object {

	// don't have to call constructor for Utility class.
	private UiUtil() {
	}

	/**
	 * Place a JDialog window to the center of computer screen.
	 */
	public static void centerDialog(Window dialog) {
		Dimension screenSize = dialog.getToolkit().getScreenSize();
		Dimension size = dialog.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		dialog.setLocation(x, y);
	}

	// moved from AppConfig.java
	/** Returns the "os.name" system property. */
	// - emenaker 2003.03.13
	public static String getOSName() {
		return (getSystemProperty("os.name"));
	}

	/** Returns the "os.version" system property. */
	public static String getOSVersion() {
		return (getSystemProperty("os.version"));
	}

	/** Returns the "java.specification.version" system property. */
	// - emenaker 2003.03.13
	public static String getJavaSpecVersion() {
		return (getSystemProperty("java.specification.version"));
	}

	/** Returns the "java.version" system property. */
	public static String getJavaVersion() {
		return (getSystemProperty("java.version"));
	}

	/** Looks up a system property and returns "" on exceptions. */
	private static String getSystemProperty(String key) {
		try {
			return (System.getProperty(key));
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
			return ("");
		}
	}

	/**
	 * Revalidate Library. Internally this calls <code>revalidateDriver()</code> method of each frame.
	 */
	public static void revalidateLibraries() {
		JSLFrame[] jList = PatchBayApplication.getDesktop().getAllFrames();
		if (jList.length > 0) {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < jList.length; i++) {
				if (jList[i] instanceof LibraryFrame)
					((LibraryFrame) (jList[i])).revalidateDrivers();
				else if (jList[i] instanceof BankEditorFrame)
					((BankEditorFrame) (jList[i])).revalidateDriver();
				else if (jList[i] instanceof PatchEditorFrame)
					((PatchEditorFrame) (jList[i])).revalidateDriver();
			}
			PatchBayApplication.hideWaitDialog();
		}
	}

	/**
	 * @param component
	 *            Any AWT component.
	 * @return component's containing Frame, or null if none found.
	 */
	public static Frame getFrame(Component component) {
		while (component != null && !(component instanceof Frame))
			component = component.getParent();
		return (Frame) component;
	}

	public static void showDetailsViewAsDefault(Component[] comp) {
		for (int i = 0; i < comp.length; i++) {
			if (comp[i] instanceof JToggleButton) {
				if (((JToggleButton) comp[i]).getIcon().equals(UIManager.getIcon("FileChooser.detailsViewIcon"))) {
					((JToggleButton) comp[i]).doClick();
					return;
				}
			}
			if (comp[i] instanceof Container)
				showDetailsViewAsDefault(((Container) comp[i]).getComponents());
		}
	}

} // End Class: Utility
