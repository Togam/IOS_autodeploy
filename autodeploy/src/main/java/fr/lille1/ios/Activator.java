package fr.lille1.ios;

import java.io.File;
import java.util.ArrayList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import fr.lille1.ios.lib.Server;

/**
 * @author six
 *
 */
public class Activator implements BundleActivator {

	Server server = new Server("/local/six/projects/workspaceIOS/bundles_a_deployer");
	Thread folderSpyThread = new Thread(server);

	public void start(BundleContext context) throws Exception {
		folderSpyThread.start();
		ArrayList<String> nvxFichier = this.server.getNouveauxFichiers();
		for (String nameFile : nvxFichier) {
			File file = new File(nameFile);
			context.installBundle(file.toURI().toURL().toString());
		}
	}

	public void stop(BundleContext context) throws Exception {
		// Patienter 30 secondes
		Thread.sleep(30000);
		// Fermer le Thread
		folderSpyThread.interrupt();
	}

}
