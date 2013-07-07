/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.roland.mks80;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import org.jsynthlib.advanced.style.XMEditor;
import org.jsynthlib.menu.widgets.CheckBoxWidget;
import org.jsynthlib.menu.widgets.ComboBoxWidget;
import org.jsynthlib.menu.widgets.EnvelopeWidget;
import org.jsynthlib.menu.widgets.LabelWidget;
import org.jsynthlib.menu.widgets.ParamModel;
import org.jsynthlib.menu.widgets.PatchNameWidget;
import org.jsynthlib.menu.widgets.ScrollBarLookupWidget;
import org.jsynthlib.menu.widgets.ScrollBarWidget;
import org.jsynthlib.menu.widgets.SysexSender;
import org.jsynthlib.menu.window.PatchEditorFrame;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.synthdrivers.roland.mks80.model.BankNumberForToneInPatch;
import org.jsynthlib.tools.HexaUtil;

@SuppressWarnings("unused")
class Mks80TonePatchSingleEditor extends PatchEditorFrame {
	private Font monospaced = new Font("Courier", Font.PLAIN, 12);

	public Mks80TonePatchSingleEditor(PatchDataImpl patch) {
		super("MKS 80 (Tone)Patch Editor " + patch.getComment(), patch);

		gbc.weightx = 1;

		JPanel tonePane = new JPanel();
		tonePane.setLayout(new GridBagLayout());
		tonePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Tone", TitledBorder.CENTER, TitledBorder.CENTER));

		// ScrollBarWidget sbw = new ScrollBarWidget("LFO Speed", patch, 0, 99, 0, new ParamModel(patch, 1), null);
		// addWidget(tonePane, sbw, 0, 0, 7, 1, 20);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		XMEditor editor = new XMEditor();
		editor.setEditable(false);
		editor.setFont(monospaced);
		SimpleAttributeSet headerAttribute = new SimpleAttributeSet();
		try {
			editor.getDoc().insertString(editor.getDoc().getLength(), "Tone\n", headerAttribute);
			editor.getDoc().insertString(editor.getDoc().getLength(),
					HexaUtil.hexDump(patch.getByteArray(), 0, Mks80.TONE_SIZE_IN_BANK, 16), headerAttribute);
			editor.getDoc().insertString(editor.getDoc().getLength(), "\nPatch\n", headerAttribute);
			editor.getDoc().insertString(editor.getDoc().getLength(),
					HexaUtil.hexDump(patch.getByteArray(), Mks80.TONE_SIZE_IN_BANK, Mks80.PATCH_SIZE_IN_BANK, 16), headerAttribute);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		tonePane.add(editor, gbc);
		scrollPane.add(tonePane, gbc);

		JPanel patchUpperPane = new JPanel();
		patchUpperPane.setLayout(new GridBagLayout());
		patchUpperPane
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Tones", TitledBorder.CENTER, TitledBorder.CENTER));

		addWidget(patchUpperPane, new LabelWidget("Upper"), 0, 0, 1, 1, 2);
		ComboBoxWidget upperBank = new ComboBoxWidget("Bank", patch, new BankNumberForToneInPatch(patch, 42, true), null,
				Mks80.getBankNumbers());
		upperBank.setPreferredSize(new Dimension(150, 35));
		addWidget(patchUpperPane, upperBank, 1, 0, 1, 1, 2);

		upperBank = new ComboBoxWidget("Patch", patch, new BankNumberForToneInPatch(patch, 42, false), null, Mks80.getPatchNumbersInBank());
		upperBank.setPreferredSize(new Dimension(100, 30));
		addWidget(patchUpperPane, upperBank, 2, 0, 1, 1, 2);

		addWidget(patchUpperPane, new LabelWidget("Lower"), 0, 1, 1, 1, 2);
		upperBank = new ComboBoxWidget("Bank", patch, new BankNumberForToneInPatch(patch, 52, true), null, Mks80.getBankNumbers());
		upperBank.setPreferredSize(new Dimension(150, 35));
		upperBank.setEnabled(!Mks80TonePatch.isPatchWhole(patch));
		addWidget(patchUpperPane, upperBank, 1, 1, 1, 1, 2);

		upperBank = new ComboBoxWidget("Patch", patch, new BankNumberForToneInPatch(patch, 52, false), null, Mks80.getPatchNumbersInBank());
		upperBank.setEnabled(!Mks80TonePatch.isPatchWhole(patch));
		upperBank.setPreferredSize(new Dimension(100, 30));
		addWidget(patchUpperPane, upperBank, 2, 1, 1, 1, 2);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		scrollPane.add(patchUpperPane, gbc);

		pack();
	}

}
