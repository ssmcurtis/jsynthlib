package org.jsynthlib.menu.ui.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jsynthlib.menu.PatchBayApplication;
import org.jsynthlib.menu.ui.JSLDialog;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.Utility;

@SuppressWarnings("serial")
public class DocumentationWindow extends JSLDialog {
	/**
	 * Open a viewer window for a document.
	 * 
	 * @param contentType
	 *            Content type of the document.
	 * @param url
	 *            URL of the document.
	 * @see JEditorPane
	 */
	public DocumentationWindow(String contentType, URL url) {
		super(PatchBayApplication.getRootFrame(), "JSynthLib Documentation Viewer", false);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		final MyEditorPane editorPane = new MyEditorPane();

		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if ((e.getDescription()).startsWith("#")) {
						// Link of the same file
						try {
							editorPane.myScrollToReference(e.getDescription());
						} catch (Exception e2) {
							ErrorMsg.reportError("Error", e.getDescription().toString(), e2);
						}
					} else {
						// Link to other file
						try {
							editorPane.setPage(e.getURL());
						} catch (UnknownHostException uhe) {
							JOptionPane.showMessageDialog(getContentPane(), "Unknown Host \"" + e.getURL()
									+ "\". Maybe you're not online.");
						} catch (Exception e3) {
							ErrorMsg.reportError("Error", e.getURL().toString(), e3);
						}
					}
				}
			}
		});
		JScrollPane pane = new JScrollPane();
		pane.getViewport().add(editorPane);

		getContentPane().add(pane, BorderLayout.CENTER);
		pane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				editorPane.repaint();
			}
		});
		try {
			editorPane.setContentType(contentType);
			editorPane.setPage(url);
		} catch (java.net.MalformedURLException e) {
			ErrorMsg.reportError("Error", "Wrong URL", e);
		} catch (java.io.IOException e) {
			ErrorMsg.reportError("Error", "Error opening documentation", e);
		}

		editorPane.setCaretPosition(0);
		editorPane.setEditable(false);

		JButton ok = new JButton("Close");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OKPressed();
			}
		});
		getContentPane().add(ok, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(ok);
		setSize(800, 600);

		// pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
		Utility.centerDialog(this);
	}

	void OKPressed() {
		this.setVisible(false);
	}

	class MyEditorPane extends JEditorPane {
		public void myScrollToReference(String s) {
			super.scrollToReference(s.substring(1));
		}
	}
}
