package org.jsynthlib._prop;

/**
 * Storable.java - Interface for objects that support simple persistence
 * (currently implemented as saving to properties files)
 * @version $Id$
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @see Storage
 */
import java.util.Set;

public interface Storable {
	/**
	 * Get the names of properties that should be stored and loaded. For each field name foo, we expect to see methods
	 * getFoo() and setFoo().
	 * 
	 * @return a Set of field names
	 */
	public Set storedProperties();

	/**
	 * Method that will be called after loading
	 */
	public void afterRestore();
}
