package org.jsynthlib.menu.window;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.model.tablemodel.PatchTableModel;
import org.jsynthlib.tools.UiUtil;

import org.jsynthlib.tools.ErrorMsgUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class StoreLibraryWindow {
	private JFrame f;
	private JTextArea ta;
	private JScrollPane sbrText;
	private JButton btnQuit;
	private JButton btnCopy;
	private JButton btnProcess;
	private JPanel buttons = new JPanel(new GridLayout(3, 1));

	private Map<SynthDriverPatchImpl, Integer> supportedDevices;

	private List<String> devices = new ArrayList<>();

	public StoreLibraryWindow() {
		f = new JFrame("Store non-generic patches from library");
		f.getContentPane().setLayout(new FlowLayout());

		ta = new JTextArea("", 20, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		btnProcess = new JButton("Store");
		buttons.add(btnProcess);

		supportedDevices = new HashMap<SynthDriverPatchImpl, Integer>();
		for (int i = 0; i < AppConfig.deviceCount(); i++) {
			Device device = AppConfig.getDevice(i);

			for (int j = 0; j < device.driverCount(); j++) {
				// ErrorMsgUtil.reportStatus(device.driverCount() + " " +
				// device.getDriver(j).getClass().getSimpleName());

				if (device.getDriver(j) instanceof SynthDriverPatchImpl) {
					SynthDriverPatchImpl driver = (SynthDriverPatchImpl) device.getDriver(j);
					if (driver.isUseForStoreLibrary()) {
						String keyString = driver.getDevice().getManufacturerName() + " " + driver.getDevice().getModelName() + " ("
								+ driver.getClass().getSimpleName() + ")";

						if (!devices.contains(keyString)) {
							appendText(keyString);
							devices.add(keyString);
							supportedDevices.put(driver, device.getMaxPatchesForLibraryStorage());
						}
					}
				}
			}
		}
		appendText("");

		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// appendText("Supported devices:");
				btnQuit.setEnabled(false);
				btnCopy.setEnabled(false);

				Thread worker = new Thread() {

					public void run() {

						if (!(PatchBayApplication.getDesktop().getSelectedFrame() instanceof LibraryFrame)) {
							throw new NotImplementedException();
						}

						LibraryFrame libraryFrame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();
						JTable table = libraryFrame.getTable();
						PatchTableModel pm = (PatchTableModel) libraryFrame.getTable().getModel();

						int maxPatchesinTable = libraryFrame.getPatchCollection().size();

						for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

							SynthDriverPatchImpl driver = entry.getKey();

							int maxPatchesForThisDevice = entry.getValue();

							for (int i = 0; i < maxPatchesinTable; i++) {

								Patch p = pm.getPatchAt(table.convertRowIndexToModel(i));

								if (entry.getValue() > 0) {

									if (p instanceof PatchDataImpl) {
										// ErrorMsgUtil.reportStatus(p.getClass().getSimpleName());

										PatchDataImpl patchToSend = (PatchDataImpl) p;

										boolean sameDevice = driver.getDevice().equals(patchToSend.getDevice());
										boolean patchSupported = driver.supportsPatchSingle(p.getPatchHeader(), p.getByteArray());

										// ErrorMsgUtil.reportStatus(driver.getDevice() + " " + patchToSend.getDevice());
										// ErrorMsgUtil.reportStatus("Device " + sameDevice + " Patch " + patchSupported);

										if (sameDevice && patchSupported) {

											// supportedDevices.put(driver, (entry.getValue()));
											int pos = maxPatchesForThisDevice - entry.getValue();
											// ErrorMsgUtil.reportStatus("Pos: " + (pos + 1) + " " + p.getComment());

											String keyString = driver.getDevice().getManufacturerName() + " "
													+ driver.getDevice().getModelName();
											if (p.getName().trim().isEmpty() || p.getName().equals("-")) {
												appendText(keyString + " Pos: " + (pos + 1) + ": " + p.getFileName().trim()
														+ p.getComment());
											} else {
												appendText(keyString + " Pos: " + (pos + 1) + ": " + p.getName().trim() + " "
														+ p.getComment());
											}

											if (driver.isBankDriver()) {
												((SynthDriverBank) driver).addToCurrentBank(patchToSend, pos);
											} else {
												driver.storePatch(patchToSend, 0, pos);
											}

											entry.setValue(entry.getValue() - 1);

										}
									} else {
										ErrorMsgUtil.reportStatus(">> wrong instancetype .. " + p.getClass().getSimpleName());
									}
								}
							}

							// TODO ssmCurtis - change to 0 / 0
							if (!driver.isBankDriver()) {
								// driver.setFirstBankFirstPatch();
							}
						}

						for (Map.Entry<SynthDriverPatchImpl, Integer> entry : supportedDevices.entrySet()) {

							SynthDriverPatchImpl driver = entry.getKey();

							if (driver.isBankDriver()) {
								((SynthDriverBank) driver).sendCurrentbank();
								((SynthDriverBank) driver).resetCurrentbank();
							}
						}
						btnQuit.setEnabled(true);
						btnCopy.setEnabled(true);

					}

				};
				worker.start();
			}
		});

		btnCopy = new JButton("Copy to clippboard");
		buttons.add(btnCopy);
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = toolkit.getSystemClipboard();
				StringSelection selection = new StringSelection(ta.getText());
				clipboard.setContents(selection, null);
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