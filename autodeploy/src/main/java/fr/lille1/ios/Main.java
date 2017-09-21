package fr.lille1.ios;

import fr.lille1.ios.lib.Server;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		// Créer le worker
		Server server = new Server("c://temp//");
		// Créer le thred
		Thread folderSpyThread = new Thread(server);
		// Lancer le thread
		folderSpyThread.start();
		// Patienter 30 secondes
		Thread.sleep(30000);
		// Fermer le Thread
		folderSpyThread.interrupt();
	}
}
