package org.jsynthlib.menu.widgets;

import org.jsynthlib.model.patch.Patch;

public interface IParameter {
	public String getName();

	/* For numeric parameters */
	public int getMin();

	public int getMax();

	public int get(Patch p);

	public void set(Patch p, int val);

	/* For list parameters */
	public String[] getValues();

	/* For String parameters */
	public String getString(Patch p);

	public int getLength();

	public void set(Patch p, String stringval);

	public void send(Patch p);
}