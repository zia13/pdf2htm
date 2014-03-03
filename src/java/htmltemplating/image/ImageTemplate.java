package htmltemplating.image;

import org.w3c.dom.Node;

import htmltemplating.StyleTemplate;

public class ImageTemplate extends StyleTemplate
{
	public ImageTemplate(String templateXML, int styleTemplateId)
	{
		super(templateXML, styleTemplateId);
	}
	
	public ImageTemplate(Node imageNode, int styleTemplateId)
	{
		super(imageNode, styleTemplateId);
	}

}
