/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viergewinnt;

import java.util.Arrays;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author Kevin
 */
public class VierGewinnt extends Application {
    
    private final int spalten = 7;
    private final int zeilen = 6;
    private boolean spieler = true;
    private int gewinnzaehlerSpieler1_horizontal;
    private int gewinnzaehlerSpieler2_horizontal;
    private int gewinnzaehlerSpieler1_vertikal;
    private int gewinnzaehlerSpieler2_vertikal;
    
     
    String[][] gesetzteFelder = new String[7][7]; 

    
    
    @Override
    public void start(Stage primaryStage) {
        
        for (String[] row: gesetzteFelder)
        Arrays.fill(row, "");
            
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(50, 10, 10, 50)); 
        
        // Kreise werden hinzugefügt
        for(int i = 0; i < spalten; i++){
            for(int k = 0; k < zeilen; k++){
                
                Circle kreis = new Circle(50); 
                final int aktuelleSpalte = i;
                final int aktuelleZeile = k;
                
                // Bei Klick auf einen Kreis
                kreis.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent t) {
                        int freieZeile = finde_freiesFeld(aktuelleSpalte);
                        if(spieler ==  true){
                            
                            gesetzteFelder [aktuelleSpalte][freieZeile] = "Spieler 1";
                           
                            // Neuen roten Chip setzen
                            //grid.getChildren().remove(aktuelleSpalte,freieZeile);
                            Circle kreis = new Circle(50);
                            kreis.setFill(Color.RED);
                            grid.add(kreis, aktuelleSpalte, freieZeile);
                            
                            boolean gewonnen_horizontal = horizontal_gewonnen(aktuelleSpalte);
                            boolean gewonnen_vertikal = vertikal_gewonnen(aktuelleZeile);
                            
                            if(gewonnen_horizontal == true || gewonnen_vertikal == true){
                                System.out.println("Spieler 1 hat gewonnen.");
                            }
                                                        
                            spieler = false;
                            
                        } else {
                            
                            gesetzteFelder [aktuelleSpalte][freieZeile] = "Spieler 2";
                            
                            // Neuen gelben Chip setzen
                           // grid.getChildren().remove(aktuelleSpalte,freieZeile);
                            Circle kreis = new Circle(50);
                            kreis.setFill(Color.YELLOW);
                            grid.add(kreis, aktuelleSpalte, freieZeile);
                            
                            
                            boolean gewonnen_horizontal = horizontal_gewonnen(aktuelleSpalte);
                            boolean gewonnen_vertikal = vertikal_gewonnen(aktuelleZeile);
                            
                            if(gewonnen_horizontal == true || gewonnen_vertikal == true){
                                System.out.println("Spieler 2 hat gewonnen.");
                            }
                            
                            spieler = true;
                        }
                        
                    }
                });
                kreis.setFill(Color.WHITE);
                grid.add(kreis, i, k);
            }
        }
        

        Scene scene = new Scene(grid, 1100, 900, Color.BLUE);  
        primaryStage.setTitle("Vier Gewinnt");
        primaryStage.setScene(scene);  
        primaryStage.show(); 
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
    
    public boolean horizontal_gewonnen(int aktuelle_spalte){
        for (int i=0;i<spalten;i++)
        {
            System.out.println(gesetzteFelder[aktuelle_spalte][i]);
            if (gesetzteFelder[aktuelle_spalte][i].equals("Spieler 1")){
                gewinnzaehlerSpieler1_horizontal++;
            } else {
                gewinnzaehlerSpieler1_horizontal=0;
            }
            if (gewinnzaehlerSpieler1_horizontal>=4) {
                return true;
            }
            
            if (gesetzteFelder[aktuelle_spalte][i].equals("Spieler 2")){
                gewinnzaehlerSpieler2_horizontal++;
            } else {
                gewinnzaehlerSpieler2_horizontal=0;
            }
            if (gewinnzaehlerSpieler2_horizontal>=4) {
                return true;
            }
        }
        return false;
    }
    
    public boolean vertikal_gewonnen(int aktuelle_zeile){
        for (int i=0;i<zeilen;i++)
        {
            if (gesetzteFelder[i][aktuelle_zeile]== "Spieler 1"){
                gewinnzaehlerSpieler1_vertikal++;
            } else {
                gewinnzaehlerSpieler1_vertikal=0;
            }
            if (gewinnzaehlerSpieler1_vertikal>=4) {
                return true;
            }
            
            if (gesetzteFelder[i][aktuelle_zeile]== "Spieler 2"){
                gewinnzaehlerSpieler2_vertikal++;
            } else {
                gewinnzaehlerSpieler2_vertikal=0;
            }
            if (gewinnzaehlerSpieler2_vertikal>=4) {
                return true;
            }
        }
        return false;
    }
    
    public void diagonal_gewonnen(){
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
