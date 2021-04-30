

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;


public class Poker{

    public static ArrayList<Carte> paquet= new ArrayList<Carte>();   
    

    public Poker(){
        CreerPaquet();
    }

    public static void CreerPaquet(){

        String couleur[] = {"Pique", "Trefle", "Coeur", "Carreau"};
        String pt[] = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        paquet.clear();
        for(var i = 0; i < pt.length; i++){
            for(var y=0; y<couleur.length; y++){
                Carte carte = new Carte(couleur[y],pt[i]);  
                paquet.add(carte);
            }   
        }
        for(int i =0; i<10;i++){
         Collections.shuffle(paquet);
         
        }
    }

    public static ArrayList<Carte> Distribution(){
        ArrayList<Carte> jeu = new ArrayList<Carte>();
        for (int i =0;i<5 ;i++ ) {
            jeu.add(paquet.get(i));
            paquet.remove(i);
        }

        return jeu;
    }


    public static ArrayList<Carte> DistributionAdmin(){
        ArrayList<Carte> jeu = new ArrayList<Carte>();
        System.out.println("Distribution ADMIN");
        String pt[] = {"2", "10", "6", "K", "7"};
        Carte carte;
        for (int i =0;i<5 ;i++ ) {
            if(i%2==0){
                carte = new Carte("Trefle",pt[i]);
            }else{
                 carte = new Carte("Pique",pt[i]);
            }
            jeu.add(carte);
        }

        return jeu;
    }

    public static ArrayList<Carte> Change(ArrayList<Carte> mainJ,int a){
        ArrayList<Carte> jeu = mainJ;
        jeu.set(a,paquet.get(0));
        paquet.remove(0);
        return jeu;
    }

    public static int[] Classement(ArrayList<Carte> mainJ ){

        boolean couleur = true;
        boolean suite = true;
        /**
         *  Code des figure : 0-> rien; 1->paire; 2->double paire; 3 -> brelan
         *                    4 -> suite; 5->couleur; 6-> full; 8->carre; 9-> quinte flush(suite+couleur)
         */
        int codeFigure=0;
        
        //Trie de la liste 
        Collections.sort(mainJ);

        //Test Couleur
        for (int y=0;y<mainJ.size()-1 ;y++ ) {
            if(mainJ.get(y).getCouleur().compareTo(mainJ.get(y+1).getCouleur()) !=0 ){
                couleur = false;
            }
        }

        //Test suite 
        for (int i = 0;i<mainJ.size()-1 ; i++) {
            if(mainJ.get(i).getPoid()!=(mainJ.get(i+1).getPoid()-1)){
                if(mainJ.get(0).getPoid()==2 && i==mainJ.size()-2 && mainJ.get(mainJ.size()-1).getPoid()==14  ){
                    suite = true;
                }else{
                    suite = false;
                    break;
                }
            }
        }

        int cpt=0;
        int poidFigure=0;
        for (int q=0;q<mainJ.size() ;q++ ) {
            for (int g=0;g<q ;g++ ) {
                if(mainJ.get(g).getPoid() == mainJ.get(q).getPoid()){
                    cpt++;
                    poidFigure = mainJ.get(g).getPoid();
                }                                          
            }
        }


        if(suite && couleur){
            codeFigure =9;
        }else if (couleur) {
            codeFigure =5;
        }else if (suite) {
            codeFigure=4;
        }else{
            if(cpt>3){
                cpt=cpt+2;
            }

            codeFigure = cpt;
        }
        
        int rep[] = {codeFigure,poidFigure,mainJ.get(mainJ.size()-1).getPoid()};
        return rep;
    }


}

