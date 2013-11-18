
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Document;
import pdfreader.RegionSetter;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zia
 */
public class ParseXML {
    Document doc;
    /**
     *
     */
    public ParseXML() 
    {        
        try{
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String directory = (String) env.lookup("pdfSavingDirectory"); //Set the Project ID and File ID here
            String pdfDirectory = directory.concat("form10q.pdf");
            String xmlDirectory = directory.concat("form10q.xml");
            String htmlDirectory = directory.concat("form10q.htm");
            System.out.println("XML Parsing Start.....");
            File stocks = new File(xmlDirectory);
            InputStream inputStream = new FileInputStream(stocks);
            JAXBContext jaxbContext = JAXBContext.newInstance(Pages.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Pages pages = (Pages) jaxbUnmarshaller.unmarshal(inputStream);
//            RegionSetter rs = new RegionSetter(pdfDirectory);
            System.out.println("PDF File :"+pdfDirectory);
            List<Page> pageList = pages.getPages();
            System.out.println("No. Of Pages :"+pageList.size());
            for(int i =0;i<pageList.size();i++)
            {
                Page p = pageList.get(i);
                int pageNumber = p.getPageNumber();
                System.out.println("\tPage Number :"+pageNumber);
               
                System.out.print("\t\tNo. Of Regions :");
                int noOfRegionsInPagei = p.getRegions().size();
                System.out.println(noOfRegionsInPagei);
                
                Rectangle rec;
                for(int j =0; j< noOfRegionsInPagei;j++)
                {
                    Region r = p.getRegions().get(j);
                     rec = new Rectangle(r.getX(),r.getY(),r.getWidth(),r.getHeight());
//                    rs.setTaggedRegionAtList(pageNumber, rec, r.getType());
                    System.out.println("\t\t\tRegions"+ j+" : Rectangle :"+rec+" Type :"+r.getType());
                    
                }
            }
//            rs.extractAllRegions(htmlDirectory);
        }
        catch(Exception ex){
            
        }
        //File stocks = new File("C:\\Users\\riyadn\\Documents\\NetBeansProjects\\PDF2HTM\\src\\java\\Regions.xml");
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder;
//        try
//        {
//            dBuilder = dbFactory.newDocumentBuilder();
//            doc = dBuilder.parse(stocks); 
//            doc.getDocumentElement().normalize();            
//        }
//        catch(Exception ex)
//        {
//            
//        }
        //System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
    }

    
    /**
     *
     * @param tag
     */
    public void getByTagName(String tag)
    {
//        NodeList nodes = doc.getElementsByTagName(tag);
//        List<Page> pageList = new ArrayList<Page>();
//        
//        for (int i = 0; i < nodes.getLength(); i++) 
//        {
//            Page p = new Page();
//            Node node = nodes.item(i);
//            if (node.getNodeType() == Node.ELEMENT_NODE) 
//            {
//                Element element = (Element) node;
//                p.setPageNumber(Integer.parseInt(element.getAttribute("no")));
//                String pageID = element.getAttribute("no");
//                System.out.println("Page No."+pageID);
//                NodeList regionNodes = element.getElementsByTagName("Region");
//                List<Region> regionList = new ArrayList<Region>();
//                
//                for(int j = 0; j<regionNodes.getLength();j++)
//                {
//                    System.out.println("\tRegion :"+(j+1));
//                    Region r = new Region();
//                    Node n = regionNodes.item(j);
//                    if(n.getNodeType() == Node.ELEMENT_NODE)
//                    {
//                        Element er = (Element)n;
//                        r.setX(Integer.parseInt(er.getAttribute("x")));
//                        r.setY(Integer.parseInt(er.getAttribute("y")));
//                        r.setWidth(Integer.parseInt(er.getAttribute("width")));
//                        r.setHeight(Integer.parseInt(er.getAttribute("height")));
//                        r.setType(er.getAttribute("type"));
//                        r.setHtmlContent(er.getElementsByTagName("HtmlContent").item(0).toString());
//                        System.out.println("X :"+er.getAttribute("x")+", Y :"+er.getAttribute("y")+" Width :"+er.getAttribute("width")+" Height :"er.getAttribute("height");
//                    }
//                    regionList.add(r);
//                }
//                p.setRegions(regionList);
//            }
//            pageList.add(p);
//        }        
    }
    
    
    
    /**
     *
     * @param args
     */
    public static void main(String[] args)
    {
        ParseXML pxml = new ParseXML();
//        pxml.getByTagName("Page");
    }
}
