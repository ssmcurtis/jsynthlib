package org.jsynthlib.menu.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jsynthlib.JSynthResource;

/** use JDesktopPane for MDI (Multiple Document Interface) mode. */
class JSLJDesktop extends JDesktopPane implements JSLDesktopProxy {
	/**
	 * 
	 */
	private final JSLDesktop jslDesktop;

	JFrame frame;

	// window size
	private static final int INSET_X = 100;
	private static final int INSET_Y = 100;

	JSLJDesktop(JSLDesktop jslDesktop, String title, JMenuBar mb, JToolBar tb) {
		super();
		this.jslDesktop = jslDesktop;
		frame = new JFrame(title);

		// Emenaker - 2006-02-02
		// TODO: Move the actual filename to some central config location
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(JSynthResource.RESOURCES_PACKAGE.getUri() + "images/JSLIcon48x48.png"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(INSET_X, INSET_Y, 1000, screenSize.height - INSET_Y * 2);

		// Quit this app when the big window closes.
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JSLJDesktop.this.jslDesktop.closingProc();
			}
		});

		Container c = frame.getContentPane();
		if (tb != null) {
			c.add(tb, BorderLayout.NORTH);
			tb.setVisible(true);
		}
		c.add(this, BorderLayout.CENTER);
		if (mb != null)
			frame.setJMenuBar(mb);
		setOpaque(false);
		putClientProperty("JDesktopPane.dragMode", "outline");

		frame.setVisible(true);
	}

	public void add(JSLFrame f) {
		add(f.getJInternalFrame());
	}

	public void updateLookAndFeel() {
		SwingUtilities.updateComponentTreeUI(frame);
		// selected.pack();
	}

	public JFrame getSelectedWindow() {
		return frame;
	}

	public JSLFrame getSelectedJSLFrame() {
		try {
			return ((JSLFrame.JSLFrameProxy) this.getSelectedFrame()).getJSLFrame();
		} catch (NullPointerException e) {
			return null; // This is normal.
		}
	}

	public JSLFrame[] getAllJSLFrames() {
		JInternalFrame[] ifs = this.getAllFrames();
		JSLFrame[] a = new JSLFrame[ifs.length];

		for (int i = 0; i < ifs.length; i++) {
			if (ifs[i] instanceof JSLFrame.JSLFrameProxy) {
				a[i] = ((JSLFrame.JSLFrameProxy) ifs[i]).getJSLFrame();
			}
		}
		return a;
	}

	public void FrameActivated(JSLFrame f) {
	}

	public void FrameClosing(JSLFrame f) {
	}

	public void FrameClosed(JSLFrame f) {
	}

	public void tiling() {
		JInternalFrame[] frames = getAllFrames();
		if (frames.length == 0) {
			return;
		}

		tiling(frames, getBounds());
	}

	public void cascade() {
		JInternalFrame[] frames = getAllFrames();
		if (frames.length == 0) {
			return;
		}

		cascade(frames, getBounds(), 24);
	}

	private void tiling(JInternalFrame[] frames, Rectangle dBounds) {
		int width = dBounds.width / frames.length;
		int height = dBounds.height;

		for (int i = 0; i < frames.length; i++) {
			frames[i].setBounds(i * width, 0, width, height);
		}

	}

	private void cascade(JInternalFrame[] frames, Rectangle dBounds, int separation) {
		int margin = frames.length * separation + separation;
		int width = dBounds.width - margin;
		int height = dBounds.height - margin;
		for (int i = 0; i < frames.length; i++) {
			// frames[i].setBounds(separation + dBounds.x + i * separation, separation + dBounds.y + i * separation,
			// width, height);
			frames[frames.length - i - 1].setBounds(i * separation, i * separation, width, height);
		}
	}

}