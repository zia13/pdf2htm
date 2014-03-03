package htmltemplating.list;

import org.w3c.dom.Node;

import htmltemplating.StyleTemplate;

public class ListTemplate extends StyleTemplate
{
	public ListTemplate(String templateXML, int styleTemplateId)
	{
		super(templateXML, styleTemplateId);
	}

	public ListTemplate(Node listNode, int styleTemplateId)
	{
		super(listNode, styleTemplateId);
	}
}