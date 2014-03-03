package htmltemplating.paragraph;

import org.w3c.dom.Node;

import htmltemplating.StyleTemplate;

public class ParagraphTemplate extends StyleTemplate
{
	public ParagraphTemplate(String templateXML, int styleTemplateId)
	{
		super(templateXML, styleTemplateId);
	}

	public ParagraphTemplate(Node paragraphNode, int styleTemplateId)
	{
		super(paragraphNode, styleTemplateId);
	}

}
