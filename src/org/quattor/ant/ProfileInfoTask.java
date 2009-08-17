package org.quattor.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ProfileInfoTask extends Task implements java.io.FileFilter {

	/* The output directory for compiled profiles. */
	private File outputdir = null;

	/* Name of XML file containing the list of profiles. Default should be appropriate. */
	private String profilesInfoName = "profiles-info.xml";
	
	/* Control printing of debugging messages in this task */
	private boolean debugTask = false;
	
	/*
	 * Method used by ant to execute this task.
	 */
	@Override
	public void execute() throws BuildException {

		// Sanity checks on the output directory.
		if (outputdir == null) {
			throw new BuildException("outputdir not specified");
		}
		if (outputdir == "") {
			if ( debugTask ) {
				System.out.println("outputdir parameters is an empty string: do nothing.");
			}
			return;
		}
		if (!outputdir.exists()) {
			throw new BuildException("outputdir (" + outputdir
					+ ") does not exist");
		}
		if (!outputdir.isDirectory()) {
			throw new BuildException("outputdir (" + outputdir
					+ ") is not a directory");
		}
		
		System.out.println("Updating "+profilesInfoName+" in "+outputdir);

		// Get all of the profiles in the given directory.
		StringBuffer contents = new StringBuffer(
				"<?xml version='1.0' encoding='utf-8'?>\n");
		contents.append("<profiles>\n");
		File[] files = outputdir.listFiles(this);
		for (File file : files) {
			long mtime = file.lastModified();
			contents.append("<profile mtime='");
			contents.append(mtime);
			contents.append("'>");
			contents.append(file.getName());
			contents.append("</profile>\n");
		}
		contents.append("</profiles>\n");

		// Create the output file.
		File info = new File(outputdir, profilesInfoName);
		try {

			// Open the output file.
			Writer writer = new OutputStreamWriter(new FileOutputStream(info));
			writer.write(contents.toString());
			writer.close();

		} catch (IOException ioe) {
			throw new BuildException("Can't write profile info file. "
					+ info.getAbsolutePath() + "\n");
		}

	}

	/*
	 * Set the directory for the compiled profiles.
	 * 
	 * @param outputdir File containing output directory
	 */
	public void setOutputdir(File outputdir) {
		this.outputdir = outputdir;
	}

	/**
	 * This implements the FileFilter interface to allow template files to be
	 * selected. This filter will accept any file that ends with the suffix
	 * ".xml", has at least 5 characters (i.e. something before the suffix), and
	 * is not hidden. The file "profile-info.xml" is specifically excluded.
	 */
	public boolean accept(File file) {
		String name = file.getName();
		boolean ok = (!profilesInfoName.equals(name)) && !file.isHidden()
				&& (name.length() > 4 && name.endsWith(".xml"));
		return ok;
	}

	/**
	 * Setting this flag will print debugging information from the task itself.
	 * This is primarily useful if one wants to debug a build using the command
	 * line interface.
	 * 
	 * @param debugTask
	 *            flag to print task debugging information
	 */
	public void setDebugTask(boolean debugTask) {
		this.debugTask = debugTask;
	}

}
