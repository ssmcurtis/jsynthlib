package org.jsynthlib.example.style;

import javax.swing.text.SimpleAttributeSet;

public class FormatedString {
	private String text = "";
	private SimpleAttributeSet attributeSet = new SimpleAttributeSet();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public SimpleAttributeSet getAttributeSet() {
		return attributeSet;
	}

	public void setAttributeSet(SimpleAttributeSet attributeSet) {
		this.attributeSet = attributeSet;
	}

}
