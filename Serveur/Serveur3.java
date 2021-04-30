import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Serveur3{

Scanner sc = new Scanner(System.in);//pour lire à partir du clavier

public static void main(String args[]){

    Socket s=null;
    ServerSocket ss2=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    System.out.println("Server Listening......");
    try{
        ss2 = new ServerSocket(5000); // can also use static final PORT_NUM , when defined

    }
    catch(IOException e){
    e.printStackTrace();
    System.out.println("Server error");

    }

    //Connexion des joueurs au serveur
    while(true){
        try{

            s= ss2.accept();
            System.out.println("connection Established");
            ServerThread st=new ServerThread(s);
            st.add(s);
            st.start();

        }

    catch(Exception e){
        e.printStackTrace();
        System.out.println("Connection Error");

    }
    }

}

}

class ServerThread extends Thread{  

    public static boolean enJeu = false;
    String line=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;
    Scanner sc = new Scanner(System.in);//pour lire à partir du clavier

    ArrayList<Carte> mainJ;
    public static ArrayList<ArrayList> listMain = new ArrayList<ArrayList>();
    public static ArrayList<PrintWriter> listConnexion = new ArrayList<PrintWriter>();
    public static ArrayList<PrintWriter> listConnexionAtt = new ArrayList<PrintWriter>();
    public static ArrayList<BufferedReader> listIsAtt = new ArrayList<BufferedReader>();
    public static ArrayList<Integer> listJetons = new ArrayList<Integer>();
    int pot,mise;

    public ServerThread(Socket s){
        this.s=s;

        
    }

    public void add(Socket s){
        try{
            is= new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=new PrintWriter(s.getOutputStream());
            ServerThread.listConnexionAtt.add(os);
            ServerThread.listIsAtt.add(is);
            ServerThread.listJetons.add(100);
            if(enJeu){

                os.println("Jeu en cour");
            }else{
                os.println("partie non demarrer");
                for (int i=0;i<listConnexionAtt.size() ;i++ ) {
                    os=listConnexionAtt.get(i);
                    os.println(listConnexionAtt.size()+" Players connected");
                    os.flush();
                }

            }
            os.flush();
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection Error");
        }
    }

