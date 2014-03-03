package htmltemplating;

import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StyleTemplate
{
	private final int styleTemplateId;
	protected Document document;

	public StyleTemplate(String templateXML, int styleTemplateId)
	{
		this.styleTemplateId = styleTemplateId;
		InputSource templateXMLSource = new InputSource(new StringReader(templateXML));
		DOMParser domParser = new DOMParser();
		try
		{
			domParser.parse(templateXMLSource);
			document = domParser.getDocument();
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

	public StyleTemplate(Node templateNode, int styleTemplateId)
	{
		this.styleTemplateId = styleTemplateId;
		document = new DocumentImpl();
		document.appendChild(document.importNode(templateNode, true));
	}

	public String getStyleValue(TemplateStyle templateStyle) throws StyleNotDefinedException
	{
		Node styleNode = document.getElementsByTagName(templateStyle.refTagName()).item(0);
		if (null == styleNode)
			throw new StyleNotDefinedException("The style " + templateStyle.refXMLString() + " is not defined.");
		if (templateStyle.isAttribute())
		{
			NamedNodeMap attributesMap = styleNode.getAttributes();
			return attributesMap.getNamedItem(templateStyle.refXMLString()).getNodeValue();
		}
		else
		{
			return styleNode.getTextContent();
		}
	}
	
	public String getStyleValue(TemplateStyle templateStyle, String defaultValue)
	{
		try
		{
			Node styleNode = document.getElementsByTagName(templateStyle.refTagName()).item(0);
			if (styleNode == null)
			{
				System.out.println("The style " + templateStyle.refXMLString() + " is not defined. default["+defaultValue+"]");
			}
			else if (templateStyle.isAttribute())
			{
				NamedNodeMap attributesMap = styleNode.getAttributes();
				defaultValue = attributesMap.getNamedItem(templateStyle.refXMLString()).getNodeValue();
			}
			else
			{
				defaultValue = styleNode.getTextContent();
			}
		}
		catch(Exception ex)
		{
			System.out.println("The style " + templateStyle.refXMLString() + " is not defined.");
		}
		return defaultValue;
	}
	
	public int getStyleTemplateId()
	{
		return styleTemplateId;
	}
}
