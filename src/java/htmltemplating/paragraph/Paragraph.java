package htmltemplating.paragraph;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;

import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;

public class Paragraph
{
	private final Div divTag;
	private boolean isStylableParagraph = true;

	public Paragraph(Div divTag)
	{
		this.divTag = divTag;
	}

	public String getStyleString()
	{
		return divTag.getAttribute("STYLE");
	}

	public Div getDivTag()
	{
		return divTag;
	}

	public boolean isStylableParagraph()
	{
		return isStylableParagraph;
	}

	public void setStylableParagraph(boolean isStylableParagraph)
	{
		this.isStylableParagraph = isStylableParagraph;
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
		this.divTag.setAttribute("STYLE", styleString);
	}

	public void setTextAlign(String textAlign)
	{
		String styleString = divTag.getAttribute("STYLE");
		try
		{
			styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.TEXTALIGN, textAlign);
			divTag.setAttribute("STYLE", styleString);
		}
		catch (UnparsableStyleException e)
		{
			e.printStackTrace();
		}
	}

	public void setMargin(String margin)
	{
		String styleString = divTag.getAttribute("STYLE");
		try
		{
			styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.MARGIN, margin);
			divTag.setAttribute("STYLE", styleString);
		}
		catch (UnparsableStyleException e)
		{
			e.printStackTrace();
		}
	}

	public void setTextIndent(String firstLineIndent, String hangingIndent)
	{
		String styleString = divTag.getAttribute("STYLE");

		int indentValue = Integer.parseInt(firstLineIndent.substring(0, firstLineIndent.length() - 2))
				- Integer.parseInt(hangingIndent.substring(0, hangingIndent.length() - 2));

		try
		{
			if (indentValue < 0)
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.PADDINGLEFT,
						Math.abs(indentValue) + "px");
			}
			styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.TEXTINDENT, indentValue + "px");
			divTag.setAttribute("STYLE", styleString);
		}
		catch (UnparsableStyleException e)
		{
			e.printStackTrace();
		}

	}

	public void setFontStyles(String fontColor, String fontSize, String fontFamily)
	{

		NodeList fontTagNodeList = new NodeList();
		NodeFilter fontTagFilter = new TagNameFilter("FONT");
		divTag.collectInto(fontTagNodeList, fontTagFilter);
		Node[] fontTagNodeArray = fontTagNodeList.toNodeArray();
		for (Node fontTagNode : fontTagNodeArray)
		{

			String styleString = ((TagNode) fontTagNode).getAttribute("STYLE");
			try
			{
				if (!fontColor.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTCOLOR, fontColor);
				if (!fontSize.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTSIZE, fontSize);
				if (!fontFamily.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTFAMILY, fontFamily);
				
				((TagNode) fontTagNode).setAttribute("STYLE", styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setBoldStatus(boolean isBold)
	{
		removeNodes(divTag, "B");
		if (isBold)
		{
			setFontStyleTags(true, false, false);
		}
	}

	public void setItalicStatus(boolean isItalic)
	{
		removeNodes(divTag, "I");
		if (isItalic)
		{
			setFontStyleTags(false, true, false);
		}
	}

	public void setUnderlineStatus(boolean isUnderline)
	{
		removeNodes(divTag, "U");
		if (isUnderline)
		{
			setFontStyleTags(false, false, true);
		}
	}

	public void setStylableState(boolean isStylable)
	{
		this.isStylableParagraph = isStylable;
	}

	private void removeNodes(TagNode tagNode, String tagNameToRemove)
	{
		NodeList children = tagNode.getChildren();
		if (null != children)
		{
			for (int childIndex = 0; childIndex < children.size();)
			{
				Node currentChild = children.elementAt(childIndex);
				if (currentChild instanceof TagNode)
				{
					TagNode currentTag = ((TagNode) currentChild);
					if (currentTag.getTagName().equals(tagNameToRemove))
					{
						children.remove(childIndex);
					}
					else
					{
						removeNodes(currentTag, tagNameToRemove);
						childIndex++;
					}
				}
				else
				{
					childIndex++;
				}
			}
			tagNode.setChildren(children);
		}
	}

	// TODO if introducing new tags using string manipulation affects the
	// overall parsing (ex. mess up the node hierarchy in the parser) find
	// another intended way to set font style tags
	private void setFontStyleTags(boolean isBold, boolean isItalic, boolean isUnderline)
	{
		NodeFilter textFilter = new NodeClassFilter(TextNode.class);
		NodeList textNodesList = new NodeList();
		divTag.collectInto(textNodesList, textFilter);
		Node[] textNodeArray = textNodesList.toNodeArray();
		for (Node currentNode : textNodeArray)
		{
			TextNode currentTextNode = (TextNode) currentNode;
			if (isUnderline)
				currentTextNode.setText("<u>" + currentTextNode.getText() + "</u>");
			if (isItalic)
				currentTextNode.setText("<i>" + currentTextNode.getText() + "</i>");
			if (isBold)
				currentTextNode.setText("<b>" + currentTextNode.getText() + "</b>");
		}
	}

}
