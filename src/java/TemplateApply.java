
import htmltemplating.HTMLTemplateProcessor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zia
 */
public class TemplateApply {
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        String newContent = null;
        
        try {
            InputStream is = new FileInputStream(new File("D://input.htm"));
            //InputStream is = new ByteArrayInputStream(new FileInputStream(f));
            InputStream xmlUploadedFileStream = new FileInputStream(new File("D://style1.xml"));
            if (true) {
                HTMLTemplateProcessor hTMLTemplateProcessor = new HTMLTemplateProcessor(is,
                        xmlUploadedFileStream, "1");
                newContent = hTMLTemplateProcessor.processedHTML();
                xmlUploadedFileStream.close();
                is.close();
            } else {
                //newContent = content;
            }
            System.out.println(newContent);
        } catch (UnsupportedEncodingException e) {
        }
    }
    private String replacePTagByDiv(String content){
        String newCont = content.replace("<p", "<div").replace("</p>", "</div>");
        return newCont;        
    }
    
}
