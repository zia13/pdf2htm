package htmltemplating;

import htmltemplating.image.ImageTemplate;
import htmltemplating.image.ImageTemplateProcessor;
import htmltemplating.list.ListTemplate;
import htmltemplating.list.ListTemplateProcessor;
import htmltemplating.paragraph.ParagraphTemplate;
import htmltemplating.paragraph.ParagraphTemplateProcessor;
import htmltemplating.table.TableTemplate;
import htmltemplating.table.TableTemplateProcessor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StyleTemplateProcessorFactory
{
	@Deprecated
	public static StyleTemplateProcessor getStyleTemplateProcessor(String templateXML, int styleTemplateId)
	{
		try
		{
			DOMParser domParser = new DOMParser();
			InputSource inputSource = new InputSource(new StringReader(templateXML));
			domParser.parse(inputSource);
			Document document = domParser.getDocument();
			
			if (document.getElementsByTagName(StyleTemplateType.TABLE.templateXMLTag()).getLength() > 0)
			{
				TableTemplate tableTemplate = new TableTemplate(templateXML, styleTemplateId);
				return new TableTemplateProcessor(tableTemplate);
			}
			else if (document.getElementsByTagName(StyleTemplateType.PARAGRAPH.templateXMLTag()).getLength() > 0)
			{
				ParagraphTemplate paragraphTemplate = new ParagraphTemplate(templateXML, styleTemplateId);
				return new ParagraphTemplateProcessor(paragraphTemplate);
			}
			else if (document.getElementsByTagName(StyleTemplateType.LIST.templateXMLTag()).getLength() > 0)
			{
				ListTemplate listTemplate = new ListTemplate(templateXML, styleTemplateId);
				return new ListTemplateProcessor(listTemplate);
			}
		}
		catch (SAXException | IOException e)
		{
			//e.printStackTrace();
		}
		return null;

	}
	
	public static ArrayList<StyleTemplateProcessor> getStyleTemplateProcessors(String templateXML, int styleTemplateId)
	{
		ArrayList<StyleTemplateProcessor> styleTemplateProcessors = new ArrayList<StyleTemplateProcessor>();
		try
		{
			DOMParser domParser = new DOMParser();
			InputSource inputSource = new InputSource(new StringReader(templateXML));
			domParser.parse(inputSource);
			Document document = domParser.getDocument();
			NodeList tableNodes = document.getElementsByTagName(StyleTemplateType.TABLE.templateXMLTag());
			if (tableNodes.getLength() > 0)
			{
				Node tableNode = tableNodes.item(0);
				TableTemplate tableTemplate = new TableTemplate(tableNode, styleTemplateId);
				styleTemplateProcessors.add(new TableTemplateProcessor(tableTemplate));
			}
			NodeList paragraphNodes = document.getElementsByTagName(StyleTemplateType.PARAGRAPH.templateXMLTag());
			if (paragraphNodes.getLength() > 0)
			{
				Node paragraphNode = paragraphNodes.item(0);
				ParagraphTemplate paragraphTemplate = new ParagraphTemplate(paragraphNode, styleTemplateId);
				styleTemplateProcessors.add(new ParagraphTemplateProcessor(paragraphTemplate));
			}
			NodeList listNodes = document.getElementsByTagName(StyleTemplateType.LIST.templateXMLTag());
			if (listNodes.getLength() > 0)
			{
				Node listNode = listNodes.item(0);
				ListTemplate listTemplate = new ListTemplate(listNode, styleTemplateId);
				styleTemplateProcessors.add(new ListTemplateProcessor(listTemplate));
			}
			NodeList imageNodes = document.getElementsByTagName(StyleTemplateType.IMAGE.templateXMLTag());
			if (imageNodes.getLength() > 0)
			{
				Node listNode = imageNodes.item(0);
				ImageTemplate imageTemplate = new ImageTemplate(listNode, styleTemplateId);
				styleTemplateProcessors.add(new ImageTemplateProcessor(imageTemplate));
			}
		}
		catch (SAXException | IOException e)
		{
			//e.printStackTrace();
		}
		return styleTemplateProcessors;

	}
}
