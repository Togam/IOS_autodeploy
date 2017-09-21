package fr.lille1.ios.lib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author six
 *
 */
public class Server implements Runnable {

	private Path path = null;
	ArrayList<String> nouveauxFichiers;
	ArrayList<String> fichiersSupprimés;

	/**
	 * Constructeur
	 * 
	 * @param pathfolder
	 *            path du dossier à espionner
	 */
	public Server(String pathfolder) {
		this.path = Paths.get(pathfolder);
		System.out.println("Dossier " + pathfolder + " espionné : start");
		this.nouveauxFichiers = new ArrayList<String>();
		this.fichiersSupprimés = new ArrayList<String>();
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
					System.out.println(fileName);
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
						nouveauxFichiers.add(this.path+fileName);
						System.out.println("new file create " + fileName);
					} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
						System.out.println(fileName + " has been modified");
					} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
						System.out.println(fileName + " has been deleted");
						fichiersSupprimés.add(this.path+fileName);
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
	public ArrayList<String> getNouveauxFichiers() {
		return nouveauxFichiers;
	}

	/**
	 * @param nouveauxFichiers
	 */
	public void setNouveauxFichiers(ArrayList<String> nouveauxFichiers) {
		this.nouveauxFichiers = nouveauxFichiers;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getFichiersSupprimés() {
		return fichiersSupprimés;
	}

	/**
	 * @param fichiersSupprimés
	 */
	public void setFichiersSupprimés(ArrayList<String> fichiersSupprimés) {
		this.fichiersSupprimés = fichiersSupprimés;
	}

}
