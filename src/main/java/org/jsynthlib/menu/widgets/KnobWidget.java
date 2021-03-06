package org.jsynthlib.menu.widgets;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.model.patch.Patch;

import com.dreamfabric.DKnob;

/**
 * A rotary knob widget. Uses the class DKnob by <a href="http://www.dreamfabric.com/">DreamFabric</a> which is
 * responsible for drawing the widget. This component should rather be used for parameters which do not need fine
 * adjustment.
 * 
 * A tooltip is used to display the current value. A prior version used a text field instead, but the resulting
 * component was inconsistent and diffucult to place.
 * 
 * @version $Id$
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 * @see KnobLookupWidget
 */
public class KnobWidget extends SysexWidget {
	/** display offset */
	private int mBase;
	/** DKnob widget object */
	protected DKnob mKnob = new DKnob();
	/** label widget object */
	protected JLabel mLabelImage;
	// protected JLabel mLabel;
	private ImageIcon[] mImages;

	/**
	 * Creates a new <code>KnobWidget</code> instance.
	 * 
	 * @param patch
	 *            a <code>Patch</code> value.
	 * @param param
	 *            a <code>Parameter</code> value.
	 */
	public KnobWidget(Patch patch, IParameter param) {
		this(patch, param, -1, -1);
	}

	/**
	 * Creates a new <code>KnobWidget</code> instance.
	 * 
	 * @param patch
	 *            a <code>Patch</code> value.
	 * @param param
	 *            a <code>Parameter</code> value.
	 * @param width
	 *            The width of the knob.
	 * @param height
	 *            The height of the knob.
	 */
	public KnobWidget(Patch patch, IParameter param, int width, int height) {
		super(patch, param);

		if (width > 0 && height > 0) {
			mKnob = new DKnob(width, height);
		}

		mBase = 0;
		createWidgets();
		layoutWidgets();
	}

	/**
	 * Creates a new <code>KnobWidget</code> instance.
	 * 
	 * @param label
	 *            label String.
	 * @param patch
	 *            a <code>Patch</code> value.
	 * @param min
	 *            minimum value.
	 * @param max
	 *            maximum value.
	 * @param base
	 *            value display offset.
	 * @param pmodel
	 *            a <code>ParamModel</code> object.
	 * @param sender
	 *            a <code>ISender</code> object.
	 */
	public KnobWidget(String label, Patch patch, int min, int max, int base, IParamModel pmodel, ISender sender) {
		this(label, patch, min, max, base, pmodel, sender, null, -1, -1);
	}

	/**
	 * Creates a new <code>KnobWidget</code> instance.
	 * 
	 * @param label
	 *            label String.
	 * @param patch
	 *            a <code>Patch</code> value.
	 * @param min
	 *            minimum value.
	 * @param max
	 *            maximum value.
	 * @param base
	 *            value display offset.
	 * @param pmodel
	 *            a <code>ParamModel</code> object.
	 * @param sender
	 *            a <code>ISender</code> object.
	 * @param width
	 *            The width of the knob.
	 * @param height
	 *            The height of the knob.
	 */
	public KnobWidget(String label, Patch patch, int min, int max, int base, IParamModel pmodel, ISender sender,
			int width, int height) {
		this(label, patch, min, max, base, pmodel, sender, null, width, height);
	}

	/**
	 * Display an image to the right of the value.
	 * 
	 * @param aImages
	 *            array of images corresponding to each value.
	 */
	public KnobWidget(String label, Patch patch, int min, int max, int base, IParamModel pmodel, ISender sender,
			ImageIcon[] aImages) {
		this(label, patch, min, max, base, pmodel, sender, aImages, -1, -1);
	}

	/**
	 * Display an image to the right of the value.
	 * 
	 * @param aImages
	 *            array of images corresponding to each value.
	 */
	public KnobWidget(String label, Patch patch, int min, int max, int base, IParamModel pmodel, ISender sender,
			ImageIcon[] aImages, int width, int height) {
		super(label, patch, min, max, pmodel, sender);
		mBase = base;
		mImages = aImages;

		if (width > 0 && height > 0) {
			mKnob = new DKnob(width, height);
		}

		createWidgets();
		layoutWidgets();
	}

