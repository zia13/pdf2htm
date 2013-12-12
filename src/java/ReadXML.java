/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Rectangle;
import java.io.*;
import java.util.Collections;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import pdfreader.HtmlFileGen;

/**
 *
 * @author Zia
 */
public class ReadXML extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //Pages initialConfig = new Pages();
        try {
            try{
                Context env = (Context) new InitialContext().lookup("java:comp/env");
                String directory = (String) env.lookup("pdfSavingDirectory"); 
                String imgSavingDirectory = (String) env.lookup("picturePublishDirectory"); 
                String imgSavingURL = (String) env.lookup("picturePublishURI");
                Pages pages;
                
                String fileId = (String) request.getParameter("fileId");
                String projectId = (String)request.getParameter("projectId");
                String xmlContent = (String)request.getParameter("xmlContent");
                
                File file = new File("D:/xml before parse.xml");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(xmlContent);
                output.close();
                
                InputStream inputStream;
                JAXBContext jaxbContext = JAXBContext.newInstance(Pages.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                inputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
                pages = (Pages) jaxbUnmarshaller.unmarshal(inputStream);

                //                initialConfig = (Pages) jaxbUnmarshaller.unmarshal(inputStream);
//                System.out.println("Getting xml: "+ getXMLString(pages));
//                File file = new File("D:/xml before parse.xml");
//                BufferedWriter output = new BufferedWriter(new FileWriter(file));
//                output.write(xmlContent);
//                output.close();
                
                String pdfDirectory = directory.concat("p"+projectId+"_f"+fileId+".pdf");                
                HtmlFileGen htmlFileGen = new HtmlFileGen(pdfDirectory,imgSavingDirectory,imgSavingURL,projectId,fileId);
                Collections.sort(pages.getPages());
                List<Page> pageList = pages.getPages();
                for(int i =0;i<pageList.size();i++)
                {
                    Page p = pageList.get(i);
                    int pageNumber = p.getPageNumber()-1;  
//                    System.out.println("Page Number :"+pageNumber);
                    if(p.getRegions()!=null)
                    {
                        int noOfRegionsInPagei = p.getRegions().size();
                        Rectangle rec;                        
                        Collections.sort(p.getRegions());    
//                        System.out.println("Rec :");                     
                        for(int j =0; j< noOfRegionsInPagei;j++)
                        { 
//                            System.out.println("Region Number");
                            Region r = p.getRegions().get(j);
                            
                            if(r.getHtmlContent()==null || r.getHtmlContent().equals(""))
                            {
//                                System.out.println("HTML Content Null!!!!!!!!");
                                rec = new Rectangle(r.getX(),r.getY(),r.getWidth(),r.getHeight());
                                String s = null;
                                try{
                                     
//                                    System.out.println("Try");
                                    s = htmlFileGen.getHtmlContent(pageNumber, rec, r.getType());
//                                    System.out.println("Fail");
                                    s = StringEscapeUtils.escapeXml(s);
                                }       
                                catch(Exception ex){
                                    System.out.println("Exception occured in: ReadXML->processRequest:"+ ex.getMessage());
                                }
//                                out.write(s+"\n");
//                                byte[] bytesInUTF8 = s.getBytes("UTF-8");
//                                r.setHtmlContent(new String(bytesInUTF8, "UTF-8"));
                                r.setHtmlContent(s);
                            }
                        }
                    }
                }
                
//                System.out.println("Returning xml: "+ getXMLString(pages));
                out.write(getXMLString(pages));                
            }
            catch(Exception ex){
                out.println();
            }
        } finally {            
            out.close();
        }
    }
    
     /**
     *
     * @param pages
     * @return
     */
    
    public String getXMLString(Pages pages) {

        try {
            JAXBContext ctx = JAXBContext.newInstance(Pages.class);
            Marshaller marshaller = ctx.createMarshaller();

            StringWriter sw = new StringWriter();
            marshaller.marshal(pages, sw);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(sw.toString().getBytes("utf-8")));

            Source xmlSource = new DOMSource(document);
            StringWriter outWriter = new StringWriter();
            Result result = new StreamResult(outWriter);

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.transform(xmlSource, result);
            StringBuffer sb = outWriter.getBuffer();
            String finalstring = sb.toString();
            
            // Create a xml file in D: drive
            
            File file1 = new File("D:/xml after parse.xml");
            BufferedWriter output1 = new BufferedWriter(new FileWriter(file1));
            output1.write(StringEscapeUtils.unescapeXml(finalstring));
            output1.close();
            
            //end of XML file creation
            return finalstring;

        } catch (JAXBException | ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Region getSortedRegions(List<Region> regionsBeforeSort, int yPos) {
        for(int j =0; j< regionsBeforeSort.size();j++)
        {
            if(regionsBeforeSort.get(j).getY() == yPos)
                return regionsBeforeSort.get(j);
        }
        return null;
    }
}
