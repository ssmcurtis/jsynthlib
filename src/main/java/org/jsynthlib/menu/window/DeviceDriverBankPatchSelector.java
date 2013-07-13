/*
 * DevDrvpatchSelector.java
 */

package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.UiUtil;

import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of a Patch. Only Devices, Drivers, Bank- and
 * PatchNumbers are choosable, which are supporting the Patch. Is used for Reassign..., Store... and SendTo... a patch.
 * 
 * @author Torsten Tittmann
 * @version $Id$
 */
@SuppressWarnings("serial")
public class DeviceDriverBankPatchSelector extends JDialog {

	// ===== Instance variables
	/** The last index in driver Combo Box. */
	private int driverNum;
	private int patchNum;
	private int bankNum;
	protected Patch p;

	private byte[] sysexByteArray;
	private String patchHeaderString;

	private JLabel myLabel;
	private JComboBox<Device> deviceComboBox;
	protected JComboBox<SynthDriver> driverComboBox;
	protected JComboBox<String> bankComboBox;
	protected JComboBox<String> patchNumComboBox;

	/**
	 * Constructor without Bank/Patch ComboBox.
	 * 
	 * @param patch
	 *            The Patch to store
	 * @param wintitle
	 *            String which appears as window title
	 * @param action
	 *            String which describe the used menu item
	 */
	// for SendToDialog and reassignDialog
	public DeviceDriverBankPatchSelector(Patch patch, String wintitle) {
		super(PatchBayApplication.getInstance(), wintitle, true);

		p = patch;
		sysexByteArray = patch.getByteArray();
		patchHeaderString = patch.getPatchHeader();
	}

	public DeviceDriverBankPatchSelector(String patchHeaderString, String wintitle) {
		super(PatchBayApplication.getInstance(), wintitle, true);
		this.patchHeaderString = patchHeaderString;
	}

	/**
	 * Constructor with Bank/Patch ComboBox
	 * 
	 * @param patch
	 *            The Patch to store
	 * @param patchnum
	 *            The default patchNumber selected in the patch Combobox.
	 * @param wintitle
	 *            String which appears as window title
	 * @param action
	 *            String which describe the used menu item
	 */
	// for SysexStoreDialog
	public DeviceDriverBankPatchSelector(Patch patch, int banknum, int patchnum, String wintitle) {
		super(PatchBayApplication.getInstance(), wintitle, true);

		this.p = patch;
		this.sysexByteArray = patch.getByteArray();
		this.patchHeaderString = patch.getPatchHeader();
		this.patchNum = patchnum;
		this.bankNum = banknum;
	}

