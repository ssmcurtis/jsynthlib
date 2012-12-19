package org.jsynthlib;

public enum JSynthResource {

	RESOURCE_NAME_DEVICES_CONFIG("/org/jsynthlib/resources/synthdrivers.properties"),
	RESOURCE_NAME_DOCUMENTATION("/org/jsynthlib/resources/documentation.html"),
	RESOURCE_NAME_LICENSE("/org/jsynthlib/resources/license.txt");

	private final String uri;

	JSynthResource(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}
