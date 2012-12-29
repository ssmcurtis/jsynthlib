package org.jsynthlib.menu.ui.window;

// import core.PatchEditorFrame;
// import java.awt.*;
// import synthdrivers.AlesisSR16.DataModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.sound.midi.SysexMessage;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jsynthlib.example.style.FormatedString;
import org.jsynthlib.example.style.XMEditor;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.tools.HexDumpUtility;

/**
 * This class serves as a initial substitute for a patch editor. It is to be used for verifying that your driver is
 * properly getting and decoding the sysex message from the device (and that the bytes in the sysex message are what you
 * expect them to be).
 * 
 * To use it, add this to your driver:
 * 
 * protected JSLFrame editPatch(Patch p) { return (new synthdrivers.Generic.HexDumpEditorFrame(p)); }
 * 
 * and then you can use the "Edit" menu option on the patch in your library to inspect the sysex message.
 */
public class HexDumpEditorHighlighted extends PatchEditorFrame {

	static final int bytesperline = 16;
	private XMEditor editor;
	private Font monospaced = new Font("Courier", Font.PLAIN, 12);
	protected String newline = "\n";

	/*
	 * public HexDumpEditorFrame(DataModel dm) { this(dm.getDecodedBytes()); }
	 */
	public HexDumpEditorHighlighted(Patch p) {
		super("Hex dump of sysex message patch " + p.getFileName(), new Patch(new byte[] { (byte) 0xF0, (byte) 0xF7 }));

		editor = new XMEditor();
		editor.setEditable(false);
		editor.setFont(monospaced);

		JScrollPane sp = new JScrollPane(editor);
		sp.setPreferredSize(new Dimension(700, 500));

		sp.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(sp, BorderLayout.CENTER);
		this.getContentPane().add(editor.statusPane, BorderLayout.PAGE_END);

		this.pack();
		this.setVisible(true);

		SysexMessage[] messages = p.getMessages();

		SimpleAttributeSet headerAttribute = new SimpleAttributeSet();
		StyleConstants.setUnderline(headerAttribute, true);
		int counter = 0;
		int minSize = Integer.MAX_VALUE;
		int maxSize = 0;
		for (int i = 0; i < messages.length; i++) {

			try {
				editor.getDoc().insertString(editor.getDoc().getLength(), "Message " + i + ":\n", headerAttribute);
				List<FormatedString> output = HexDumpUtility.hexDumpFormated(messages[i].getMessage(), 0, -1, bytesperline, true, true);
				for (FormatedString item : output) {
					editor.getDoc().insertString(editor.getDoc().getLength(), item.getText(), item.getAttributeSet());
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			int size = messages[i].getMessage().length;
			minSize = minSize > size ? size : minSize;
			maxSize = maxSize < size ? size : minSize;
			counter++;
		}
		if (minSize == maxSize) {
			editor.getStatusPane().add(new JLabel("Count: " + counter + " Size: " + minSize + " Byte"));
		} else {
			editor.getStatusPane().add(new JLabel("Count: " + counter + " Size: " + minSize + ".." + maxSize + " Byte"));
		}
	}
}
