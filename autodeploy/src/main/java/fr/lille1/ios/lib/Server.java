package fr.lille1.ios.lib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author six
 *
 */
public class Server implements Runnable {

	private Path path = null;

	/**
	 * Constructeur
	 * 
	 * @param pathfolder
	 *            path du dossier espionné
	 */
	public Server(String pathfolder) {
		this.path = Paths.get(pathfolder);
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

			WatchKey watchKey;

			while (!Thread.interrupted()) {
				watchKey = service.take();

				// traiter les evenements
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					String fileName = event.context().toString();
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
						System.out.println("new file create " + fileName);
					} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
						System.out.println(fileName + " has been modified");
					} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
						System.out.println(fileName + " has been deleted");
					} else if (StandardWatchEventKinds.OVERFLOW.equals(event.kind())) {
						System.out.println("Strange event");
						continue;
					}
				}
				// se place en attente de message
				watchKey.reset();
			}
		} catch (InterruptedException ex) {
			try {
				if (service != null)
					service.close();
				System.out.println("Stop FolderSpy");
			} catch (IOException ex1) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
			}
		} catch (Exception ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
