/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import htmltemplating.HTMLTemplateProcessor;
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

    private String paragraphStyle = null;
//            = "<paragraph_style>"
//            + "<paragraph text-align=\"justify\" margin=\"5px 5px 5px 5px\">"
//            + "<first_line indent=\"5px\"/>"
//            + "<hanging indent=\"6px\"/>"
//            + "<font color=\"#000000\" size=\"10pt\" family=\"Times New Roman\">"
//            + "<b>false</b>"
//            + "<i>false</i>"
//            + "<u>false</u>"
//            + "</font>"
//            + "</paragraph>"
//            + "</paragraph_style>";

    private String imageStyle = null;
//            = "<image_style>"
//            + "  <img align=\"center\" margin=\"3px 3px 3px 3px\" border=\"2px solid #ff0000\"/>"
//            + "</image_style>";

    private String listStyle = null;
//            = "<list_style>" 
//            + "  <list text-align=\"justify\" list-style-type=\"disc\">"
//            + "    <font size=\"16px\" family=\"Times New Roman\" color=\"#000000\">"
//            + "      <b>false</b>"
//            + "      <i>false</i>"
//            + "      <u>false</u>"
//            + "    </font>"
//            + "  </list>"
//            + "</list_style>";

    private String tableStyle = null;
//            = 
//            "<financial_table_style>"
//            + "  <global_styles>"
//            + "    <currency_column width=\"2\" align=\"Right\">true</currency_column>"
//            + "    <gutter_column width=\"2\" align=\"Left\">true</gutter_column>"
//            + "    <remove_empty_column>true</remove_empty_column>"
//            + "    <remove_empty_row>true</remove_empty_row>"
//            + "    <stub_column minWidth=\"40\"/>"
//            + "    <value_columns apply=\"true\">"
//            + "      <width_rule name=\"min_rule\">"
//            + "        <min_columns>-1</min_columns>"
//            + "        <max_columns>10</max_columns>"
//            + "        <width>10</width>"
//            + "      </width_rule>"
//            + "      <width_rule name=\"max_rule\">"
//            + "        <min_columns>5</min_columns>"
//            + "        <max_columns>-1</max_columns>"
//            + "        <width>8</width>"
//            + "      </width_rule>"
//            + "    </value_columns>"
//            + "  </global_styles>"
//            + "  <table>"
//            + "    <applyRule/>"
//            + "    <even_rows>"
//            + "      <even_cells align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </even_cells>"
//            + "      <odd_cells align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </odd_cells>"
//            + "      <stub_cell align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </stub_cell>"
//            + "    </even_rows>"
//            + "    <header_row>"
//            + "      <even_cells align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </even_cells>"
//            + "      <odd_cells align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </odd_cells>"
//            + "      <stub_cell align=\"left\" bgcolor=\"#ffffff\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </stub_cell>"
//            + "    </header_row>"
//            + "    <odd_rows>"
//            + "      <even_cells align=\"left\" bgcolor=\"#cc3300\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </even_cells>"
//            + "      <odd_cells align=\"left\" bgcolor=\"#cc3300\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </odd_cells>"
//            + "      <stub_cell align=\"left\" bgcolor=\"#cc3300\">"
//            + "        <font size=\"10px\" family=\"Times New Roman\" color=\"\">"
//            + "          <b>false</b>"
//            + "          <i>false</i>"
//            + "          <u>false</u>"
//            + "        </font>"
//            + "      </stub_cell>"
//            + "    </odd_rows>"
//            + "  </table>"
//            + "</financial_table_style>";

    private String xmlStyleTemplateContent = null;
    private final String styleTemplateID = "1";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //Pages initialConfig = new Pages();
        try {
            try {
                Context env = (Context) new InitialContext().lookup("java:comp/env");
                String directory = (String) env.lookup("pdfSavingDirectory");
                String imgSavingDirectory = (String) env.lookup("picturePublishDirectory");
                String imgSavingURL = (String) env.lookup("picturePublishURI");
                Pages pages;

                String fileId = (String) request.getParameter("fileId");
                String projectId = (String) request.getParameter("projectId");
                String xmlContent = (String) request.getParameter("xmlContent");
                tableStyle = (String) request.getParameter("defaultTableTemplateContent");
                paragraphStyle = (String) request.getParameter("defaultParagraphTemplateContent");
                listStyle = (String) request.getParameter("defaultListTemplateContent");
                imageStyle = (String) request.getParameter("defaultGraphicTemplateContent");

//                boolean useNewTemplateProcessor = true;
//                useNewTemplateProcessor = (String) request.getParameter("isNewsFile")!=null;
                File file = new File("D:/xml before parse.xml");
                File templates = new File("D:/templates.xml");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(xmlContent);
                output.close();
                BufferedWriter output1 = new BufferedWriter(new FileWriter(templates));
                output1.write(paragraphStyle + "\n" + tableStyle + "\n" + listStyle + "\n" + imageStyle);
                output1.close();

                //<editor-fold defaultstate="collapsed" desc="comment">
//// Works on 12-03-2014
//                String userId = (String) request.getParameter("userId");
//                String templateId = (String) request.getParameter("templateId");
//                String imgTemplatefileDirectory = "C://xpress_filer_docs//template-list//graphic//u"+userId+"_t"+templateId+".xml";
//                String paragraphTemplatefileDirectory = "C://xpress_filer_docs//template-list//paragraph//u"+userId+"_t"+templateId+".xml";
//                String listTemplatefileDirectory = "C://xpress_filer_docs//template-list//list//u"+userId+"_t"+templateId+".xml";
//                String tableTemplatefileDirectory = "C://xpress_filer_docs//template-list//table//u"+userId+"_t"+templateId+".xml";
////                File imgTemplatefile = new File(imgTemplatefileDirectory);
////                File paragraphTemplatefile = new File(paragraphTemplatefileDirectory);
////                File listTemplatefile = new File(listTemplatefileDirectory);
////                File tableTemplatefile = new File(tableTemplatefileDirectory);
////                BufferedReader br = new BufferedReader(new FileReader(imgTemplatefileDirectory));
//                BufferedReader br = null;
//                for (int i = 0; i < 4; i++) {
//                    String sCurrentLine;
//                        switch(i){
//                            case 1:
//                                try {
//                                br = new BufferedReader(new FileReader(imgTemplatefileDirectory));
//                                while ((sCurrentLine = br.readLine()) != null) {
//                                    System.out.println(sCurrentLine);
//                                }
//                                }catch(Exception e){}
//                                break;
//                            case 2:
//                                br = new BufferedReader(new FileReader(imgTemplatefileDirectory));
//                                while ((sCurrentLine = br.readLine()) != null) {
//                                    System.out.println(sCurrentLine);
//                                }
//                                break;
//                            case 3:
//                                br = new BufferedReader(new FileReader(imgTemplatefileDirectory));
//                                while ((sCurrentLine = br.readLine()) != null) {
//                                    System.out.println(sCurrentLine);
//                                }
//                                break;
//                            case 4:
//                                br = new BufferedReader(new FileReader(imgTemplatefileDirectory));
//                                while ((sCurrentLine = br.readLine()) != null) {
//                                    System.out.println(sCurrentLine);
//                                }
//                                break;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            if (br != null) {
//                                br.close();
//                            }
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
                //// Works on 12-03-2014
//</editor-fold>
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
                String pdfDirectory = directory.concat("p" + projectId + "_f" + fileId + ".pdf");
                HtmlFileGen htmlFileGen = new HtmlFileGen(pdfDirectory, imgSavingDirectory, imgSavingURL, projectId, fileId);
                Collections.sort(pages.getPages());
                List<Page> pageList = pages.getPages();
                for (int i = 0; i < pageList.size(); i++) {
                    Page p = pageList.get(i);
                    int pageNumber = p.getPageNumber() - 1;
//                    System.out.println("Page Number :"+pageNumber);
                    if (p.getRegions() != null) {
                        int noOfRegionsInPagei = p.getRegions().size();
                        Rectangle rec;
                        Collections.sort(p.getRegions());
//                        System.out.println("Rec :");                     
                        for (int j = 0; j < noOfRegionsInPagei; j++) {
//                            System.out.println("Region Number");
                            Region r = p.getRegions().get(j);

                            if (r.getHtmlContent() == null || r.getHtmlContent().equals("")) {
//                                System.out.println("HTML Content Null!!!!!!!!");
                                rec = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
                                String s = null;
                                try {

//                                    System.out.println("Try");
                                    s = htmlFileGen.getHtmlContent(pageNumber, rec, r.getType());
//                                    s = replacePTagByDiv(s);
                                    try {
                                        String tempNewContent = null;
                                        if (r.getType().equals("table") && (tableStyle != null && !"".equals(tableStyle))) {
                                            tempNewContent = changeFontSize(s);
                                        } else if ((r.getType().equals("paragraph") || r.getType().equals("text_with_line_break")) && paragraphStyle != null && !("".equals(paragraphStyle))) {
                                            tempNewContent = applyTemplate(s, r.getType());
                                            tempNewContent = tempNewContent.replaceAll("padding-left:0px;", "");
                                        } else if ((r.getType().equals("image")) && (imageStyle != null && !"".equals(imageStyle))) {
                                            tempNewContent = applyTemplate(s, r.getType());
                                        } else if ((r.getType().equals("list")) && (listStyle != null && !"".equals(listStyle))) {
                                            tempNewContent = applyTemplate(s, r.getType());
                                        }

                                        if (tempNewContent != null) {
                                            s = tempNewContent;
                                        }
                                        else if((r.getType().equals("paragraph") || r.getType().equals("text_with_line_break")))
                                            s = s.replaceAll("padding-left:0px;", "");
                                        
//                                        System.out.println(r.getType()+" after template: "+s);
                                    } catch (Exception ex) {
                                        System.out.println(ex.getMessage());
                                    }
//                                    //<editor-fold defaultstate="collapsed" desc="comment">
                                    //if (useNewTemplateProcessor) {
//                                        System.out.println(r.getType()+" before template: "+s);
//                                        try {
//                                            String tempNewContent;
//                                            if("table".equals(r.getType()))
//                                            {
//                                                tempNewContent = changeFontSize(s);
//                                            }
//                                            else{
//                                                tempNewContent = applyTemplate(s, r.getType());
//                                            }
//                                            s = tempNewContent;
//
//                                        System.out.println(r.getType()+" after template: "+s);
//
//                                        } catch (Exception ex) {
//                                            System.out.println(ex.getMessage());
//                                        }
//                                    }
//                                    System.out.println("Fail");
//</editor-fold>
                                    s = StringEscapeUtils.escapeXml(s);
                                } catch (Exception ex) {
                                    System.out.println("Exception occured in: ReadXML->processRequest:" + ex.getMessage());
                                }

//                                out.write(s+"\n");
//                                byte[] bytesInUTF8 = s.getBytes("UTF-8");
//                                r.setHtmlContent(new String(bytesInUTF8, "UTF-8"));
                                r.setHtmlContent(s);
                            }
                        }
                    }
                }
//                if(true)
//                {
//                    HTMLTemplateProcessor htmlAfterTemplating = new HTMLTemplateProcessor(inputStream, inputStream, xmlContent);
//                }
//                System.out.println("Returning xml: "+ getXMLString(pages));
                out.write(getXMLString(pages));
            } catch (Exception ex) {
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
            File fileAfterParse = new File("D:/xml after parse.xml");
            BufferedWriter outputAfterParse = new BufferedWriter(new FileWriter(fileAfterParse));
//            finalstring = StringEscapeUtils.unescapeXml(finalstring);
            outputAfterParse.write(StringEscapeUtils.unescapeXml(finalstring));
            outputAfterParse.close();

            //end of XML file creation
            return finalstring;

        } catch (JAXBException | ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
        for (int j = 0; j < regionsBeforeSort.size(); j++) {
            if (regionsBeforeSort.get(j).getY() == yPos) {
                return regionsBeforeSort.get(j);
            }
        }
        return null;
    }

    private String applyTemplate(String content, String type) throws IOException {
        String newContent = null;
        switch (type) {
            case "paragraph":
            case "text_with_line_break":
                xmlStyleTemplateContent = paragraphStyle;
                break;
            case "list":
                xmlStyleTemplateContent = listStyle;
                content = replacePTagByDiv(content);
                break;
            case "image":
                xmlStyleTemplateContent = imageStyle;
                break;
        }
//        xmlStyleTemplateContent = "<all>".concat(pStyle).concat(iStyle).concat(tableStyle).concat("</all>");
        try {
            InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
            InputStream xmlUploadedFileStream = new ByteArrayInputStream(xmlStyleTemplateContent.getBytes("UTF-8"));
            HTMLTemplateProcessor hTMLTemplateProcessor = new HTMLTemplateProcessor(is,
                    xmlUploadedFileStream, styleTemplateID);
            newContent = hTMLTemplateProcessor.processedHTML();
            xmlUploadedFileStream.close();
            is.close();

        } catch (UnsupportedEncodingException e) {
        }
        return newContent;
    }

    private String replacePTagByDiv(String content) {
        String newCont = content.replace("<p", "<div").replace("</p>", "</div>");
        return newCont;
    }

    private String changeFontSize(String content) {
        String newContent, tempContent;
        content = replacePTagByDiv(content);
        tempContent = content.replaceAll("font-size:\\s[0-9]*[p][x][;]", "");
        newContent = tempContent.replace("style=\"border", "style=\"font-size: 10pt; border");
        tempContent = newContent.replaceFirst("margin-left:[-+]?[0-9]*[p][x][;]", "");
        newContent = tempContent;
//        System.out.println("Changed Font Size of table: "+newContent);
        return newContent;
    }
}