    public void run() {
    

    try {
        line=is.readLine();
        boolean stay = true;
        
        while(stay){

           //lancement de la parti en ligne de commande 

                if(line.compareTo("!start")==0){

                    //Permet d'avoit des joueur en file d'attente pour la prochaine partie 
                    if (!enJeu) {
                        listConnexion = (ArrayList<PrintWriter>)listConnexionAtt.clone(); 
                    }
                    
                    //Attente de minimum 2 joueurs
                    if(listConnexion.size()<=1){
                        //os=new PrintWriter(joueurs.get(0).getOutputStream());
                        os=listConnexionAtt.get(0);
                        os.println("You need to be at least 2 to play");
                        os.flush();
                    }
                    else{
                        /**
                         *  Distribution des cartes a tous les joueurs 
                         */
                        enJeu = true;
                        pot = 0;
                        Poker.CreerPaquet();
                        for (int k=0;k<listConnexion.size() ;k++ ) {

                            mainJ = Poker.Distribution();
                            listMain.add(mainJ);
                            
                            os=listConnexion.get(k);
                            os.println("You are player "+(k+1));
                            os.println("You have "+ listJetons.get(k)+" Coins");

                            //Affichage de la main de chaque joueurs 
                            os.println("-------------");
                            for (int i=0;i<mainJ.size() ;i++ ) {
                                os.println(mainJ.get(i).toString());
                            }
                            os.println("-------------");
                            os.flush();
                        }

                        /**
                         * Possibilité pour les joueurs de changer de carte (2 fois dans les vrai regle a implémeneter)
                         */
                        for (int p=1;p <= 2;p++ ) {
                            mise = 0;
                            for (int f=0;f<listConnexion.size() ;f++ ) {

                                //Donne le joueuer actif 
                                for(int w=0;w< listConnexion.size();w++){

                                    os=listConnexion.get(w);
                                    if(w!=f){
                                        os.println("it's the turn of player "+(f+1)+"/"+listConnexion.size());
                                    }else{

                                        os.println("it's your turn ("+p+"/2)");
                                        os.println("Pot : "+pot);
                                        os.println("Mise : "+mise);
                                        os.flush();
                                        is=listIsAtt.get(w);
                                        line=is.readLine();
                                        //BOUCLE POUR AVOIR UNE MISE VALIDE 
                                        while(line.compareTo("")==0 || line.matches("[a-zA-Z]+") || 
                                            Integer.parseInt(line)> listJetons.get(w) || Integer.parseInt(line)< mise){
                                            os.println("veuillez  entrer un entier / un montant <= a vos jetons / une mise >= ");
                                            os.flush();
                                            line=is.readLine();
                                        }
                                        if(Integer.parseInt(line) > mise){
                                            mise=Integer.parseInt(line);
                                        }
                                        listJetons.set(w, listJetons.get(w)- Integer.parseInt(line));
                                        pot +=  Integer.parseInt(line);
                                        os.println("Pot : "+pot);
                                        os.println("You have "+ listJetons.get(w)+" Coins");
                                        os.println("Change card : ");
                                        os.flush();

                                        
                                    }
                                    os.flush();
                                }

                                is=listIsAtt.get(f);
                                line=is.readLine();
                                os=listConnexion.get(f);

                                if(line.compareTo("")!=0 && !line.matches("[a-zA-Z]+")){
                                    
                                        String thisLine[] = line.split(";");
                                        if(thisLine.length>5){
                                            os.println("liste au dessus de 5 tu perd ton tour");
                                            thisLine = new String[0];
                                        }
                                        else{

                                            for (int m=0;m<thisLine.length ;m++ ) {
                                                if(Integer.parseInt(thisLine[m])>=5){
                                                    os.println(" >5 tu perd ton tour");
                                                    thisLine=new String[0];
                                                }
                                            }


                                            for (int q=0;q<thisLine.length ;q++ ) {
                                                for (int g=0;g<q ;g++ ) {
                                                    if(thisLine[g].equals(thisLine[q])){
                                                        os.println("doublon detecter tu perd ton tour");
                                                        thisLine=new String[0];
                                                    }
                                                    
                                                }
                                            }
                                        }

                                        for (int y=0;y < thisLine.length ;y++ ) {
                                            mainJ = listMain.get(f);
                                            mainJ = Poker.Change(mainJ,Integer.parseInt(thisLine[y]));
                                            listMain.set(f,mainJ);
                                        }
                                    
                                }

                                mainJ = listMain.get(f);
                                os.println("-------------");
                                for (int j=0;j<mainJ.size() ;j++ ) {
                                    os.println(mainJ.get(j).toString());
                                    
                                }
                                os.println("-------------");
                                os.flush();
                            }
                        }

                        //Ajoute toute les figure de tout les joueurs 
                        ArrayList<int[]> listClassement = new ArrayList<int[]>();
                        for (int y=0;y < listMain.size() ;y++ ) {
                            mainJ = listMain.get(y);
                            int classement[] = Poker.Classement(mainJ);
                            listClassement.add(classement);
                        }


                        ArrayList<int[]> gagnant = new ArrayList<int[]>();
                        int tmpGagnant[] ={-1,-1,-1,-1};
                        for (int t=0;t<listClassement.size() ;t++ ) {

                            //Si figure est plus grande
                            if(listClassement.get(t)[0]>tmpGagnant[1]){
                                tmpGagnant = new int[]{t,listClassement.get(t)[0],listClassement.get(t)[1],listClassement.get(t)[2]};
                                gagnant.clear();
                                gagnant.add(tmpGagnant);

                            //si figure égale
                            }else if (listClassement.get(t)[0]==tmpGagnant[1]) {

                                //si carte figure plus grande 
                                if(listClassement.get(t)[1]>tmpGagnant[2]){
                                    tmpGagnant = new int[]{t,listClassement.get(t)[0],listClassement.get(t)[1],listClassement.get(t)[2]};
                                    gagnant.clear();
                                    gagnant.add(tmpGagnant);
                                    //Si carte figure égale
                                }else if (listClassement.get(t)[1]==tmpGagnant[2]) {

                                    //Si carte haute plus forte
                                    if(listClassement.get(t)[2]>tmpGagnant[3]){
                                        tmpGagnant = new int[]{t,listClassement.get(t)[0],listClassement.get(t)[1],listClassement.get(t)[2]};
                                        gagnant.clear();
                                        gagnant.add(tmpGagnant);
                                    }//Si égalite 
                                    else if (listClassement.get(t)[2]==tmpGagnant[3]) {
                                        tmpGagnant = new int[]{t,listClassement.get(t)[0],listClassement.get(t)[1],listClassement.get(t)[2]};
                                        gagnant.add(tmpGagnant);
                                    }
                                }
                                
                            }
                        }


                        //Affiche tous les gagnant (possible égalité)
                        for (int u=0;u<gagnant.size() ;u++ ) {

                          System.out.println("le gagnant est le joueurs "+(gagnant.get(u)[0]+1)+" avec la figure "
                            +gagnant.get(u)[1]+" carte figure "+gagnant.get(u)[2]+" Plus grosse carte "+gagnant.get(u)[3]);


                          //Envoi le/les gagnant(s) a tout les joueurs
                            for(int w=0;w< listConnexion.size();w++){

                                os=listConnexion.get(w);
                                os.println("le gagnant est le joueurs "+(gagnant.get(u)[0]+1)+" avec la figure "
                                    +gagnant.get(u)[1]+" carte figure "+gagnant.get(u)[2]+" Plus grosse carte "+gagnant.get(u)[3]);
                                os.println("il gagne "+pot/gagnant.size()+" jetons");
                                os.flush();
                            }
                            listJetons.set(gagnant.get(u)[0],listJetons.get(gagnant.get(u)[0])+(pot/gagnant.size()));
                        }


                        enJeu=false;
                        for(int w=0;w< listConnexionAtt.size();w++){
                            os=listConnexionAtt.get(w);
                            if(listJetons.get(w)==0){
                                os.println("no more coins you will be disconected ");
                                listConnexionAtt.remove(w);
                                listIsAtt.remove(w);
                                listJetons.remove(w);
                                stay =false;
                                os.close();
                            }
                            else{

                                os.println("Fin de partie");
                                os.flush();
                            }

                        }
                        Poker.CreerPaquet();
                        listMain.clear();
                        gagnant.clear();

                    }

                }

                if(line.compareTo("!QUIT")==0 && !enJeu){
                    stay = false;
                        try{

                            for (int i=0;i<listConnexionAtt.size() ;i++ ) {
                                if(listConnexionAtt.get(i).equals(os)){
                                    listConnexionAtt.remove(i);
                                    listIsAtt.remove(i);
                                }
                            }
                            os.close();
                            System.out.println("NB de joueur : "+listConnexionAtt.size());
                        }catch (Exception e) {
                            System.out.println(e);
                        }
                }
                
            

            if(stay){
                line=is.readLine();
                System.out.println("Response to Client "+this.getName()+" :  "+line);
            }
        }
          
    } catch (IOException e) {

        line=this.getName(); //reused String line for getting thread name
        System.out.println("IO Error/ Client "+line+" terminated abruptly");
        e.printStackTrace();
    }
    catch(NullPointerException e){
        line=this.getName(); //reused String line for getting thread name
        System.out.println("Client "+line+" Closed");
    }

    finally{    
    try{
        enJeu =false;
        System.out.println("Connection Closing..");
        if (is!=null){
            is.close(); 
            System.out.println(" Socket Input Stream Closed");
        }

        if(!os.equals(null)){
            System.out.println(listConnexionAtt.size());

             for (int i=0;i<listConnexionAtt.size() ;i++ ) {
                if(listConnexionAtt.get(i).equals(os)){
                    listConnexionAtt.remove(i);
                    listIsAtt.remove(i);
                }
            }

            os.close();
            System.out.println(listConnexionAtt.size());
            System.out.println("Socket Out Closed");
        }
        if (s!=null){
        s.close();
        System.out.println("Socket Closed");
        }

    }
    catch(Exception ie){
        System.out.println("Socket Close Error");
    }
    }//end finally
    }
}