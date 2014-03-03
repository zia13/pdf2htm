package htmltemplating.paragraph;

import htmltemplating.TemplateStyle;

public enum ParagraphStyle implements TemplateStyle
{
	TEXTALIGN("text-align", "paragraph"), MARGIN("margin", "paragraph"), FONTCOLOR("color", "font"), FONTSIZE("size",
			"font"), FONTFAMILY("family", "font"), BOLD("b", "b"), ITALIC("i", "i"), UNDERLINE("u", "u"), FIRSTLINEINDENT(
			"indent", "first_line"), HANGINGINDENT("indent", "hanging");
	private ParagraphStyle(String refXMLString, String refTagName)
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
