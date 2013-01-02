package org.jsynthlib.menu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jsynthlib.tools.ErrorMsgUtil;

/** fake desktop for SDI (Single Document Interface) mode. */
class JSLFakeDesktop implements JSLDesktopProxy {
	/**
	 * 
	 */
	private final JSLDesktop jslDesktop;
	protected JSLFrame toolbar;
	/** invisible frame to keep menus when no open windows on MacOSX. */
	JSLFrame invisible = null;
	private JSLFrame selected = null;
	/** last selected (activated) frame except toolbar nor invisible frame. */
	private JSLFrame last_selected = null;

	JSLFakeDesktop(JSLDesktop jslDesktop) {
		this.jslDesktop = jslDesktop;
	}

	/** Create invisible window to keep menus when no open windows */
	void createInvisibleWindow(JMenuBar mb) {
		invisible = new JSLFrame(this.jslDesktop);
		JFrame frame = invisible.getJFrame();
		frame.setTitle("Please enable ScreenMenuBar.");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (mb != null)
			frame.setJMenuBar(mb);
		frame.setSize(0, 0);
		frame.setUndecorated(true);
		// frame(0,0x7FFFFFFF);
		frame.pack();
		frame.setVisible(true);
		// frame.addWindowListener(this);
		selected = invisible;
	}

	/** create a toolbar window */
	void createToolBarWindow(String title, JMenuBar mb, JToolBar tb) {
		toolbar = new JSLFrame(this.jslDesktop);
		JFrame frame = toolbar.getJFrame();
		toolbar.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// toolbar.addJSLFrameListener(this);
		if (mb != null)
			toolbar.setJMenuBar(mb);
		tb.setFloatable(false);
		toolbar.getContentPane().add(tb);
		toolbar.pack();

		Dimension gs = frame.getGlassPane().getSize();
		Dimension ts = frame.getSize();
		this.jslDesktop.xdecoration = (int) (ts.getWidth() - gs.getWidth());
		this.jslDesktop.ydecoration = (int) (ts.getHeight() - gs.getHeight());
		toolbar.setLocation(this.jslDesktop.xdecoration / 2, this.jslDesktop.ydecoration);

		this.jslDesktop.add(toolbar);
		toolbar.setVisible(true);
	}

	public void add(JSLFrame f) {
	}

	public void updateLookAndFeel() {
		// update toolbar
		if (toolbar != null) {
			SwingUtilities.updateComponentTreeUI(toolbar.getJFrame());
			toolbar.pack();
		}
		;
		// update each Frame
		Iterator<JSLFrame> it = this.jslDesktop.windows.iterator();
		while (it.hasNext()) {
			JFrame frame = ((JSLFrame) it.next()).getJFrame();
			SwingUtilities.updateComponentTreeUI(frame);
			frame.pack();
		}
	}

	public Dimension getSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public JFrame getSelectedWindow() {
		return selected.getJFrame();
	}

	public JSLFrame getSelectedJSLFrame() {
		return last_selected;
	}

	public JSLFrame[] getAllJSLFrames() {
		JSLFrame[] a = new JSLFrame[this.jslDesktop.windows.size()];
		Iterator<JSLFrame> it = this.jslDesktop.windows.iterator();
		for (int i = 0; it.hasNext(); i++) {
			Object o = it.next();
			if (o instanceof JSLFrame) {
				a[i] = (JSLFrame) o;
			}
		}
		return a;
	}

	// JSLFrameListener methods : called for both toolbar and JSLFrame
	private void showState(JSLFrame f, String s) {
		ErrorMsgUtil.reportStatus(ErrorMsgUtil.FRAME, "\"" + f.getTitle() + "\" " + s);
	}

	public void FrameActivated(JSLFrame f) {
		if (f == toolbar) {
			synchronized (this.jslDesktop.in_fake_activation) {
				if (this.jslDesktop.in_fake_activation.booleanValue())
					return;
				// When toolbar is activated, activate the last selected
				// frame if it is not iconified nor closing.
				if (last_selected != null
				// && last_selected != toolbar
						&& !last_selected.isIcon() && !last_selected.isClosing()) {
					showState(last_selected, "FakeActivated");
					this.jslDesktop.in_fake_activation = Boolean.TRUE;
					((JSLFrame.JSLJFrame) last_selected.getJFrame()).fakeActivate();
					this.jslDesktop.in_fake_activation = Boolean.FALSE;
				}
			}
		} else if (f != invisible)
			last_selected = f;

		selected = f;
		showState(f, "selected : " + selected);
	}

	public void FrameClosing(JSLFrame f) {
		// if (f == toolbar && confirmExiting())
		// exitAction.actionPerformed(null);
	}

	public void FrameClosed(JSLFrame f) {
		showState(f, "closed. " + (this.jslDesktop.windows.size() - 1) + " windows still open.");
		if (last_selected == f)
			last_selected = null;
	}
}