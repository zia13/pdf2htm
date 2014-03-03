package htmltemplating;

public enum StyleTemplateType
{
	TABLE("financial_table_style"), PARAGRAPH("paragraph_style"), LIST("list_style"), IMAGE("image_style");
	private StyleTemplateType(String templateXMLTag)
	{
		this.templateXMLTag = templateXMLTag;
	}

	public String templateXMLTag()
	{
		return templateXMLTag;
	}

	private final String templateXMLTag;
}
