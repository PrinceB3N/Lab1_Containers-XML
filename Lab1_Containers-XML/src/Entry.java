import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Entry 
{
    static void TestXML(XMLHandler xml, String sfile, String selement, String[] sfields)
    {
            try 
            {
                    xml.TestXML(sfile, selement, sfields);
            } 
            catch (ParserConfigurationException e) 
            {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } 
            catch (SAXException e) 
            {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } 
            catch (IOException e) 
            {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
    }

    public static void main(String[] args) 
    {
        String sfilename = "Passengers.XML";
        String snode = "Passenger";
        String[] sfields = new String[]{"Row","Seat","Name"};

        try
        {
                // Non-static Version
                XMLHandler xml = new XMLHandler();
                xml.TestXML(sfilename,snode,sfields);

                // Static Version
                TestXML(xml,sfilename,snode,sfields);
        } 
        catch (ParserConfigurationException e) 
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } 
        catch (SAXException e) 
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } 
        catch (IOException e) 
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        finally
        {
        }
    }
}
