
import java.io.*;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import edgarhtmlcomponents.EDGARHTMLComponentCreationException;
/**
 * Servlet implementation class HomePage
 */
public class HomePage extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomePage() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        String fieldName = "N/A";
        String projectId = null;
        String fileId = null;
        
        boolean uploadStreamNull = false;
        
        PrintWriter out = response.getWriter();
        InputStream uploadedFileStream;
        String pdfSavingDirectory = "";
        String swfToolsDirectory = "";  
        try
        {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            pdfSavingDirectory = (String) env.lookup("pdfSavingDirectory");
            swfToolsDirectory = (String) env.lookup("swfToolInstallationDirectory");
        }
        catch(Exception ex)
        {
            
        }

        File pdfFile;
        try 
        {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) 
            {                
                if (item.isFormField()) 
                {                
                    fieldName = item.getFieldName();
                    if (item.getFieldName().equals("projectId")) 
                    {
                        projectId = item.getString();
                    }
                    else if (item.getFieldName().equals("fileId")) 
                    {
                        fileId = item.getString();
                    }
                }

                if (!item.isFormField()) 
                {
                    if (item.getFieldName().equals("myFile")) 
                    {
                        uploadedFileStream = item.getInputStream();
                        if (uploadedFileStream.available() == 0) 
                        {
                            uploadStreamNull = true;
                        }
                        try 
                        {
                            String fileUrl = pdfSavingDirectory.concat("\\\\p").concat(projectId).concat("_f").concat(fileId);
                            pdfFile = new File(fileUrl +".pdf");
                            item.write(pdfFile);
                            try 
                            {
                                Runtime rt = Runtime.getRuntime();                                
                                String command = "\""+swfToolsDirectory+"\\pdf2swf.exe\" \""+fileUrl+".pdf\" -o \""+fileUrl+".swf\"";
                                Process pr = rt.exec(command);
                                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));  
                                String line = null;
                                while ((line = input.readLine()) != null) 
                                {
                                    System.out.println(line);
                                }
                                int exitVal = pr.waitFor();
                                if (exitVal == 0) 
                                {
                                    out.println("Successfully Converted ");
                                    request.setAttribute("projectId", projectId);
                                    request.setAttribute("fileId", fileId);
                                    request.getRequestDispatcher("convertRegions.jsp").forward(request, response);  
//                                    out.println("<a href=\"convertRegion.jsp\">Convert to XML<>");
                                } 
                                else 
                                {
                                    out.println("Exited with error code " + exitVal);
                                }
                            }
                            catch (Exception e) 
                            {
                                out.println(e.getMessage());
                            }
                            
                        }
                        catch (Exception e) 
                        {
                            out.println(e.getMessage());
                        } 
                        out.close();
                    }
                }
            }
        } 
        catch (FileUploadException e) 
        {
            out.println(e.getMessage());
        }

    }

}