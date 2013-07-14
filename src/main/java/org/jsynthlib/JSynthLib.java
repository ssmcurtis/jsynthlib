package org.jsynthlib;

import java.util.ArrayList;

import org.jsynthlib.tools.ErrorMsgUtil;

public class JSynthLib {

	private static int debugLevel = 3;
	private static ArrayList<String> fileList = new ArrayList<String>();
	private static String studio = "devices";

	public static String getStudio() {
		return studio;
	}

	public static void setStudio(String studio) {
		JSynthLib.studio = studio;
	}

	public static void main(String[] args) {
		parseArgument(args);
		// @SuppressWarnings("unused")
		PatchBayApplication frame = new PatchBayApplication(fileList, debugLevel);
		// frame.setVisible(true);
	}

	private static void parseArgument(String[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-D")) {
					// may cause Illegal Index Exception
					int tmpDebugLevel = Integer.parseInt(args[++i]);
					//debugLevel = tmpDebugLevel > 1 ? 1 : tmpDebugLevel;
					debugLevel = tmpDebugLevel;
				} else if (args[i].startsWith("-S")) {
					studio = args[++i];
					studio = studio.replaceAll("[^A-Za-z 0-9]", "x");
					if (studio.isEmpty()) {
						studio = "devices";
					}
				} else if (args[i].startsWith("-h")) {
					usage(0);
				} else if (args[i].startsWith("-")) {
					usage(1);
				} else {
					// fileList.add(args[i]);
				}
			}
			ErrorMsgUtil.reportStatus(" Studio: " + studio);
		} catch (Exception e) {
			usage(1);
		}
	}

	private static void usage(int status) {
		System.err.println("usage: java JSynthLib [-D number]  [-S studioconfig]");
		System.err.println("    -S configuration name for used synth drivers (will be created if not exists), "
				+ "see preferences -> synth driver for current configuration");
		System.err.println("    -D number\tset debugging flags (argument is a bit mask)");
		System.err.println("\t1\tmisc");
		System.err.println("\t2\tdump stack");
		System.err.println("\t4\tMIDI");
		System.err.println("\t8\tframe");
		System.err.println("\t...");
		System.err.println("\t-1\tall");
		System.exit(status);
	}

}
