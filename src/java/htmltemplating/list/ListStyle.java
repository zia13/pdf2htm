package htmltemplating.list;

import htmltemplating.TemplateStyle;

public enum ListStyle implements TemplateStyle
{
	TEXTALIGN("text-align", "list"), FONTCOLOR("color", "font"), FONTSIZE("size", "font"), FONTFAMILY("family", "font"), BOLD(
			"b", "b"), ITALIC("i", "i"), UNDERLINE("u", "u"),LISTSTYLETYPE("list-style-type","list");
	private ListStyle(String refXMLString, String refTagName)
	{
		this.refXMLString = refXMLString;
		this.refTagName = refTagName;
	}

	public String refXMLString()
	{
		return refXMLString;
	}

	public String refTagName()
	{
		return refTagName;
	}

	public boolean isAttribute()
	{
		return !refXMLString.equals(refTagName);
	}

	private final String refXMLString;
	private final String refTagName;
}