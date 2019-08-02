/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exercise4;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.*;
/**
 *
 * @author benja
 */
public class Exercise4 {
    class Server extends Thread{
        private ServerSocket server;
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream ps;
        
        Server() throws Exception{
            server=null;
            socket=null;
            dis=null;
            ps=null;
        }
        public void returnPing() throws Exception{
            server=new ServerSocket(1234);
            socket=server.accept();
            System.out.println("Connected!");
            dis = new DataInputStream(socket.getInputStream());
            ps = new DataOutputStream(socket.getOutputStream());
            String str = null;
            
            str=dis.readUTF();
            
            System.out.println("Server received message!");
            if(str.equals("Pinging..."))
                ps.writeUTF("Hello");
            else
                System.out.println("Not an expected message.");
            socket.close();
            server.close();
            
        }
        public void run(){
            try{
                this.returnPing();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    class Client extends Thread{
        private Socket t;
        private DataInputStream dis;
        private DataOutputStream ps;
        Client() throws Exception{
            t=new Socket("localhost",1234);
            dis = new DataInputStream(t.getInputStream());
            ps = new DataOutputStream(t.getOutputStream());
                      
        }
        void pingServer() throws Exception{
            ps.writeUTF("Pinging...");
            String str = dis.readUTF();      
            if (str.equals("Hello"))
                System.out.println("Server responded!");
            else
                System.out.println("No response from server.");
            t.close();              
        }
        public void run(){
            try{
                this.pingServer();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
}
    public void run(){
        try{
        Server server = new Server();
        server.start();
        Client client = new Client();
        client.start();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Exercise4 ex = new Exercise4();
        ex.run();
        
    }     
}

