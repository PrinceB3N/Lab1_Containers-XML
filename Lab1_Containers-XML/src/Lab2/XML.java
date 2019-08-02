package Lab2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
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

public class XML {
    private HashMap<BitSet,String> codeMap;
    private HashMap<BitSet,Float> productMap;
    private static final int CODE_LENGTH=9;
    private static final int BARCODE_LENGTH=45;
    
    public XML(String codefilename, String xml_binaryname,String xml_charname, String productfilename, String xml_prodname, String xml_pricename) throws ParserConfigurationException,SAXException,IOException{
        codeMap=this.getCodeMapFrom(codefilename,xml_binaryname,xml_charname);
        productMap=this.getProductMapFrom(productfilename, xml_prodname, xml_pricename);
    }
    private HashMap<BitSet,String> getCodeMapFrom(String sfilename,String binary,String character) throws ParserConfigurationException,SAXException,IOException{
        HashMap<BitSet,String> hSet=new HashMap<BitSet,String>();
        XMLHandler xml = new XMLHandler();
        Document doc = xml.ReadXML(sfilename);
        NodeList nList = xml.GetNodes(doc, "Symbol");
        
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  fromString(eElement.getElementsByTagName(binary).item(0).getTextContent());
                String c = (eElement.getElementsByTagName(character).item(0).getTextContent());
                hSet.put(bSet,c);
                    
            }
        }
 
        return hSet; 
    }
    private HashMap<BitSet,Float> getProductMapFrom(String sfilename,String prodname,String pricename) throws ParserConfigurationException,SAXException,IOException{
        HashMap<BitSet,Float> hSet=new HashMap<BitSet,Float>();
        XMLHandler xml = new XMLHandler();
        Document doc = xml.ReadXML(sfilename);
        NodeList nList = xml.GetNodes(doc, "Product");
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  fromString(eElement.getElementsByTagName(prodname).item(0).getTextContent());
                Float f = Float.parseFloat(eElement.getElementsByTagName(pricename).item(0).getTextContent());
                if(!hSet.containsKey(bSet)){
                    hSet.put(bSet,f);
                }
            }
        }

        return hSet;
    }
    
    private synchronized String deCodeProductName(BitSet bSet){
        String codeName = toBinaryString(bSet);
        String uncoded="";
        for(int i=0;i<codeName.length()/CODE_LENGTH;i++){
            BitSet temp = fromString(codeName.substring(i*CODE_LENGTH, (i+1)*CODE_LENGTH));
            uncoded+=codeMap.get(temp);
        }
        
        return uncoded;
    }
    private synchronized float getPrice(BitSet bSet){
        try{
            return productMap.get(bSet);
        }
        catch (NullPointerException e){
            return 0.00f;
        }
    }
    private static BitSet fromString(String binary) {
        BitSet bitset = new BitSet(binary.length());
            for (int i = 0; i < binary.length(); i++) {
                if (binary.charAt(i) == '1') {
                    bitset.set(i);
                }
            }
            return bitset;
    }
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
    private synchronized void printReceipt(StringBuilder sBuild){
        System.out.println(sBuild.toString());
    }
    private void processReceipt(ArrayList<BitSet> cartList){
        float total = 0.00f;
        float temp;
        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sBuild = new StringBuilder();
        
        sBuild.append("Now printing receipt...\n");
        for(BitSet i:cartList){
            sBuild.append("Barcode Label: "+ toBinaryString(i)+"\n");
            sBuild.append("Product Name: "+this.deCodeProductName(i)+"\n");
            temp=this.getPrice(i);
            sBuild.append("Price: $"+df.format(temp)+"\n");
            
            if(temp==0.0f){
                sBuild.append("Sorry, we don't have that product in our database. Please disregard it.\n");
            }
            sBuild.append("----------------------\n");
            
            total+=temp;
        }
        
        sBuild.append("\n===========\nTotal: $"+df.format(total)+"\n===========\n");
        //Finally print entire receipt in sync with other threads
        printReceipt(sBuild);
    }

    public void processCartsThreads(String sfilename, String itemname, int numlanes) throws ParserConfigurationException,SAXException,IOException{
            
            //Stores items
            class Cart{
                private ArrayList<BitSet> items;
                Cart(){
                    items=new ArrayList<BitSet>();
                }
                public void add(BitSet item){
                    items.add(item);
                }
                public ArrayList<BitSet> getItems(){
                    return items;
                }
            }   
            //Storing thread-sharing resources
            class ValContainer{
                int numcarts=0;
                boolean cartsLeft=true;
                Queue cartGateLine = new LinkedList<Cart>();               
                int getnumcarts(){
                    return numcarts;
                }
                boolean areCartsLeft(){
                    return (numcarts==0 && cartsLeft==false) ? (false) : (true);
                }
                synchronized void setCartsLeft(boolean bool){
                    cartsLeft=bool;
                }
                synchronized void addCartToGate(Cart c){
                    cartGateLine.add(c);
                    numcarts++;
                }
                synchronized Cart moveCartToLine(){
                    if(numcarts==0){
                        return null;
                    }
                    else{
                        numcarts--;
                        return (Cart)cartGateLine.remove();
                    }
                }
                
            }            
            
            XMLHandler xml = new XMLHandler();
            Document doc = xml.ReadXML(sfilename);
            NodeList nList = xml.GetNodes(doc, "Cart");           
            ValContainer values = new ValContainer();
            //inner class CartLane(consumer)
            class CartLane extends Thread{
                private Queue<Cart> cartQueue;
                public CartLane(){
                    cartQueue=new LinkedList<Cart>();
                }
                public void addtoCartLane(Cart c){
                    cartQueue.add(c);                    
                }
                @Override
                public void run(){
                    while(values.areCartsLeft() || cartQueue.isEmpty()==false){
                        if(cartQueue.isEmpty()==false){
                            try{
                                processReceipt(cartQueue.remove().getItems());
                            }
                            catch(Exception e){
                                
                            }
                        }
                        else{
                            
                        }
                    }
                }
              
            }
            //inner class CartGate(producer)
            class CartGate extends Thread{
                @Override
                public void run(){
                    for(int i=0;i<nList.getLength();i++){
                        Node nNode=nList.item(i);
                        Cart cart=new Cart();
                        if(nNode.getNodeType()==Node.ELEMENT_NODE){
                            Element eElement = (Element) nNode;
                            NodeList innerList = eElement.getElementsByTagName(itemname);

                            for(int x=0;x<innerList.getLength();x++){
                                Node innerNode = innerList.item(x);
                                if(innerNode.getNodeType()==Node.ELEMENT_NODE){
                                    Element innerElement = (Element) innerNode;
                                    cart.add(fromString(innerElement.getTextContent()));
                                }

                            } 
                            values.addCartToGate(cart);
                        }
                    } 
                    values.setCartsLeft(false);
                }
            }     
            
            //Start of threads
            ArrayList<CartLane> lanes = new ArrayList<CartLane>();
            CartGate cg = new CartGate();
            cg.start();
            for(int i=0;i<numlanes;i++){
                lanes.add(new CartLane());
                lanes.get(i).start();
            }
            //Main thread - delegates Carts to each CartLane thread using a modulo iteration
            for(int x=0;x<numlanes;x=(x+1)%numlanes){
                if(!values.areCartsLeft()){
                    break;
                }
                Cart tmp = values.moveCartToLine();
                if(tmp!=null){
                    lanes.get(x).addtoCartLane(tmp);
                }

            }
            //Joins with each thread until each is finished.
            //Producer thread
            try{
                cg.join();
            }
            catch(Exception e){
                
            }
            //Consumer threads
            for(int j=0;j<numlanes;j++){
                try{
                    lanes.get(j).join();
                }
                catch(Exception e){
                    
                }
            }
    }    

public static void main(String[] args)
{
    String codeFile = "./src/Lab2/Barcodes3of9.xml";
    String productFile = "./src/Lab2/BCProducts.xml";
    String cartsFile = "./src/Lab2/Carts.xml";
    int lanes_open = 10;
    try{
        XML test= new XML(codeFile, "Binary", "Character", productFile, "Barcode", "Price");
        test.processCartsThreads(cartsFile, "item",lanes_open);
    }
    catch (Exception e){
        e.printStackTrace();
    }
    }

}