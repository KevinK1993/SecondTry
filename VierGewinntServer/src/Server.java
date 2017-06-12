
import java.io.*;
import java.net.*;
import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * 
 * @author Lucille Reeve, Hannah Kuhn, Jens Michalke, Kevin Klages
*/

public class Server extends Application {

	private int clientNummer = 0;
	private TextArea haupttextbox = new TextArea();
	ServerSocket serverSocket;
	static List<Socket> clients = new ArrayList<Socket>();

	@Override
	public void start (Stage primaryStage) throws Exception {
		haupttextbox.setEditable(false);

		Scene scene = new Scene(new ScrollPane(haupttextbox), 450, 200);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.show();

		new Thread( () -> {
			try {
				serverSocket = new ServerSocket(5888);

				Platform.runLater( () -> {
					haupttextbox.appendText("Server ist gestartet" + '\n');
				});

				while (true) {
					Socket socket = serverSocket.accept();
					clients.add(socket);
					clientNummer++;

					Platform.runLater( () -> {					
						haupttextbox.appendText("Client " + clientNummer + " IP-Adresse " + socket.getInetAddress().getHostAddress() + '\n');
					});
					
					
					DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());	
					String clientnummer = Integer.toString(clientNummer);
					outputToClient.writeUTF(clientnummer);
					
					new Thread(new ThreadClient(socket)).start();
				}
			} catch (Exception e) {
				haupttextbox.appendText(e.toString() + '\n');
			}
		}).start();
	}

	class ThreadClient implements Runnable {

		private Socket socket;

		public ThreadClient(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run () {
			try {
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				
				
				while (true) {
					String normal = inputFromClient.readUTF();
					
				for(Socket socket : clients){
						DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
						outputToClient.writeUTF(normal);
					}
				
					haupttextbox.appendText("Daten von Client bekommen: " + normal + '\n');
					
				}
			} catch (Exception e) {
				haupttextbox.appendText(e.toString() + '\n');
			}
		}
	}


	public static void main (String[] args) {
		Application.launch(args);
	}
	
    public void stop(){
    	try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}