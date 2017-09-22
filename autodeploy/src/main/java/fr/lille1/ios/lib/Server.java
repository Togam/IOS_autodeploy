package fr.lille1.ios.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author six
 *
 */
public class Server implements Runnable {

	private Path path = null;
	private BundleContext context;

	/**
	 * Constructeur
	 * 
	 * @param pathfolder
	 *            path du dossier à espionner
	 */
	public Server(Path pathfolder, BundleContext context) {
		this.path = pathfolder;
		this.context = context;
		System.out.println("Dossier " + pathfolder + " espionné : start");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		WatchService service = null;
		try {
			service = this.path.getFileSystem().newWatchService();
			// Enregistrer les opérations à surveiller
			this.path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			// TODO : au demarrage checker tous les fichiers déjà présent dans le répertoire
			// et les update
//
//			File dossier = new File(this.path.toString());
//			File[] fichiersDejaPresent = dossier.listFiles();
//			Bundle[] bundleInstalles = this.context.getBundles();
//
//			for (int i = 0; i < fichiersDejaPresent.length; i++) {
//				
//				
//			}
			// this.context.getBundles() ??

			WatchKey watchKey;

			while (!Thread.interrupted()) {
				watchKey = service.take();

				// traiter les evenements
				for (WatchEvent<?> event : watchKey.pollEvents()) {

					String fileName = event.context().toString();

					// Vérifie que le fichier sur lequel il y a un evenement est un .jar, sinon il
					// ne fera rien
					if (fileName.endsWith(".jar")) {
						File file = new File(this.path + "/" + fileName);

						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
							System.out.println("nouveau fichier : " + fileName);
							this.context.installBundle(file.toURI().toURL().toString());
							this.context.getBundle(file.toURI().toURL().toString()).start();

						} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
							System.out.println(fileName + " a été modifié");
							this.context.getBundle(file.toURI().toURL().toString()).update();

						} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
							System.out.println(fileName + " a été supprimé");
							this.context.getBundle(file.toURI().toURL().toString()).uninstall();

						} else if (StandardWatchEventKinds.OVERFLOW.equals(event.kind())) {
							System.out.println("Strange event");
							continue;
						}
					}
				}
				// se place en attente de message
				watchKey.reset();
			}
		} catch (InterruptedException ex) {
			try {
				if (service != null)
					service.close();
				System.out.println("Arrêt de l'espionnage du dossier");
			} catch (IOException ex1) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
			}
		} catch (Exception ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @return
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @param path
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * @return
	 */
	public BundleContext getContext() {
		return context;
	}

	/**
	 * @param context
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

}
