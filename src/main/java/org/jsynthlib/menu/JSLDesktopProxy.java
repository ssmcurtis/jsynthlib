package org.jsynthlib.menu;

import java.awt.Dimension;

import javax.swing.JFrame;

interface JSLDesktopProxy {
	JFrame getSelectedWindow();

	JSLFrame[] getAllJSLFrames();

	JSLFrame getSelectedJSLFrame();

	Dimension getSize();

	void add(JSLFrame f);

	void updateLookAndFeel();

	void FrameActivated(JSLFrame f);

	void FrameClosing(JSLFrame f);

	void FrameClosed(JSLFrame f);
}