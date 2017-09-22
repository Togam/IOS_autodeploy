package fr.lille1.ios;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import fr.lille1.ios.lib.Server;

/**
 * @author six
 *
 */
public class Activator implements BundleActivator {

	Server server = null;
	Thread threadEspion = null;
	List<String> files = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.server = new Server(Paths.get("/local/six/projects/workspaceIOS/bundles_a_deployer"), context);
		threadEspion = new Thread(server);
		threadEspion.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		threadEspion.interrupt();
	}
}
