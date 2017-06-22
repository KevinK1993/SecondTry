package VierGewinnt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

/**
 * 
 * @author Lucille Reeve, Hannah Kuhn, Jens Michalke, Kevin Klages
*/

public class VierGewinnt extends Application {
    
    private final int spalten = 7;
    private final int zeilen = 6;
    private String spielernummer;
    public String spieler_am_zug = "1";
    private int gewinnzaehlerSpieler1_horizontal;
    private int gewinnzaehlerSpieler2_horizontal;
    private int gewinnzaehlerSpieler1_vertikal;
    private int gewinnzaehlerSpieler2_vertikal;
    private boolean spiel_beendet = false;
    
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	Socket socket;
	
	private GridPane grid = new GridPane();
    private Text spieler = new Text();
    private Text gewonnen = new Text();
    private Text verbindungsstatus = new Text();
    private Button verbinden_button = new Button();
    private TextField ip_adresse = new TextField();
	    
    String[][] gesetzteFelder = new String[12][12]; 
    String[] daten_vomServer = new String[3];
    Circle[][] kreisliste = new Circle[10][10]; 
    
    @Override
    public void start(Stage primaryStage) {
    	
        for (String[] row: gesetzteFelder)
        Arrays.fill(row, "");
        
        for (String[] row: gesetzteFelder)
        Arrays.fill(row, "");
                
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(50, 10, 10, 50)); 
        
        // Weiße Kreise werden automatisch dem Spielfeld hinzugefügt
        for(int i = 0; i < spalten; i++){
            for(int k = 0; k < zeilen; k++){
                
            	Circle kreis = new Circle(50);
            	kreisliste[i][k] = kreis;
            
                final int aktuelleSpalte = i;
                
                verbinden_button.setOnMouseClicked(new EventHandler<MouseEvent>(){            	
                    @Override
                    public void handle(MouseEvent t) {
                    	client_starten(ip_adresse.getText());                      
                    }
                });

                // Bei Klick auf einen Kreis
                kreis.setOnMouseClicked(new EventHandler<MouseEvent>(){
                	@Override
                    public void handle(MouseEvent t) {
                    	if(spieler_am_zug.equals(spielernummer)  && spiel_beendet != true){
                    		
                    		// Das nächste freie Feld in einer angeklickten Spalte wird gesucht
	                        int freieZeile = finde_freiesFeld(aktuelleSpalte);
	                                                        
	                        // Aktuelle Zeilen- und Spaltennummer des gesetzte Chip an Server senden	
	                        String serveraktuelleSpalte = Integer.toString(aktuelleSpalte);
	                        String serveraktuelleZeile = Integer.toString(freieZeile);
	
	                        anServer_senden(serveraktuelleSpalte);
	                        anServer_senden(serveraktuelleZeile);
	                        anServer_senden(spielernummer);
                    	}
                        
                    }
                });
                kreis.setFill(Color.WHITE);
                grid.add(kreis, i, k);
            }
        }
             
        spieler.setFill(Color.WHITE);
        gewonnen.setFill(Color.WHITE);
        spieler.setFont(Font.font ("Arial", 20));
        gewonnen.setFont(Font.font ("Arial", 20));
        verbindungsstatus.setFont(Font.font ("Arial", 20));
        verbinden_button.setText("Verbinden");

        grid.add(spieler, 8, 0);
        grid.add(gewonnen, 8, 2);       
        grid.add(ip_adresse, 8, 5);
        grid.add(verbinden_button, 9, 5);
        grid.add(verbindungsstatus, 8, 6);
        
