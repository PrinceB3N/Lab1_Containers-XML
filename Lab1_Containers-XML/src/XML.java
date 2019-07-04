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
    
    public XML(String codefilename, String xml_binaryname,String xml_charname, String productfilename, String xml_prodname, String xml_pricename) throws ParserConfigurationException,SAXException,IOException{
        codeMap=this.getCodeMapFrom(codefilename,xml_binaryname,xml_charname);
        productMap=this.getProductMapFrom(productfilename, xml_prodname, xml_pricename);
    }
    private HashMap<BitSet,String> getCodeMapFrom(String sfilename,String binary,String character) throws ParserConfigurationException,SAXException,IOException{
        HashMap<BitSet,String> hSet=new HashMap<BitSet,String>();
        XMLHandler xml = new XMLHandler();
        Document doc = xml.ReadXML(sfilename);
        NodeList nList = xml.GetNodes(doc, sfilename);
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  BitSet.valueOf(eElement.getElementsByTagName(binary).item(0).getTextContent().getBytes());
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
        NodeList nList = xml.GetNodes(doc, sfilename);
        for(int i=0;i<nList.getLength();i++){
            Node nNode=nList.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                BitSet bSet =  BitSet.valueOf(eElement.getElementsByTagName(prodname).item(0).getTextContent().getBytes());
                Float f = Float.parseFloat(eElement.getElementsByTagName(pricename).item(0).getTextContent());
                if(!hSet.containsKey(bSet)){
                    hSet.put(bSet,f);
                }
            }
        }

        return hSet;
    }
    private void printReceipt(ArrayList<BitSet> cartList){
        
    }
    public void processCart(String sfilename, String codesfilename, String productsfilename, String itemname) throws ParserConfigurationException,SAXException,IOException{
            
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
                            cartList.add(BitSet.valueOf(eElement.geitem(0).getTextContent().getBytes()));
                        }
                        
                    }
                    
                    this.printReceipt(cartList);
                }
            }
            
               
    }
public static void main(String[] args)
{
    String sfilename = "Passengers.XML";
    String snode = "Passenger";
    String[] sfields = new String[]{"Row","Seat","Name"};
    XMLHandler xml;
    Document doc;
    try{
        xml = new XMLHandler();
        doc = xml.ReadXML(sfilename);
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