import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {

   public static void main(String[] args) {
      
      final Socket clientSocket;
      final BufferedReader in;
      final PrintWriter out;
      final Scanner sc = new Scanner(System.in);//pour lire à partir du clavier
  
      try {
         /*
         * les informations du serveur ( port et adresse IP ou nom d'hote
         * 127.0.0.1 est l'adresse local de la machine
         */
         clientSocket = new Socket("127.0.0.1",5000);
   
         //flux pour envoyer
         out = new PrintWriter(clientSocket.getOutputStream());
         //flux pour recevoir
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   
         Thread envoyer = new Thread(new Runnable() {
             String msg;
             boolean stay = true;
              @Override
              public void run() {
                while(stay){
                  msg = sc.nextLine();
                  if(msg.compareTo("!QUIT")!=0){
                    out.println(msg);
                    out.flush();
                  }else {
                    out.println(msg);
                    out.flush();
                    stay = false;
                  }
                }
                try{
                  in.close();
                  out.close();
                  clientSocket.close();
                }catch (Exception e) {
                  e.printStackTrace();
                }
             }
         });
         envoyer.start();
   
        Thread recevoir = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
               try {
                 boolean stay = true; 
                 msg = in.readLine();
                 while(msg!=null){
                    System.out.println("Serveur : "+msg);
                    msg = in.readLine();
                 }
                 System.out.println("Serveur déconecté");
                 out.close();
                 clientSocket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
            }
        });
        recevoir.start();
   
      } catch (IOException e) {
           e.printStackTrace();
      }
  }
}