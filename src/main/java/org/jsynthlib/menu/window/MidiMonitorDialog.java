package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.JSLDialog;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.MidiUtil;
import org.jsynthlib.tools.UiUtil;

public class MidiMonitorDialog extends JSLDialog {
	private final MyEditorPane jt;

	public MidiMonitorDialog() {
		super(PatchBayApplication.getRootFrame(), "JSynthLib Midi Monitor", false);
		setModal(false);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		jt = new MyEditorPane();
		JScrollPane pane = new JScrollPane();
		pane.getViewport().add(jt);

		getContentPane().add(pane, BorderLayout.CENTER);
		pane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jt.repaint();
			}
		});
		try {
			// jt.setContentType("text/html");
			// FileInputStream in = new FileInputStream("documentation.html");
			// jt.read(in,(new HTMLEditorKit()).createDefaultDocument());//new
			// HTMLDocument());
			jt.setCaretPosition(0);
			jt.setEditable(false);
			jt.setFont(new Font("monospaced", Font.PLAIN, 12));

			// create an own panel for "clear" and "close" buttons
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());

			JButton ok = new JButton("Close");
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OKPressed();
				}
			});
			buttonPanel.add(ok, BorderLayout.EAST);

			JCheckBox csm = new JCheckBox("Complete SysexMessages?", MidiUtil.getCSM());
			csm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MidiUtil.toggleCSM();
				}
			});
			buttonPanel.add(csm, BorderLayout.CENTER);

			JButton clr = new JButton("Clear");
			clr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jt.setText("");
				}
			});
			buttonPanel.add(clr, BorderLayout.WEST);

			JButton copy = new JButton("Copy");
			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					StringSelection selection = new StringSelection(jt.getText());
					clipboard.setContents(selection, null);
				}
			});
			buttonPanel.add(copy, BorderLayout.NORTH);

			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			getRootPane().setDefaultButton(ok);
			setSize(500, 400);

			// pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
			UiUtil.centerDialog(this);
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error opening Monitor", e);
		}

	}

	void OKPressed() {
		this.setVisible(false);
	}

	class MyEditorPane extends JEditorPane {
		public void myScrollToReference(String s) {
			super.scrollToReference(s.substring(1));
		}
	}

	public void log(String s) {
		// move the selection at the end of text
		jt.select(Integer.MAX_VALUE, Integer.MAX_VALUE);
		jt.setEditable(true);
		jt.replaceSelection(s);
		jt.setEditable(false);
	}

	void log(int port, boolean in, byte[] sysex, int length) {
		log("Port: " + port + (in ? " RECV " : " XMIT ") + length + " bytes :\n" + hexDump(sysex, length) + "\n");
	}

	String hexDump(byte[] data, int length) {
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < length; i++) {
			String sHex = Integer.toHexString((int) (data[i] & 0xFF));
			s.append(((sHex.length() == 1) ? "0" : "") + sHex + " ");
			s.append((i % 20 == 19) ? "\n" : "");
		}
		return s.toString();
	}

}