	public void initDialog(String action, boolean hasBPComboBox) {
		// now the panel
		JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

		myLabel = new JLabel(action, JLabel.CENTER);
		dialogPanel.add(myLabel, BorderLayout.NORTH);

		// =================================== Combo Panel ==================================
		// ----- Create the combo boxes
		deviceComboBox = new JComboBox<Device>();
		deviceComboBox.addActionListener(new DeviceActionListener());
		driverComboBox = new JComboBox<SynthDriver>();
		if (hasBPComboBox) {
			driverComboBox.addActionListener(new DriverActionListener());
			bankComboBox = new JComboBox<String>();
			patchNumComboBox = new JComboBox<String>();
		}

		// ----- Populate the combo boxes only with devices, which supports the patch
		int nDriver = 0;
		for (int i = 0; i < AppConfig.deviceCount(); i++) {
			Device device = AppConfig.getDevice(i);

			boolean newDevice = true;

			for (int j = 0, m = 0; j < device.driverCount(); j++) {
				SynthDriver driver = device.getDriver(j);

				if (patchIsSupported(driver)) {
					ErrorMsgUtil.reportStatus(">>> Supported driver ... ");
					if (newDevice) { // only one entry for each supporting device
						deviceComboBox.addItem(device);
						newDevice = false;
					}
					if (p != null && p.getDriver() == driver) {
						driverNum = m;
						deviceComboBox.setSelectedIndex(deviceComboBox.getItemCount() - 1);
					}
					nDriver++;
					m++;
				}
			} // driver loop
		} // device loop
		deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

		// ----- Layout the labels in a panel.
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		labelPanel.add(new JLabel("Device:", JLabel.LEFT));
		labelPanel.add(new JLabel("Driver:", JLabel.LEFT));
		if (hasBPComboBox) {
			labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
			labelPanel.add(new JLabel("Patch:", JLabel.LEFT));
		}

		// ----- Layout the fields in a panel
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
		fieldPanel.add(deviceComboBox);
		fieldPanel.add(driverComboBox);
		if (hasBPComboBox) {
			fieldPanel.add(bankComboBox);
			fieldPanel.add(patchNumComboBox);
		}

		// ----- Create the comboPanel, labels on left, fields on right
		JPanel comboPanel = new JPanel(new BorderLayout());
		comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		comboPanel.add(labelPanel, BorderLayout.CENTER);
		comboPanel.add(fieldPanel, BorderLayout.EAST);
		dialogPanel.add(comboPanel, BorderLayout.CENTER);

		// =================================== Button Panel ==================================
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton doit = new JButton(action);
		doit.addActionListener(new DoitActionListener());
		buttonPanel.add(doit);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		buttonPanel.add(cancel);
		getRootPane().setDefaultButton(doit);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

		// ===== Final initialisation of dialog box
		getContentPane().add(dialogPanel);
		pack();
		UiUtil.centerDialog(this);

		if (nDriver > 0) {
			setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null, "Oops, No driver was found, which support this patch! Nothing will happen",
					"Error during: " + action, JOptionPane.WARNING_MESSAGE);
			dispose();
		}
	}

	protected void doit() {
	}

	/**
	 * Makes the actual work after pressing the 'Store' button
	 */
	private class DoitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			doit();
		}
	}

	/**
	 * Re-populate the Driver ComboBox with valid drivers after a Device change
	 */
	private class DeviceActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			driverComboBox.removeAllItems();

			Device device = (Device) deviceComboBox.getSelectedItem();
			int nDriver = 0;
			for (int i = 0; i < device.driverCount(); i++) {
				SynthDriver driver = device.getDriver(i);

				// TODO ssymCurtis - only single ?
				if (patchIsSupported(driver)) {
					driverComboBox.addItem(driver);
					nDriver++;
				}
			}
			// the original driver is the default
			// When a different device is selected, driverNum can be out of range.
			driverComboBox.setSelectedIndex(Math.min(driverNum, nDriver - 1));
			driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
		}
	}

	/**
	 * Repopulate the Bank/Patch ComboBox with valid entries after a Device/Driver change
	 */
	private class DriverActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {

			SynthDriverPatch driver = (SynthDriverPatch) driverComboBox.getSelectedItem();
			bankComboBox.removeAllItems();
			patchNumComboBox.removeAllItems();

			if (driver != null) {
				// populate bank combo box
				String[] bankNumbers = driver.getBankNumbers();
				if (bankNumbers != null && bankNumbers.length > 1) {
					for (int i = 0; i < bankNumbers.length; i++) {
						bankComboBox.addItem(bankNumbers[i]);
					}
					bankComboBox.setSelectedIndex(Math.min(bankNum, bankComboBox.getItemCount() - 1));
				}
				if (driver.isSingleDriver()) {
					// populate patch number combo box
					String[] patchNumbers = getPatchNumbers(driver);
					if (patchNumbers.length > 1) {
						for (int i = 0; i < patchNumbers.length; i++) {
							patchNumComboBox.addItem(patchNumbers[i]);
						}
						patchNumComboBox.setSelectedIndex(Math.min(patchNum, patchNumComboBox.getItemCount() - 1));
					}
				}
			}
			bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
			// N.B. Do not enable patch selection for banks
			patchNumComboBox.setEnabled(patchNumComboBox.getItemCount() > 1);
		}
	}

	/**
	 * This method returns the list of patch numbers, which may change according to the dialog type (some have patch
	 * locations to which you can send but not store)
	 */
	protected String[] getPatchNumbers(SynthDriverPatch driver) {
		return driver.getPatchNumbers();
	}

	protected boolean patchIsSupported(SynthDriver driver) {
		return (driver.isSingleDriver() || driver.isBankDriver()) && driver.supportsPatch(patchHeaderString, sysexByteArray);
	}

	public String getPatchHeaderString() {
		return patchHeaderString;
	}

	public byte[] getSysexByteArray() {
		return sysexByteArray;
	}

}
