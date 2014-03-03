package htmltemplating.cssstyle;

public enum CSSStyle
{
	BGCOLOR("background-color"), ALIGN("align"), BORDER("border"), BORDERSTYLE("border-style"), BORDERWIDTH(
			"border-width"), TEXTALIGN("text-align"), FONTSIZE("font-size"), FONTCOLOR("color"), FONTFAMILY(
			"font-family"), MARGIN("margin"), MARGINLEFT("margin-left"), MARGINRIGHT("margin-right"), 
			STYLETEMPLATEID("style-template-id"), WIDTH("width"), HEIGHT("height"), TEXTINDENT("text-indent"),
			PADDINGLEFT("padding-left"), DISPLAY("display"), VERTICALALIGN("vertical-align");

	private CSSStyle(String styleName)
	{
		this.styleName = styleName;
	}

	public String styleName()
	{
		return styleName;
	}

	private final String styleName;
}
