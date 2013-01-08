package org.jsynthlib.menu.window;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.MidiUtil;
import org.jsynthlib.tools.UiUtil;

public class StoreLibraryWindow {
	private JFrame f;
	private JTextArea ta;
	private JScrollPane sbrText;
	private JButton btnQuit;
	private JButton btnProcess;
	private JPanel buttons = new JPanel(new GridLayout(2, 1));

	private Map<SynthDriverPatchImpl, Integer> supportedDevices = new HashMap<SynthDriverPatchImpl, Integer>();

	public StoreLibraryWindow() {
		f = new JFrame("Store non-generic patches from library (max. 16 Patches for device)");
		f.getContentPane().setLayout(new FlowLayout());

		ta = new JTextArea("", 20, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		btnProcess = new JButton("Store");
		buttons.add(btnProcess);
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < AppConfig.deviceCount(); i++) {
					Device device = AppConfig.getDevice(i);

					for (int j = 0; j < device.driverCount(); j++) {
						System.out.println(device.driverCount() + " " + device.getDriver(j).getClass().getSimpleName());

						if (device.getDriver(j) instanceof SynthDriverPatchImpl) {
							SynthDriverPatchImpl driver = (SynthDriverPatchImpl) device.getDriver(j);
							if (driver.isUseableForLibrary()) {
								supportedDevices.put(driver, 0);
							}
						}
					}
				}

				ArrayList<Patch> patches = Actions.getSelectedFrame().getPatchCollection();

				for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

					SynthDriverPatchImpl driver = entry.getKey();

					for (Patch p : patches) {
						if (entry.getValue() < 16) {

							if (p instanceof PatchDataImpl) {
								PatchDataImpl pp = (PatchDataImpl) p;
								if (driver.getDevice().equals(pp.getDevice()) && driver.supportsPatch(p.getPatchHeader(), p.getByteArray())) {
									supportedDevices.put(driver, (entry.getValue()));
									if (driver.isBankDriver()) {
										((SynthDriverBank) driver).putPatch(null, pp, entry.getValue());
									} else {
										driver.storePatch(pp, 0, entry.getValue());
									}
									entry.setValue(entry.getValue() + 1);
									if (p.getName().isEmpty() || p.getName().equals("-")) {
										appendText(driver.toString() + " Pos: " + entry.getValue() + ": " + p.getFileName());
									} else {
										appendText(driver.toString() + " Pos: " + entry.getValue() + ": " + p.getName());
									}

								}
							}
						}
					}
				}

				for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

					SynthDriverPatchImpl driver = entry.getKey();

					if (driver.isBankDriver()) {
						((SynthDriverBank) driver).storePatch(null, 0, 0);
					}
				}

			}
		});

		btnQuit = new JButton("Quit");
		buttons.add(btnQuit);
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.setVisible(false);
			}
		});

	}

	public void launchFrame() {
		f.getContentPane().add(sbrText);
		f.getContentPane().add(buttons);

		f.pack();
		UiUtil.centerDialog(f);
		f.setVisible(true);
	}

	public void appendText(String textToAppend) {
		ta.append(textToAppend + "\n");
		ta.setCaretPosition(ta.getText().length() - 1);
	}
}