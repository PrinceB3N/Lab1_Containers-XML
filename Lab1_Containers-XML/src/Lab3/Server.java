/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lab3;

import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
import java.util.TreeSet;
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
import java.util.concurrent.*;
import java.net.*;
/**
 *
 * @author benja
 */
public class Server extends Thread{
    class Code implements Comparable{
        final public BitSet sequence;
        final public String character;
        public Code(BitSet b,String c){
            sequence=b;
            character=c;
        }
        public int compareTo(Object c2){
            return this.sequence.toString().compareTo(((Code)c2).sequence.toString());
        }
    }
    class Product implements Comparable{
        final public BitSet barcode;
        final public float price;
        public Product(BitSet b,float p){
            barcode=b;
            price=p;
        }
        public int compareTo(Object c2){
            return this.barcode.toString().compareTo(((Product)c2).barcode.toString());
        }        
    }
    private TreeSet<Code> codeList;
    private TreeSet<Product> productList;
    private static final int CODE_LENGTH=9;
    private static final int BARCODE_LENGTH=45;
    private ServerSocket server;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    public Server(String codefilename, String xml_binaryname,String xml_charname, String productfilename, String xml_prodname, String xml_pricename) throws ParserConfigurationException,SAXException,IOException{
        codeList=this.getCodesFrom(codefilename,xml_binaryname,xml_charname);
        productList=this.getProductsFrom(productfilename, xml_prodname, xml_pricename);
    }
    //Fills up database with encryption patterns for barcodes
    private TreeSet<Code> getCodesFrom(String sfilename,String binary,String character) throws ParserConfigurationException,SAXException,IOException{
        TreeSet<Code> codes=new TreeSet<Code>();
        XMLHandler xml = new XMLHandler();
        Document doc = xml.ReadXML(sfilename);
        NodeList nList = xml.GetNodes(doc, "Symbol");
        
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  fromString(eElement.getElementsByTagName(binary).item(0).getTextContent());
                String c = (eElement.getElementsByTagName(character).item(0).getTextContent());
                codes.add(new Code(bSet,c));
                    
            }
        }
        return codes;
    }
    //Fills up the database for parsing out available Products
    private TreeSet<Product> getProductsFrom(String sfilename,String prodname,String pricename) throws ParserConfigurationException,SAXException,IOException{
        TreeSet<Product> products=new TreeSet<Product>();
        XMLHandler xml = new XMLHandler();
        Document doc = xml.ReadXML(sfilename);
        NodeList nList = xml.GetNodes(doc, "Product");
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  fromString(eElement.getElementsByTagName(prodname).item(0).getTextContent());
                Float f = Float.parseFloat(eElement.getElementsByTagName(pricename).item(0).getTextContent());
                products.add(new Product(bSet,f));
            }
        }

        return products;
    }
    //searches for a specific Product in the database
    private static Product searchForProduct(TreeSet<Product> treeset, BitSet key){
       Iterator<Product> iter = treeset.iterator();
       for(int i=0;i<treeset.size();i++){
           Product temp = iter.next();
           if(temp.barcode.equals(key)){
               return temp;
           }
       }
       return null;
    }
    //Searches for a speific code in the database
    private static Code searchForCode(TreeSet<Code> treeset, BitSet key){
        Iterator<Code> iter = treeset.iterator();
        for(int i=0;i<treeset.size();i++){
            Code temp = iter.next();
            if(temp.sequence.equals(key)){
                return temp;
            }
        }
        return null;
    }
    //Decodes Barcode to return the Product's name in database
    private String deCodeProductName(BitSet bSet){
        String codeName = toBinaryString(bSet);
        String uncoded="";
        try{
        for(int i=0;i<codeName.length()/CODE_LENGTH;i++){
            BitSet temp = fromString(codeName.substring(i*CODE_LENGTH, (i+1)*CODE_LENGTH));
            uncoded+=searchForCode(codeList,temp).character;
        }
        }
        catch(NullPointerException e){
            System.out.println(bSet.toString());
            System.out.println("\n\n\n\n");
             System.out.println("\n\n\n\n");
              System.out.println("\n\n\n\n");
               System.out.println("\n\n\n\n");
        }
        return uncoded;
    }
    //Searches database for respective barcode, then returns price of that Product
    private float getPrice(BitSet bSet){
        try{
            return searchForProduct(productList, bSet).price;
        }
        catch (NullPointerException e){
            System.out.println("Sorry, we don't have that item in our database. Disregard it.");
            return 0.00f;
        }
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
            for(int x=sBuild.length();x<BARCODE_LENGTH;x++){
                sBuild.append(0);
            }
            
            return sBuild.toString();
    }
    public void startServer() throws Exception{
    //Initializes all server eonnection,io capabilities to start listening    
        server=new ServerSocket(1234);
        socket=server.accept();
        System.out.println("Connection established!");
        input=new DataInputStream(socket.getInputStream());
        output=new DataOutputStream(socket.getOutputStream());
        
    }
    public void endServer() throws Exception{
        this.output.close();
        this.input.close();
        this.socket.close();
        this.server.close();
    }
    public void processRequests(){
        String message=null;
        while(true){
            try{
                message = input.readUTF();

                if(message.contains("getPrice()")){
                    output.writeUTF(Float.toString(this.getPrice(fromString(message.substring(11)))));
                }
                else if(message.contains("deCodeProductName()")){
                    output.writeUTF(this.deCodeProductName(fromString(message.substring(20))));
                }
                else if(message.contains("endConnection()")){
                    this.endServer();
                    break;
                }
                else{
                
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            message=null;
        }
    }
    public void run(){
        //Starts up server 
        try{
            this.startServer();
            this.processRequests();
        }
        catch(Exception e){
            
        }
    }
}
