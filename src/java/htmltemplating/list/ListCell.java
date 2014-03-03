package htmltemplating.list;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import java.util.ArrayList;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.util.NodeList;

public class ListCell
{
	private static final String STYLE = "style";
	private boolean isValidCellElement = false;
	private ArrayList<ListCellType> selfCellTypes = new ArrayList<ListCellType>();
	private final TableColumn listColumn;
	private String cellNodeDataValue;
	private String styleString;
	private String content;

	public enum ListCellType
	{
		TextCell
	}

	public ListCell(TableColumn listColumn)
	{
		this.listColumn = listColumn;
		styleString = listColumn.getAttribute(STYLE);
		content = listColumn.toPlainTextString();
	}

	public void setTextAlign(String align)
	{
		
		NodeList tdTagNodeList = new NodeList();
		NodeFilter tdTagFilter = new TagNameFilter("TD");
		listColumn.collectInto(tdTagNodeList, tdTagFilter);
		Node[] tdTagNodeArray = tdTagNodeList.toNodeArray();
		
		for (Node tdTagNode : tdTagNodeArray)
		{
			
			String styleString = ((TagNode) tdTagNode).getAttribute(STYLE);
			try
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.TEXTALIGN, align);
				((TagNode) tdTagNode).setAttribute(STYLE, styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setCellWidth(String width)
	{
		NodeList divTagNodeList = new NodeList();
		NodeFilter divTagFilter = new NodeClassFilter(Div.class);
		listColumn.collectInto(divTagNodeList, divTagFilter);
		Node[] divTagNodeArray = divTagNodeList.toNodeArray();
		for (Node divTagNode : divTagNodeArray)
		{
			
			
			String styleString = ((TagNode) divTagNode).getAttribute(STYLE);
			try
			{
				styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.WIDTH, width);
				((TagNode) divTagNode).setAttribute(STYLE, styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setFontStyles(String fontColor, String fontSize, String fontFamily)
	{
		NodeList fontTagNodeList = new NodeList();
		NodeFilter fontTagFilter = new TagNameFilter("FONT");
		listColumn.collectInto(fontTagNodeList, fontTagFilter);
		Node[] fontTagNodeArray = fontTagNodeList.toNodeArray();
		for (Node fontTagNode : fontTagNodeArray)
		{
			String styleString = ((TagNode) fontTagNode).getAttribute(STYLE);
			try
			{
				if (!fontColor.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTCOLOR, fontColor);
				if (!fontSize.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTSIZE, fontSize);
				if (!fontFamily.isEmpty())
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.FONTFAMILY, fontFamily);
				((TagNode) fontTagNode).setAttribute(STYLE, styleString);
			}
			catch (UnparsableStyleException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setBoldStatus(boolean isBold)
	{
		removeNodes(listColumn, "B");
		if (isBold)
		{
			setFontStyleTags(true, false, false);
		}
	}

	public void setItalicStatus(boolean isItalic)
	{
		removeNodes(listColumn, "I");
		if (isItalic)
		{
			setFontStyleTags(false, true, false);
		}
	}

	public void setUnderlineStatus(boolean isUnderline)
	{
		removeNodes(listColumn, "U");
		if (isUnderline)
		{
			setFontStyleTags(false, false, true);
		}
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
		listColumn.collectInto(textNodesList, textFilter);
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

	public String toHTML()
	{
		return this.listColumn.toHtml();
	}

	public String getCellStyle(CSSStyle cellStyle) throws UnparsableStyleException, CellStyleNotFoundException
	{
		return StyleStringProcessor.getCSSStyle(styleString, cellStyle);
	}

	public void setCellStyle(CSSStyle cellStyle, String styleValue) throws UnparsableStyleException
	{
		styleString = StyleStringProcessor.setCSSStyle(styleString, cellStyle, styleValue);
		this.listColumn.setAttribute(STYLE, styleString);
	}

	public boolean isValidCellElement()
	{
		return isValidCellElement;
	}

	// TODO Remove this methods after defining a proper method to retrieve
	// styles
	public boolean isCenterAligned()
	{
		return false;
	}

	public boolean isCellBold()
	{
		return false;
	}

	public void setCellDataValue(String dataValue)
	{
		cellNodeDataValue = dataValue;
	}

	public String getCellDataValue()
	{
		return cellNodeDataValue;
	}

	public void setCellType(ListCellType cellType)
	{
		selfCellTypes.add(cellType);
	}

	public boolean isCellType(ListCellType cellType)
	{
		return selfCellTypes.contains(cellType);
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getContent()
	{
		return content;
	}

	public TableColumn getTableColumn()
	{
		return listColumn;
	}

}