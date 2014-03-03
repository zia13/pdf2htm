package htmltemplating.image;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import org.htmlparser.tags.ImageTag;

public class Image
{
	private final ImageTag imageTag;
	
	
	public Image(ImageTag imageTag){
		this.imageTag = imageTag;
	}
	
	public String getStyleString()
	{
		return imageTag.getAttribute("STYLE");
	}

	public String toHTML()
	{
		return null;
	}

	public int getStyleTemplateId() throws UnparsableStyleException
	{
		try
		{
			return Integer.parseInt(StyleStringProcessor.getCSSStyle(getStyleString(), CSSStyle.STYLETEMPLATEID));
		}
		catch (NumberFormatException e)
		{
			throw new UnparsableStyleException("given style-template-id is not parsable as int");
		}
		catch (CellStyleNotFoundException e)
		{
			return -1;
		}
	}	
	
	public void setStyleTemplateId(int styleTemplateId) throws UnparsableStyleException
	{
		String styleString = StyleStringProcessor.setCSSStyle(getStyleString(), CSSStyle.STYLETEMPLATEID,
				Integer.toString(styleTemplateId));
		this.imageTag.setAttribute("STYLE", styleString);
	}	

	public void setMargin(String margin)
	{
		String styleString = imageTag.getAttribute("STYLE");
		try
		{
			styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGIN, margin);
			imageTag.setAttribute("STYLE", styleString);
		}
		catch (UnparsableStyleException e)
		{
			e.printStackTrace();
		}
	}	

	public void setImageAlign(String align){
		String styleString = imageTag.getAttribute("STYLE");
		if(align.equals("left")){
			try
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.DISPLAY, "block");
				imageTag.setAttribute("STYLE", styleString);
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGINRIGHT, "auto");
				imageTag.setAttribute("STYLE", styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
		else if(align.equals("right")){
			try
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.DISPLAY, "block");
				imageTag.setAttribute("STYLE", styleString);
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGINLEFT, "auto");
				imageTag.setAttribute("STYLE", styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
		else if(align.equals("center")){
			try
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.DISPLAY, "block");
				imageTag.setAttribute("STYLE", styleString);
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGINLEFT, "auto");
				imageTag.setAttribute("STYLE", styleString);
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGINRIGHT, "auto");
				imageTag.setAttribute("STYLE", styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void setBorder(String border)
	{
		String styleString = imageTag.getAttribute("STYLE");
		try
		{
			styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.BORDER, border);
			imageTag.setAttribute("STYLE", styleString);
		}
		catch (UnparsableStyleException e)
		{
			e.printStackTrace();
		}
	}	
	
}
