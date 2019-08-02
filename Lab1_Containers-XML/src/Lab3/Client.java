/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lab3;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.net.*;
import java.io.DataOutputStream;
import java.io.DataInputStream;
/**
 *
 * @author benja
 */
public class Client extends Thread{
    private Socket socket;
    private String cartsFile;
    private String identifier;
    private DataInputStream input;
    private DataOutputStream output;
    private final int BARCODE_LENGTH=45;
    public Client(String carts){
        socket=null;
        cartsFile=carts;
        input=null;
        output=null;
    }
    public float getPrice(BitSet bSet){
        String price=null;
        try{
            output.writeUTF("getPrice() "+toBinaryString(bSet));            
            price=input.readUTF();
            return Float.valueOf(price);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0.00f;
    }
    public String deCodeProductName(BitSet bSet){
        String name=null;
        try{
            output.writeUTF("deCodeProductName() "+toBinaryString(bSet));    
            name=input.readUTF();
        }
        catch(Exception e){
            
        }
        return name;
    }
    //Turns a Binary String into a BitSet Object
    private static BitSet fromString(String binary) {
        BitSet bitset = new BitSet(binary.length());
            for (int i = 0; i < binary.length(); i++) {
                if (binary.charAt(i) == '1') {
                    bitset.set(i);
                }
            }
            return bitset;
    }
    //Helps with BitSet to a Binary String format
    private static String toBinaryString(BitSet bSet){
            StringBuilder sBuild = new StringBuilder();
            
            for( int i = 0; i < bSet.length();  i++ )
            {
                sBuild.append( bSet.get( i ) == true ? 1: 0 );
            }
            for(int x=sBuild.length();x<45;x++){
                sBuild.append(0);
            }
            
            return sBuild.toString();
    }
    private void printReceipt(ArrayList<BitSet> cartList){
        //master caller for connection-based functions and gets the info
        //gather that information and orgnaizes it to print a reciept.
        float total = 0.0f;
        float temp=0.0f;
        DecimalFormat df = new DecimalFormat("0.00");
        for(BitSet i:cartList){
            System.out.println("Barcode Label: "+ toBinaryString(i));
            System.out.println("Product Name: "+this.deCodeProductName(i));
            temp=this.getPrice(i);
            System.out.println("Price: $"+df.format(temp));
            System.out.println("----------------------");
            
            total+=temp;
        }
        
        System.out.println("\n===========\nTotal: $"+df.format(total)+"\n===========\n");
   
    }
    public void processCarts(String sfilename){
        //use regex to parse carts.csv
        //two for loops, after second, make sure to call print receipt
        BufferedReader fileReader = null;
        final String DELIMITER = ",";
        try{
            String line="";
            fileReader=new BufferedReader(new FileReader(sfilename));
            fileReader.readLine(); //skip first line
            while((line=fileReader.readLine())!=null){
                String[] tokens = line.split(DELIMITER);
                tokens[tokens.length-1] = tokens[tokens.length-1].replace("<End>", "");
                ArrayList<BitSet> cart = new ArrayList<BitSet>(0);
                for(String token: tokens){
                    cart.add(fromString(token));
                }
                this.printReceipt(cart);
                fileReader.readLine(); //skip next line
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void connect(String portname, int port) throws Exception{
        this.socket= new Socket(portname,port);
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }
    public void endConnection() throws Exception{
        this.output.writeUTF("endConnection()");
        
        this.input.close();
        this.output.close();
        this.socket.close();
    }
    public void run(){
        //Tries to connect to database, then begins processing carts and printing receipts.
        try{
            this.connect("localhost",1234);
            this.processCarts(cartsFile);
            this.endConnection();
        }
        catch(Exception e){
            
        }
    }
    public static void main(String[] args){
        String codeFile = "./src/Lab3/Barcodes3of9.xml";
        String productFile = "./src/Lab3/BCProducts.xml";
        String cartsFile = "./src/Lab3/Carts.csv";
        try{
            Server database = new Server(codeFile, "Binary", "Character", productFile, "Barcode", "Price");
            Client lane = new Client(cartsFile);
            database.start();
            lane.start();
        }
        catch(Exception e){
            
        }
    }
}
