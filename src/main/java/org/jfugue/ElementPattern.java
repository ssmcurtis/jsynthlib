/**
 * 
 */
package org.jfugue;

import java.util.List;

import org.jfugue.elements.JFugueElement;

/**
 * @author joshua
 *
 */
@SuppressWarnings("serial")
public class ElementPattern extends Pattern {

	protected List<JFugueElement> elements;
	
	/**
	 * 
	 */
	public ElementPattern() {
		
	}

	/**
	 * @param musicString
	 */
	public ElementPattern(String musicString) {
		super(musicString);
		
	}

	/**
	 * @param strings
	 */
	public ElementPattern(String... strings) {
		super(strings);
		
	}

	/**
	 * @param pattern
	 */
	public ElementPattern(Pattern pattern) {
		super(pattern);
		
	}

	/**
	 * @param elements
	 */
	public ElementPattern(JFugueElement... elements) {
		super(elements);
		
	}

}
