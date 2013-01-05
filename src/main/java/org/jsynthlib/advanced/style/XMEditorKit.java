package org.jsynthlib.advanced.style;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;


public class XMEditorKit extends StyledEditorKit {
	protected Hashtable imageTable = new Hashtable();
	protected Hashtable imageFileTable = new Hashtable();

	public XMEditorKit() {
		super();
	}

	static public class XMTextAction extends StyledTextAction {
		public XMTextAction(String name) {
			super(name);
		}

		public XMEditor getXMEditor(ActionEvent event) {
			XMEditor editor = (XMEditor) super.getEditor(event);
			if (editor == null) {
				Object c = event.getSource();
				while (c != null) {
					c = ((Container) c).getParent();
					if (c instanceof JPopupMenu) {
						c = ((JPopupMenu) c).getInvoker();
						if (c instanceof XMEditor) {
							editor = (XMEditor) c;
							break;
						}
					}
				}
			}
			return editor;
		}

		public void actionPerformed(ActionEvent event) {
		}
	}

	static public class TextColorAction extends XMTextAction {
		protected JColorChooser colorChooser = new JColorChooser();

		public TextColorAction(String nm) {
			super(nm);
		}

		public void actionPerformed(ActionEvent event) {
			XMEditor editor = getXMEditor(event);
			if (editor != null) {
				Color color = JColorChooser.showDialog(editor, "Choose Background Color", Color.white);

				if ((event != null) && (event.getSource() == editor)) {
					String s = event.getActionCommand();
					try {
						color = Color.decode(s);
					} catch (NumberFormatException nfe) {
					}
				}
				if (color != null) {
					MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setForeground(attr, color);
					setCharacterAttributes(editor, attr, false);
				} else {
					UIManager.getLookAndFeel().provideErrorFeedback(editor);
				}
			}
		}
	}

	static public class TextBackgroundAction extends XMTextAction {
		protected JColorChooser colorChooser = new JColorChooser();

		/**
		 * Creates a new ForegroundAction.
		 * 
		 * @param nm
		 *            the action name
		 * @param fg
		 *            the foreground color
		 */
		public TextBackgroundAction(String nm) {
			super(nm);
		}

		/**
		 * Sets the foreground color.
		 * 
		 * @param e
		 *            the action event
		 */
		public void actionPerformed(ActionEvent event) {
			XMEditor editor = getXMEditor(event);
			if (editor != null) {
				Color color = JColorChooser.showDialog(editor, "Choose Background Color", Color.white);

				if ((event != null) && (event.getSource() == editor)) {
					String s = event.getActionCommand();
					try {
						color = Color.decode(s);
					} catch (NumberFormatException nfe) {
					}
				}
				if (color != null) {
					MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setBackground(attr, color);
					setCharacterAttributes(editor, attr, false);
				} else {
					UIManager.getLookAndFeel().provideErrorFeedback(editor);
				}
			}
		}
	}

	static public class BackgroundAction extends XMTextAction {
		protected JColorChooser colorChooser = new JColorChooser();

		/**
		 * Creates a new ForegroundAction.
		 * 
		 * @param nm
		 *            the action name
		 * @param fg
		 *            the foreground color
		 */
		public BackgroundAction(String nm) {
			super(nm);
		}

		/**
		 * Sets the foreground color.
		 * 
		 * @param e
		 *            the action event
		 */
		public void actionPerformed(ActionEvent event) {
			XMEditor editor = getXMEditor(event);
			if (editor != null) {
				Color color = JColorChooser.showDialog(editor, "Choose Background Color", editor.getBackground());

				if ((event != null) && (event.getSource() == editor)) {
					String s = event.getActionCommand();
					try {
						color = Color.decode(s);
					} catch (NumberFormatException nfe) {
					}
				}
				if (color != null) {
					editor.setBackground(color);
				} else {
					UIManager.getLookAndFeel().provideErrorFeedback(editor);
				}
			}
		}
	}

	static public class InsertImageAction extends XMTextAction {
		protected JFileChooser imageFileChooser = new JFileChooser();

		public InsertImageAction(String name) {
			super(name);
			ExtensionFileFilter filter = new ExtensionFileFilter();
			filter.addExtension("jpg");
			filter.addExtension("gif");
			filter.setDescription("JPG & GIF Images");
			imageFileChooser.setFileFilter(filter);
		}

		public void actionPerformed(ActionEvent event) {
			XMEditor editor = getXMEditor(event);
			if (editor != null) {
				XMEditorKit editorKit = (XMEditorKit) editor.getEditorKit();
				int returnVal = imageFileChooser.showOpenDialog(editor);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File imageFile = imageFileChooser.getSelectedFile();
					ImageIcon image = new ImageIcon(imageFile.getAbsolutePath());
					editor.insertIcon(image);
					editorKit.imageTable.put(image, imageFile);
					editorKit.imageFileTable.put(imageFile.getAbsolutePath(), image);
				}
			}
		}
	}
}
