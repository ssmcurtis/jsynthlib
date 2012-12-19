package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.ui.window.SceneFrame;

public class NewSceneAction extends AbstractAction {
	public NewSceneAction(Map<Serializable, Integer> mnemonics) {
		super("New Scene", null);
	}

	public void actionPerformed(ActionEvent e) {
		Actions.addLibraryFrame(new SceneFrame());
	}
}