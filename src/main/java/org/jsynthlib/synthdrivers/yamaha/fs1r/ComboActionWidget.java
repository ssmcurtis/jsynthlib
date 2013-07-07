package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jsynthlib.menu.widgets.ComboBoxWidget;
import org.jsynthlib.menu.widgets.ParamModel;
import org.jsynthlib.menu.widgets.SysexSender;
import org.jsynthlib.model.patch.PatchDataImpl;

public class ComboActionWidget extends ComboBoxWidget {
	protected ComboActionListener mListener;

	public ComboActionWidget(String l, PatchDataImpl p, ParamModel ofs, SysexSender s, String[] o,
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