        Scene scene = new Scene(grid, 1300, 850, Color.BLUE);
        grid.setStyle("-fx-background-color: null;");
        primaryStage.setTitle("Vier Gewinnt");
        primaryStage.setScene(scene);  
        primaryStage.show();
    }
    
    public void client_starten(String ip){	      
		try {
			socket = new Socket(ip, 5888);

			fromServer = new DataInputStream(socket.getInputStream());
			this.spielernummer = fromServer.readUTF();
			if(spielernummer.equals("1") || spielernummer.equals("2")){
				spieler.setText("Spieler " + spielernummer);
			} else {
				spieler.setText("Zuschauer");
			}
			
						
			toServer = new DataOutputStream(socket.getOutputStream());
			
			grid.getChildren().remove(ip_adresse);
			grid.getChildren().remove(verbinden_button);
			verbindungsstatus.setFill(Color.WHITE);
			verbindungsstatus.setText("Mit Server verbunden");			
		} catch (Exception e) {
			verbindungsstatus.setFill(Color.RED);
			verbindungsstatus.setText("Verbindung fehlgeschlagen");
		}
		
		// Nachrichten werden vom Server abgefangen
        new Thread( () -> {
        	try {
        		while(true){
            		String nachricht_vom_Server = fromServer.readUTF();
            		if(nachricht_vom_Server != "" && nachricht_vom_Server != null){
            			chipeinfuegen(nachricht_vom_Server);
            		}
        		}
    		} catch (Exception e) {  			
    		} 
		}).start();  	
    }

    public void chipeinfuegen(String nachricht_vom_Server){
    	
        //int umgewandelteNachricht = Integer.parseInt(nachricht_vom_Server);
        boolean alleDatenvorhanden = false;

        if(daten_vomServer[0] == "" || daten_vomServer[0] == null){
            daten_vomServer[0] = nachricht_vom_Server;
            alleDatenvorhanden = false;
        } else if(daten_vomServer[1] == "" || daten_vomServer[1] == null) {
        	daten_vomServer[1] = nachricht_vom_Server;
        	alleDatenvorhanden = false;     	
        } else if (daten_vomServer[2] == "" || daten_vomServer[2] == null) {
        	daten_vomServer[2] = nachricht_vom_Server;
        	alleDatenvorhanden = true;  
        }
        
        if(alleDatenvorhanden == true){
        	int i = Integer.parseInt(daten_vomServer[0]);
        	int k = Integer.parseInt(daten_vomServer[1]);
        	
        	Circle aktuellerKreis = kreisliste[i][k];
        	if(daten_vomServer[2].equals("1")){
        		aktuellerKreis.setFill(Color.RED);
        		spieler_am_zug = "2";
        	} else {
        		aktuellerKreis.setFill(Color.YELLOW);
        		spieler_am_zug = "1";
        	}
        	
        	// Für die Gewinnabfrage werden die belegten Felder und die dazugehörigen Spieler in ein Array gespeichert
        	gesetzteFelder[i][k] = daten_vomServer[2];    	 
            horizontal_gewonnen(i, k);
            vertikal_gewonnen(i, k);
            links_hoch_diagonal_gewonnen(i, k);
            rechts_hoch_diagonal_gewonnen(i, k);
             
        	daten_vomServer[0] = "";
        	daten_vomServer[1] = "";
          	daten_vomServer[2] = "";
        	alleDatenvorhanden = false;   	
        }   	
    }
    
    public int finde_freiesFeld(int spalte){
        int freie_zeile = 0;
        for(int zaehler = 0; zaehler < zeilen; zaehler++){
            if(gesetzteFelder [spalte][zaehler] == "" || gesetzteFelder [spalte][zaehler] == null){
                freie_zeile = zaehler;
            }
        }
        return freie_zeile;
    }
    
    // Server
    public void anServer_senden(String nachricht){
		try {
			toServer.writeUTF(nachricht);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    }
    
    public void horizontal_gewonnen(int aktuelle_spalte, int aktuelle_zeile){
    	String gesetzterChip = gesetzteFelder[aktuelle_spalte][aktuelle_zeile];
        for (int i=0;i<spalten;i++)
        {
            if (gesetzteFelder[aktuelle_spalte][i].equals(gesetzterChip)){
                gewinnzaehlerSpieler1_horizontal++;
            } else {
                gewinnzaehlerSpieler1_horizontal=0;
            }
            if (gewinnzaehlerSpieler1_horizontal>=4) {
            	gewonnen.setText("Spieler " +  gesetzterChip + " hat gewonnen!");
            	spiel_beendet = true;
            }
        }      
    }
    
    public void vertikal_gewonnen(int aktuelle_spalte, int aktuelle_zeile){
    	String gesetzterChip = gesetzteFelder[aktuelle_spalte][aktuelle_zeile];
        for (int i=0;i<zeilen;i++)
        {
            if (gesetzteFelder[i][aktuelle_zeile].contentEquals(gesetzterChip)){
                gewinnzaehlerSpieler1_vertikal++;
            } else {
                gewinnzaehlerSpieler1_vertikal=0;
            }
            if (gewinnzaehlerSpieler1_vertikal>=4) {
            	gewonnen.setText("Spieler " +  gesetzterChip + " hat gewonnen!");
            	spiel_beendet = true;
            }
        }
    }
    
    public void links_hoch_diagonal_gewonnen(int aktuelle_spalte, int aktuelle_zeile){
    	
		// Diagonal links nach oben
    	
    	String gesetzterChip = gesetzteFelder[aktuelle_spalte][aktuelle_zeile];
    	
    	int gleicheFarbe = 1;
		
    	for (int zaehler = 1; zaehler <= 3; zaehler++) {
			if (gesetzteFelder[aktuelle_spalte + zaehler][aktuelle_zeile - zaehler].equals(gesetzterChip)){
				gleicheFarbe++;
			} else {
				break;
			}
    	}
    	
    	for (int zaehler = 1; zaehler <= 3; zaehler++) {
			if (gesetzteFelder[aktuelle_spalte - zaehler][aktuelle_zeile + zaehler].equals(gesetzterChip)){
				gleicheFarbe++;
			} else {
				break;
			}
    	}
    	if(gleicheFarbe == 4){
    		gewonnen.setText("Spieler " + gesetzterChip + " hat gewonnen!");
    		spiel_beendet = true;
    	}
    	

    }
    
    public void rechts_hoch_diagonal_gewonnen(int aktuelle_spalte, int aktuelle_zeile){
		
		// Diagonal rechts nach oben
    	
    	String gesetzterChip = gesetzteFelder[aktuelle_spalte][aktuelle_zeile];
    	
    	int gleicheFarbe = 1;
		
    	for (int zaehler = 1; zaehler <= 3; zaehler++) {
			if (gesetzteFelder[aktuelle_spalte + zaehler][aktuelle_zeile + zaehler].equals(gesetzterChip)){
				gleicheFarbe++;
			} else {
				break;
			}
    	}
    	
    	for (int zaehler = 1; zaehler <= 3; zaehler++) {
			if (gesetzteFelder[aktuelle_spalte - zaehler][aktuelle_zeile - zaehler].equals(gesetzterChip)){
				gleicheFarbe++;
			} else {
				break;
			}
    	}
    	if(gleicheFarbe == 4){
    		gewonnen.setText("Spieler " + gesetzterChip + " hat gewonnen!");
    		spiel_beendet = true;
    	}
    	
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop(){
    	try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
