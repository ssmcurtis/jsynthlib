package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.widgets.ComboBoxWidget;
import org.jsynthlib.widgets.ParamModel;
import org.jsynthlib.widgets.SysexSender;

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