	/**
	 * Special constructor for derived classes.
	 * 
	 * @deprecated
	 */
	protected KnobWidget(String label, Patch patch, int min, int max, IParamModel pmodel, ISender sender) {
		super(label, patch, min, max, pmodel, sender);

		createWidgets();
		layoutWidgets();
	}

	protected void createWidgets() {
		mKnob.setDragType(DKnob.SIMPLE_MOUSE_DIRECTION);
		if (getLabel() != null) {
			// mLabel = new JLabel(getLabel(), SwingConstants.CENTER);
			getJLabel().setHorizontalAlignment(SwingConstants.CENTER);
		}
		int oValue = getValue();
		mKnob.setToolTipText(Integer.toString(oValue + mBase));
		// positionner la valeur courante
		mKnob.setValue(((float) getValue() - getValueMin()) / (getValueMax() - getValueMin()));

		if (mImages != null) {
			mLabelImage = new JLabel(mImages[oValue]);
		}

		// Add a change listener to the knob
		mKnob.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				eventListener(e);
			}
		});
		// mouse wheel event is supported by J2SE 1.4 and later
		mKnob.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				eventListener(e);
			}
		});
	}

	/** invoked when knob is moved. */
	protected void eventListener(ChangeEvent e) {
		DKnob t = (DKnob) e.getSource();
		int oValue = Math.round(t.getValue() * (getValueMax() - getValueMin())) + getValueMin();
		String oVStr = Integer.toString(oValue + mBase);
		t.setToolTipText(oVStr);
		t.setValueAsString(oVStr);
		if (mLabelImage != null) {
			mLabelImage.setIcon(mImages[oValue]);
		}
		sendSysex(oValue);
	}

	/** invoked when mouse wheel is moved. */
	protected void eventListener(MouseWheelEvent e) {
		DKnob t = (DKnob) e.getSource();
		if (t.hasFocus()) // to make consistent with other operation.
			t.setValue(t.getValue() - (e.getWheelRotation() / (float) (getValueMax() - getValueMin())));
	}

	/** Adds a <code>ChangeListener</code> to the Knob. */
	public void addEventListener(ChangeListener l) {
		mKnob.addChangeListener(l);
	}

	/** Adds a <code>MouseWheelListener</code> to the Knob. */
	public void addEventListener(MouseWheelListener l) {
		mKnob.addMouseWheelListener(l);
	}

	protected void layoutWidgets() {
		// int oWidthOff;
		if (mImages != null) {
			JPanel oPane = new JPanel(new BorderLayout(0, 0));
			oPane.add(mKnob, BorderLayout.NORTH);
			if (getLabel() != null) {
				// oPane.add(mLabel, BorderLayout.SOUTH);
				oPane.add(getJLabel(), BorderLayout.SOUTH);
			}
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			add(oPane);
			add(mLabelImage);
			// oWidthOff = 100;
		} else {
			setLayout(new BorderLayout(0, 0));
			// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(mKnob, BorderLayout.NORTH);
			if (getLabel() != null) {
				// add(mLabel, BorderLayout.SOUTH);
				add(getJLabel(), BorderLayout.SOUTH);
			}
			// oWidthOff = 0;
		}
		// setMaximumSize(new Dimension(120+oWidthOff, 80));
	}

	public void setValue(int v) {
		super.setValue(v);
		String oVStr = Integer.toString(v + mBase);
		mKnob.setToolTipText(oVStr);
		mKnob.setValueAsString(oVStr);
		if (mLabelImage != null) {
			mLabelImage.setIcon(mImages[v]);
		}
		mKnob.setValue(((float) v - getValueMin()) / (getValueMax() - getValueMin()));
	}

	public void setEnabled(boolean e) {
		mKnob.setEnabled(e);
		getJLabel().setEnabled(e);
	}
}
