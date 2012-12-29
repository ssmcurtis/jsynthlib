package org.jsynthlib.menu.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.MacUtils;

/**
 * A virtual JDesktopPane class which supports both MDI (Multiple Document Interface: using JInternalFrame) and SDI
 * (Single Document Interface: using JFrame) mothods. In MDI mode JDesktopPane is used. In SDI mode a ToolBar window is
 * created and JDesktopPane methods are emulated. For the details of each method, refer the documentation of
 * JDesktopPane.
 * 
 * @see JDesktopPane
 * @see JSLWindowMenu
 * @see JSLFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLDesktop implements JSLFrameListener {
	private JSLDesktopProxy proxy;
	Boolean in_fake_activation = Boolean.FALSE;
	private int frame_count = 0;
	int xdecoration = 0;
	int ydecoration = 0;
	private Action exitAction;
	/** @see #setGUIMode(boolean) */
	private static boolean useMDI = true;
	/** a list of JSLFrames added to the JSLDesktop. */
	protected ArrayList<JSLFrame> windows = new ArrayList<JSLFrame>();
	/** just for efficiency. */
	private static boolean isMac = MacUtils.isMac();
	/** True if we can exit the application */
	private boolean readyToExit = false;

	private Iterator<JSLFrame> iter = null;

	/** Creates a new JSLDesktop. */
	protected JSLDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {

		this.exitAction = exitAction;
		if (useMDI) {
			proxy = new JSLJDesktop(this, title, mb, tb);
		} else {
			proxy = new JSLFakeDesktop(this);
			if (isMac && "true".equals(System.getProperty("apple.laf.useScreenMenuBar")))
				((JSLFakeDesktop) proxy).createInvisibleWindow(mb);
			else
				((JSLFakeDesktop) proxy).createToolBarWindow(title + " Tool Bar", mb, tb);
		}
	}

	/**
	 * Select GUI mode. This method must be called before the first JSLDesktop constructor call. If this method is not
	 * call, MDI is used.
	 * 
	 * @param useMDI
	 *            if true MDI (single window mode) is used, otherwise SDI (multiple window mode) is used.
	 */
	public static void setGUIMode(boolean useMDI) {
		JSLDesktop.useMDI = useMDI;
	}

	/**
	 * @return <code>true</code> in MDI mode, <code>false</code> in SDI mode.
	 */
	public static boolean useMDI() {
		return useMDI;
	}

	/** @see JSLFrame#moveToDefaultLocation() */
	Point getDefaultLocation(Dimension frameSize) {
		int xofs = 0;
		int yofs = 0;
		int xsep = 30;
		int ysep = 30;
		if (!useMDI()) {
			if (isMac) { // no toolbar window
				xofs = 100;
				yofs = 100;
			} else {
				JFrame tb = ((JSLFakeDesktop) proxy).toolbar.getJFrame();
				if (tb.getLocation().getY() < 100) { // Do we need this check?
					xofs = (int) tb.getLocation().getX();
					yofs = (int) (tb.getLocation().getY() + tb.getSize().getHeight());
				}
				ysep = ydecoration;
			}
		}

		Dimension screenSize = getSize();
		int x, xRemain;
		xRemain = (int) (screenSize.getWidth() - frameSize.getWidth() - xofs);
		x = xRemain > 0 ? (xofs + (xsep * frame_count) % xRemain) : xofs;
		if (x + frameSize.getWidth() > screenSize.getWidth()) {
			x = xofs;
			if (x + frameSize.getWidth() > screenSize.getWidth())
				x = 0;
		}
		int y, yRemain;
		yRemain = (int) (screenSize.getHeight() - frameSize.getHeight() - yofs);
		y = yRemain > 0 ? (yofs + (ysep * frame_count) % yRemain) : yofs;
		if (y + frameSize.getHeight() > screenSize.getHeight()) {
			y = yofs;
			if (y + frameSize.getHeight() > screenSize.getHeight())
				y = 0;
		}

		frame_count++;
		return new Point(x, y);
	}

	// JDesktopPane compatible methods
	/** Returns all JInternalFrames currently displayed in the desktop. */
	// TODO use getJSLFrameIterator() instead of this
	public JSLFrame[] getAllFrames() {
		return proxy.getAllJSLFrames();
	}

	/**
	 * Returns the currently active JSLFrame, or last active JSLFrame if no JSLFrame is currently active, or null if any
	 * JSLFrame has never been activated.
	 */
	public JSLFrame getSelectedFrame() {
		return proxy.getSelectedJSLFrame();
	}

	/** Returns the size of this component in the form of a Dimension object. */
	public Dimension getSize() {
		return proxy.getSize();
	}

	// original (non-JDesktopPane compatible) methods
	/**
	 * Returns the current active JFrame. Used for the <code>owner</code> parameter for <code>JDialog</code>
	 * constructor. In MDI mode returns the root JFrame created. In SDI mode returns the current active JFrame (may be
	 * the Toolbar frame).
	 * 
	 * @see #getSelectedWindow()
	 */
	public JFrame getSelectedWindow() {
		return proxy.getSelectedWindow();
	}

	/**
	 * Returns the root JFrame for the <code>owner</code> parameter for <code>JDialog</code> constructor to show a
	 * dialog window in the center of screen. In MDI mode returns the root JFrame created. In SDI mode returns null.
	 * 
	 * @see #getSelectedWindow()
	 */
	public JFrame getRootFrame() {
		return useMDI ? ((JSLJDesktop) proxy).frame : null;
	}

	/** Returns invisible window. Used only in SDI mode for Mac OS. */
	public JFrame getInvisible() {
		return ((JSLFakeDesktop) proxy).invisible.getJFrame();
	}

	/** add a JSLFrame under this JSLDesktop control. */
	public void add(JSLFrame f) {
		if (windows.contains(f)) {
			ErrorMsg.reportStatus("JSLDesktop.add : multiple add() call.");
			return;
		}
		proxy.add(f);
		f.addJSLFrameListener(this);
		windows.add(f);
	}

	/**
	 * @return <code>Iterator</code> of JSLFrame added on the JSLDesktop.
	 */
	public Iterator<JSLFrame> getJSLFrameIterator() {
		return windows.iterator();
	}

	public void JSLFrameActivated(JSLFrameEvent e) {
		proxy.FrameActivated(e.getJSLFrame());
	}

	public void JSLFrameClosing(JSLFrameEvent e) {
		proxy.FrameClosing(e.getJSLFrame());
	}

	public void JSLFrameClosed(JSLFrameEvent e) {
		if (iter != null) {
			try {
				// TODO ssmcuris
				// iter.remove();
			} catch (java.lang.IllegalStateException ex) {
				ex.printStackTrace();
				// nothing
			}
		} else {
			windows.remove(e.getJSLFrame());
		}
		proxy.FrameClosed(e.getJSLFrame());
		if (windows.isEmpty() || (!useMDI && ((JSLFakeDesktop) proxy).toolbar != null && windows.size() == 1))
			frame_count = 0; // reset frame position
	}

	public void JSLFrameDeactivated(JSLFrameEvent e) {
	}

	public void JSLFrameDeiconified(JSLFrameEvent e) {
	}

	public void JSLFrameIconified(JSLFrameEvent e) {
	}

	public void JSLFrameOpened(JSLFrameEvent e) {
	}

	/**
	 * Notification from the UIManager that the L&F has changed. Replaces the current UI object with the latest version
	 * from the UIManager.
	 */
	public void updateLookAndFeel() {
		proxy.updateLookAndFeel();
	}

	public boolean confirmExiting() {
		readyToExit = JOptionPane.showConfirmDialog(PatchBayApplication.getRootFrame(), "Exit JSynthLib?", "Confirmation",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
		return readyToExit;
	}

	public boolean isReadyToExit() {
		return readyToExit;
	}

	public void closingProc() {
		if (confirmExiting()) {
			iter = getJSLFrameIterator();
			while (iter.hasNext()) {
				JSLFrame frame = iter.next();
				try {
					frame.setClosed(true);
				} catch (Exception ex) {
					ErrorMsg.reportStatus(ex);
				}
			}
			iter = null;
			AppConfig.savePrefs();
			System.exit(0);
		}
	}
	
	public void cascade(){
		((JSLJDesktop) proxy).cascade();
	}
	
	public void tiling(){
		((JSLJDesktop) proxy).tiling();
	}

}
