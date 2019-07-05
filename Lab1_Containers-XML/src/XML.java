import java.io.File;
import java.io.IOException;
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
    
    private String deCodeProductName(BitSet bSet){
        String codeName = toBinaryString(bSet);
        String uncoded="";
        for(int i=0;i<codeName.length()/CODE_LENGTH;i++){
            BitSet temp = fromString(codeName.substring(i*CODE_LENGTH, (i+1)*CODE_LENGTH));
            uncoded+=codeMap.get(temp);
        }
        
        return uncoded;
    }
    private float getPrice(BitSet bSet){
        try{
            return productMap.get(bSet);
        }
        catch (NullPointerException e){
            System.out.println("Sorry, we don't have that item in our database. Disregard it.");
            return 0.0f;
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
    private void printReceipt(ArrayList<BitSet> cartList){
        float total = 0.0f;
        float temp=0.0f;
        for(BitSet i:cartList){
            System.out.println("Barcode Label: "+ toBinaryString(i));
            System.out.println("Product Name: "+this.deCodeProductName(i));
            temp=this.getPrice(i);
            System.out.println("Price: "+temp);
            System.out.println("----------------------");
            
            total+=temp;
        }
        
        System.out.println("\n===========\nTotal: "+total+"\n===========\n");
   
    }
    public void processCarts(String sfilename, String itemname) throws ParserConfigurationException,SAXException,IOException{
            
            XMLHandler xml = new XMLHandler();
            Document doc = xml.ReadXML(sfilename);
            NodeList nList = xml.GetNodes(doc, "Cart");
            
            for(int i=0;i<nList.getLength();i++){
                Node nNode=nList.item(i);
                ArrayList<BitSet> cartList=new ArrayList<BitSet>();
                if(nNode.getNodeType()==Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    NodeList innerList = eElement.getElementsByTagName(itemname);
                    
                    for(int x=0;x<innerList.getLength();x++){
                        Node innerNode = innerList.item(x);
                        if(innerNode.getNodeType()==Node.ELEMENT_NODE){
                            Element innerElement = (Element) innerNode;
                            cartList.add(fromString(innerElement.getTextContent()));
                        }
                        
                    } 
                    System.out.println("Printing Receipt for Cart "+i+"...");
                    this.printReceipt(cartList);
                }
            }           
    }
public static void main(String[] args)
{
    String codeFile = "./src/Barcodes3of9.xml";
    String productFile = "./src/BCProducts.xml";
    String cartsFile = "./src/Carts.xml";
    try{
        XML test= new XML(codeFile, "Binary", "Character", productFile, "Barcode", "Price");
        test.processCarts(cartsFile, "item");
    }
    catch (ParserConfigurationException e) 
    {
        e.printStackTrace();
    } 
    catch (SAXException e) 
    {
        e.printStackTrace();
    } 
    catch (IOException e) 
    {
        e.printStackTrace();
    }
    }

}