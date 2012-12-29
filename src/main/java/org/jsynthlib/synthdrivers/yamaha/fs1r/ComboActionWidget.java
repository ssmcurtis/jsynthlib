package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jsynthlib.menu.patch.ParamModel;
import org.jsynthlib.menu.patch.Patch;
import org.jsynthlib.widgets.ComboBoxWidget;
import org.jsynthlib.widgets.SysexSender;

public class ComboActionWidget extends ComboBoxWidget {
	protected ComboActionListener mListener;

	public ComboActionWidget(String l, Patch p, ParamModel ofs, SysexSender s, String[] o,
			ComboActionListener aListener) {
		super(l, p, ofs, s, o);
		mListener = aListener;
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mListener.notifyChange(cb.getSelectedIndex());
			}
		});
	}

}
