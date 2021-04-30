

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;


public class Carte implements Comparable<Carte>{

    private  String couleur;
    private  String point;
    private  int poid;

    public Carte(String couleur,String point){
        this.couleur = couleur;
        this.point = point;
        if(point.compareTo("A")==0)
            this.poid = 14;
        else if (point.compareTo("K")==0) {
            this.poid = 13;
        }else if (point.compareTo("Q")==0) {
            this.poid=12;
        }else if (point.compareTo("J")==0) {
            this.poid = 11;
        }else{
            this.poid = Integer.parseInt(point);
        }
    }

    @Override
    public int compareTo(Carte c) {
        return (this.poid - c.getPoid());
       //return Integer.compare(poid, c.getPoid());
    }

    public String getCouleur(){
        return this.couleur;
    }

    public String getPoint(){
        return this.point;
    }

    public int getPoid(){
        return this.poid;
    }

    public String toString(){
        return "["+this.point+"-"+this.couleur+"]";
    }
}