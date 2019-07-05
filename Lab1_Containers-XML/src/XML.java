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
    private static final int codeLength=9;
    public XML(String codefilename, String xml_binaryname,String xml_charname, String productfilename, String xml_prodname, String xml_pricename) throws ParserConfigurationException,SAXException,IOException{
        codeMap=this.getCodeMapFrom(codefilename,xml_binaryname,xml_charname);
        productMap=this.getProductMapFrom(productfilename, xml_prodname, xml_pricename);
 
        Iterator it = productMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
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
        String codeName = bSet.toString();
        String uncoded="";
        for(int i=0;i<codeName.length()/codeLength;i++){
            BitSet temp = fromString(codeName.substring(i*codeLength, (i+1)*codeLength));
            uncoded+=codeMap.get(temp);
        }
        
        return uncoded;
    }
    private float getPrice(BitSet bSet){
        return productMap.get(bSet);
    }
    private static BitSet fromString(String binary) {
        BitSet bitset = new BitSet(binary.length());
        int len = binary.length();
        for (int i = len-1; i >= 0; i--) {
            if (binary.charAt(i) == '1') {
                bitset.set(len-i-1);
            }
        }
        return bitset;
    }
    private void printReceipt(ArrayList<BitSet> cartList){
        float total = 0.0f;
        System.out.println("Printing Receipt...");
        for(BitSet i:cartList){
            System.out.println("Barcode Label: "+ i.toString());
            System.out.println("Product Name: "+this.deCodeProductName(i));
            System.out.println("Price: "+this.getPrice(i));
            System.out.println("----------------------");
            
            total+=this.getPrice(i);
        }
        
        System.out.println("\n\nTotal: "+total);
   
    }
    public void processCarts(String sfilename, String itemname) throws ParserConfigurationException,SAXException,IOException{
            
            XMLHandler xml = new XMLHandler();
            Document doc = xml.ReadXML(sfilename);
            NodeList nList = xml.GetNodes(doc, sfilename);
            
            for(int i=0;i<nList.getLength();i++){
                Node nNode=nList.item(i);
                ArrayList<BitSet> cartList=new ArrayList<BitSet>();
                if(nNode.getNodeType()==Node.ELEMENT_NODE){
                    NodeList innerList = nNode.getChildNodes();
                    for(int x=0;x<innerList.getLength();x++){
                        Node innerNode = innerList.item(x);
                        if(nNode.getNodeType()==Node.ELEMENT_NODE){
                            Element eElement = (Element) nNode;
                            cartList.add(BitSet.valueOf(eElement.getElementsByTagName("Item").item(0).getTextContent().getBytes()));
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