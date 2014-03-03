package htmltemplating.table;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import java.util.ArrayList;
import java.util.HashMap;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.util.NodeList;

public class TableCell
{
	private static final String COLSPAN = "colspan";
	private static final String ROWSPAN = "rowspan";
	private static final String STYLE = "style";
	private boolean isValidCellElement = false;
	private ArrayList<TableCellType> selfCellTypes = new ArrayList<TableCellType>();
	HashMap<CellCustomParameter, Object> CellCustomParameterMap = new HashMap<CellCustomParameter, Object>();
	private final TableColumn tableColumn;
	private String cellNodeDataValue;
	private int columnSpan;
	private int rowSpan;
	private String styleString;
	private String content;
	protected boolean isRowSpan = false;
	protected boolean isColSpan = false;
	protected int rowSpanIndex = 0;
	protected int colSpanIndex = 0;

	public enum TableCellType
	{
		OtherCell, CurrencyCell, GutterCell, EvenRowCell, OddRowCell, EvenColumnCell, OddColumnCell, TextCell, NumberCell, StubCell, HeaderCell, PercentCell
	}

	public enum CellCustomParameter
	{
		pointsForTextDensity, pointsForText, pointsForBoldData, pointsForCellAlignment, pointsForBelowValueCell, pointsForAllColumnText, pointsForLeftCellTextAndRightCellText, pointsForCurrency, pointsForRightSideNumber, pointsForEndingBracket, pointsForNumberStartWithOpenBracket
	}

	public TableCell(TableColumn tableColumn)
	{
		this.tableColumn = tableColumn;
		if (null != tableColumn.getAttribute(COLSPAN))
			columnSpan = Integer.parseInt(tableColumn.getAttribute(COLSPAN));
		isColSpan = (columnSpan > 1);
		if (null != tableColumn.getAttribute(ROWSPAN))
			rowSpan = Integer.parseInt(tableColumn.getAttribute(ROWSPAN));
		isRowSpan = (rowSpan > 1);
		styleString = tableColumn.getAttribute(STYLE);
		content = tableColumn.toPlainTextString();
	}

