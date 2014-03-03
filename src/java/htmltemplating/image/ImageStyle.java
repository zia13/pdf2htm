package htmltemplating.image;

import htmltemplating.TemplateStyle;

public enum ImageStyle implements TemplateStyle
{
	ALIGN("align", "img"), MARGIN("margin", "img"), BORDER("border", "img");
	
	private final String refXMLString;
	private final String refTagName;
	
	private ImageStyle(String refXMLString, String refTagName){
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
}