	public void setTextAlign(String align)
	{
		if(!align.isEmpty())
		{
			NodeList divTagNodeList = new NodeList();
			NodeFilter divTagFilter = new NodeClassFilter(Div.class);
			tableColumn.collectInto(divTagNodeList, divTagFilter);
			Node[] divTagNodeArray = divTagNodeList.toNodeArray();
			for (Node divTagNode : divTagNodeArray)
			{
				String styleString = ((TagNode) divTagNode).getAttribute(STYLE);
				try
				{
					styleString = StyleStringProcessor.setCSSStyle(styleString, CSSStyle.TEXTALIGN, align);
					((TagNode) divTagNode).setAttribute(STYLE, styleString);
				}
				catch (UnparsableStyleException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}

	public void setCellWidth(String width)
	{
		NodeList divTagNodeList = new NodeList();
		NodeFilter divTagFilter = new NodeClassFilter(Div.class);
		tableColumn.collectInto(divTagNodeList, divTagFilter);
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
		tableColumn.collectInto(fontTagNodeList, fontTagFilter);
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
	
	public void setBoldStatus(String boldStatus)
	{
		if (!boldStatus.isEmpty())
		{
			setBoldStatus(Boolean.parseBoolean(boldStatus));
		}
	}

	public void setBoldStatus(boolean isBold)
	{
		removeNodes(tableColumn, "B");
		if (isBold)
		{
			setFontStyleTags(true, false, false);
		}
	}
	
	public void setItalicStatus(String italicStatus)
	{
		if (!italicStatus.isEmpty())
		{
			setItalicStatus(Boolean.parseBoolean(italicStatus));
		}
	}

	public void setItalicStatus(boolean isItalic)
	{
		removeNodes(tableColumn, "I");
		if (isItalic)
		{
			setFontStyleTags(false, true, false);
		}
	}
	
	public void setUnderlineStatus(String underlineStatus)
	{
		if (!underlineStatus.isEmpty())
		{
			setUnderlineStatus(Boolean.parseBoolean(underlineStatus));
		}
	}

	public void setUnderlineStatus(boolean isUnderline)
	{
		removeNodes(tableColumn, "U");
		if (isUnderline)
		{
			setFontStyleTags(false, false, true);
		}
	}
	
	public void removeWidth()
	{
		
		NodeList tableTagNodeList = new NodeList();
		NodeFilter tableTagFilter = new NodeClassFilter(TableColumn.class);
		String styleString = "";
		tableColumn.collectInto(tableTagNodeList, tableTagFilter);
		Node[] tableTagNodeArray = tableTagNodeList.toNodeArray();
		for (Node tableTagNode : tableTagNodeArray)
		{
			styleString = ((TagNode) tableTagNode).getAttribute(STYLE);
			if(styleString.contains("width")){
				String s = "width[^;](.*?)%;";
				styleString = styleString.replaceAll(s, "");
			}
			((TagNode) tableTagNode).setAttribute(STYLE, styleString);
		}
	}
	
	public void removeCellStyle(CSSStyle cellStyle){
		NodeList divTagNodeList = new NodeList();
		NodeFilter divTagFilter = new NodeClassFilter(Div.class);
		String styleString = "";
		tableColumn.collectInto(divTagNodeList, divTagFilter);
		Node[] divTagNodeArray = divTagNodeList.toNodeArray();
		for (Node divTagNode : divTagNodeArray)
		{
			styleString = ((TagNode) divTagNode).getAttribute(STYLE);
			if(styleString.contains(cellStyle.styleName())){
				String s = cellStyle.styleName()+"[^;](.*?);";
				styleString = styleString.replaceAll(s, "");
			}
			((TagNode) divTagNode).setAttribute(STYLE, styleString);
		}
		
		NodeList paragraphTagNodeList = new NodeList();
		NodeFilter paragraphFilter = new NodeClassFilter(ParagraphTag.class);
		tableColumn.collectInto(paragraphTagNodeList, paragraphFilter);
		Node[] paragraphTagNodeArray = paragraphTagNodeList.toNodeArray();
		for (Node paragraphTagNode : paragraphTagNodeArray)
		{
			styleString = ((TagNode) paragraphTagNode).getAttribute(STYLE);
			if(styleString.contains(cellStyle.styleName())){
				String s = cellStyle.styleName()+"[^;](.*?);";
				styleString = styleString.replaceAll(s, "");
			}
			((TagNode) paragraphTagNode).setAttribute(STYLE, styleString);
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
		tableColumn.collectInto(textNodesList, textFilter);
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
		return this.tableColumn.toHtml();
	}

	public String getCellStyle(CSSStyle cellStyle) throws UnparsableStyleException, CellStyleNotFoundException
	{
		return StyleStringProcessor.getCSSStyle(styleString, cellStyle);
	}

	public void setCellStyle(CSSStyle cellStyle, String styleValue) throws UnparsableStyleException
	{
		if(!styleValue.isEmpty())
		{
			styleString = StyleStringProcessor.setCSSStyle(styleString, cellStyle, styleValue);
			this.tableColumn.setAttribute(STYLE, styleString);
		}
	}

	public void setPointParameter(CellCustomParameter parameter, Object parameterValue)
	{
		CellCustomParameterMap.put(parameter, parameterValue);
	}

	public Object getPointParameter(CellCustomParameter parameter)
	{
		return CellCustomParameterMap.get(parameter);
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

	public void setCellType(TableCellType cellType)
	{
		selfCellTypes.add(cellType);
	}
	
	public void removeCellType(TableCellType cellType)
	{
		selfCellTypes.remove(cellType);
	}
	
	public boolean isCellType(TableCellType cellType)
	{
		return selfCellTypes.contains(cellType);
	}

	public void setCellColSpan(int colSpan)
	{
		columnSpan = colSpan;
	}

	public int getCellColSpan()
	{
		return columnSpan;
	}

	public void setRowSpan(int rowSpan)
	{
		this.rowSpan = rowSpan;
	}

	public int getRowSpan()
	{
		return rowSpan;
	}

	public void setIsRowSpan(boolean isSpan)
	{
		isRowSpan = isSpan;
	}

	public boolean getIsRowSpan()
	{
		return isRowSpan;
	}

	public void setIsColSpan(boolean isSpan)
	{
		isColSpan = isSpan;
	}

	public boolean getIsColSpan()
	{
		return isColSpan;
	}

	public void setRowSpanIndex(int index)
	{
		rowSpanIndex = index;
	}

	public int getRowSpanIndex()
	{
		return rowSpanIndex;
	}

	public void setColSpanIndex(int index)
	{
		colSpanIndex = index;
	}

	public int getColSpanIndex()
	{
		return colSpanIndex;
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
		return tableColumn;
	}

	public boolean isCurrencyCell()
	{
		String cellContent = getContent();
		if (cellContent.equals("$"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isGutterCell()
	{
		String cellContent = getContent();
		if (cellContent.equals(")"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
