package htmltemplating.table;

import htmltemplating.StyleTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;
import org.w3c.dom.*;

//TODO refactor and clean up this class to be a fully compatible subclass of StyleTemplate.java. Example: ParagraphTemplate.java
public class TableTemplate extends StyleTemplate
{

	public enum TableStyle
	{

		STUB_COLUMN_MIN_WIDTH("minwidth"), GUTTER_COLUMN_WIDTH("width"), GUTTER_COLUMN_ALIGNMENT("align"), GUTTER_COLUMN_IS_CREATE(
				"gutter_column"),

		CURRENCY_COLUMN_WIDTH("width"), CURRENCY_COLUMN_ALIGNMENT("align"), CURRENCY_COLUMN_IS_CREATE("currency_column"),

		VALUE_COLUMN_WIDTH("width"), WIDTH_MIN_COLUMNS("min_columns"), WIDTH_MAX_COLUMNS("max_columns"),

		CELL_ALIGNMENT("align"), CELL_BGCOLOR("bgcolor"), FONTCOLOR("color"), FONTSIZE("size"), FONTFAMILY("family"), BOLD(
				"b"), ITALIC("i"), UNDERLINE("u"),REMOVE_EMPTY_COLUMN("remove_empty_column"), REMOVE_EMPTY_ROW("remove_empty_row");

		private String attribute;

		TableStyle(String attribute)
		{
			this.attribute = attribute;
		}

		public String getAttributeName()
		{
			return attribute;
		}
	}

	private Document xmlDocument;

	private Element tableElement;

	private boolean isValidTableTemplateXML = false;
	private NamedNodeMap stubColumnAttributesMap;
	private NamedNodeMap gutterColumnAttributesMap;

	private NamedNodeMap currencyColumnAttributesMap;

	private Map<String, String> valueColumnAttributesMap = new HashMap<String, String>();
	private NamedNodeMap headerRowStubCellAttributesMap;
	private NamedNodeMap headerRowEvenCellsAttributesMap;

	private NamedNodeMap headerRowOddCellsAttributesMap;
	private NamedNodeMap oddRowsStubCellAttributesMap;
	private NamedNodeMap oddRowsEvenCellsAttributesMap;

	private NamedNodeMap oddRowsOddCellsAttributesMap;
	private NamedNodeMap evenRowsStubCellAttributesMap;
	private NamedNodeMap evenRowsEvenCellsAttributesMap;

	private NamedNodeMap evenRowsOddCellsAttributesMap;
	private String globalStyles = "global_styles";
	private String stubColumnXMLTag = "stub_column";
	private String gutterColumnXMLTag = "gutter_column";
	private String currencyColumnXMLTag = "currency_column";
	private String valueColumnXMLTag = "value_columns";
	private String valueColumnMinColumnsXMLTag = "min_columns";
	private String valueColumnMaxColumnsXMLTag = "max_columns";

	private String valueColumnWidthXMLTag = "width";
	private String tableXMLTag = "table";
	private String headerRowXMLTag = "header_row";
	private String oddRowsXMLTag = "odd_rows";
	private String evenRowsXMLTag = "even_rows";
	private String stubCellXMLTag = "stub_cell";
	private String evenCellsXMLTag = "even_cells";

	private String oddCellsXMLTag = "odd_cells";
	private String fontBoldXML = "b";
	private String fontItalicXML = "i";

	private String fontUnderlineXML = "u";

	// private static double DEFAULT_COLUMN_WIDTH = 10.0;
	private List<WidthRule> widthRules = new ArrayList<WidthRule>();

	@Deprecated
	public TableTemplate(String xmlString) throws IOException
	{
		// TODO remove this constructor when the deprecated usages are cleaned
		// up.
		super(xmlString, -1);
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlString));

			xmlDocument = documentBuilder.parse(inputSource);

			// Get the table attributes
			NodeList tableGlobalNodes = xmlDocument.getElementsByTagName(globalStyles);
			setTableGlobalStylesAttributes(tableGlobalNodes);

			// Get the rows and columns
			NodeList tableNodes = xmlDocument.getElementsByTagName(tableXMLTag);

			if (tableNodes.getLength() != 0)
			{
				isValidTableTemplateXML = true;

				for (int i = 0; i < tableNodes.getLength(); i++)
				{
					if (tableNodes.item(i).getNodeType() == 1)
						tableElement = (Element) tableNodes.item(i);

					NodeList rows = tableElement.getChildNodes();

					for (int j = 0; j < rows.getLength(); j++)
					{
						int nodeType = rows.item(j).getNodeType();

						if (nodeType == 1)
						{
							Element row = (Element) rows.item(j);
							String rowName = row.getNodeName();

							if (rowName.equals(headerRowXMLTag))
							{
								headerRowStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								headerRowEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								headerRowOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(oddRowsXMLTag))
							{
								oddRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								oddRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								oddRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(evenRowsXMLTag))
							{
								evenRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								evenRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								evenRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public TableTemplate(String templateXMLString, int styleTemplateId) throws IOException
	{
		super(templateXMLString, styleTemplateId);

		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(templateXMLString));

			xmlDocument = documentBuilder.parse(inputSource);

			// Get the table attributes
			NodeList tableGlobalNodes = xmlDocument.getElementsByTagName(globalStyles);
			setTableGlobalStylesAttributes(tableGlobalNodes);

			// Get the rows and columns
			NodeList tableNodes = xmlDocument.getElementsByTagName(tableXMLTag);

			if (tableNodes.getLength() != 0)
			{
				isValidTableTemplateXML = true;

				for (int i = 0; i < tableNodes.getLength(); i++)
				{
					if (tableNodes.item(i).getNodeType() == 1)
						tableElement = (Element) tableNodes.item(i);

					NodeList rows = tableElement.getChildNodes();

					for (int j = 0; j < rows.getLength(); j++)
					{
						int nodeType = rows.item(j).getNodeType();

						if (nodeType == 1)
						{
							Element row = (Element) rows.item(j);
							String rowName = row.getNodeName();

							if (rowName.equals(headerRowXMLTag))
							{
								headerRowStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								headerRowEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								headerRowOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(oddRowsXMLTag))
							{
								oddRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								oddRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								oddRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(evenRowsXMLTag))
							{
								evenRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								evenRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								evenRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public TableTemplate(Node tableStyleNode, int styleTemplateId) throws IOException
	{
		super(tableStyleNode, styleTemplateId);

		try
		{
//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//			InputSource inputSource = new InputSource();
//			inputSource.setCharacterStream(new StringReader(templateXMLString));

			xmlDocument = document;

			// Get the table attributes
			NodeList tableGlobalNodes = xmlDocument.getElementsByTagName(globalStyles);
			setTableGlobalStylesAttributes(tableGlobalNodes);

			// Get the rows and columns
			NodeList tableNodes = xmlDocument.getElementsByTagName(tableXMLTag);

			if (tableNodes.getLength() != 0)
			{
				isValidTableTemplateXML = true;

				for (int i = 0; i < tableNodes.getLength(); i++)
				{
					if (tableNodes.item(i).getNodeType() == 1)
						tableElement = (Element) tableNodes.item(i);

					NodeList rows = tableElement.getChildNodes();

					for (int j = 0; j < rows.getLength(); j++)
					{
						int nodeType = rows.item(j).getNodeType();

						if (nodeType == 1)
						{
							Element row = (Element) rows.item(j);
							String rowName = row.getNodeName();

							if (rowName.equals(headerRowXMLTag))
							{
								headerRowStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								headerRowEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								headerRowOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(oddRowsXMLTag))
							{
								oddRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								oddRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								oddRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
							else if (rowName.equals(evenRowsXMLTag))
							{
								evenRowsStubCellAttributesMap = getTableCellAttributesMap(row, stubCellXMLTag);
								evenRowsEvenCellsAttributesMap = getTableCellAttributesMap(row, evenCellsXMLTag);
								evenRowsOddCellsAttributesMap = getTableCellAttributesMap(row, oddCellsXMLTag);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	// TODO refactor to accept an typesafe enum rather than a string (at least
	// for attributeName)
	@Deprecated
	public String getAttributeValue(String row, String column, String attributeName)
	{
		String attrValue = "";

		if (row.toLowerCase().contains("header") && column.toLowerCase().contains("stub"))
		{
			Node attrNode = headerRowStubCellAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("header") && column.toLowerCase().contains("even"))
		{
			Node attrNode = headerRowEvenCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("header") && column.toLowerCase().contains("odd"))
		{
			Node attrNode = headerRowOddCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("odd") && column.toLowerCase().contains("stub"))
		{
			Node attrNode = oddRowsStubCellAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("odd") && column.toLowerCase().contains("even"))
		{
			Node attrNode = oddRowsEvenCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("odd") && column.toLowerCase().contains("odd"))
		{
			Node attrNode = oddRowsOddCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("even") && column.toLowerCase().contains("stub"))
		{
			Node attrNode = evenRowsStubCellAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("even") && column.toLowerCase().contains("even"))
		{
			Node attrNode = evenRowsEvenCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}
		else if (row.toLowerCase().contains("even") && column.toLowerCase().contains("odd"))
		{
			Node attrNode = evenRowsOddCellsAttributesMap.getNamedItem(attributeName);

			if (attrNode != null)
				attrValue = attrNode.getNodeValue();
		}

		return attrValue;
	}

	private String getCurrencyColumnAttribute(String attributeName)
	{
		String attrValue = "";

		Node attrNode = currencyColumnAttributesMap.getNamedItem(attributeName);

		if (attrNode != null)
			attrValue = attrNode.getNodeValue();

		return attrValue;
	}

	private String getGutterColumnAttribute(String attributeName)
	{
		String attrValue = "";

		Node attrNode = gutterColumnAttributesMap.getNamedItem(attributeName);

		if (attrNode != null)
			attrValue = attrNode.getNodeValue();

		return attrValue;
	}

	private String getStubColumnAttribute(String attributeName)
	{
		String attrValue = "";

		Node attrNode = stubColumnAttributesMap.getNamedItem(attributeName);

		if (attrNode != null)
			attrValue = attrNode.getNodeValue();

		return attrValue;
	}

	private NamedNodeMap getTableCellAttributesMap(Element row, String cell)
	{
		NamedNodeMap cellsAttrMaps = null;
		NamedNodeMap fontAttrMaps = null;

		NodeList cells = row.getChildNodes();

		for (int i = 0; i < cells.getLength(); i++)
		{
			int cellnodeType = cells.item(i).getNodeType();

			if (cellnodeType == 1)
			{
				Element cellElem = (Element) cells.item(i);

				if (cellElem.getNodeName().equals(cell))
				{
					cellsAttrMaps = cellElem.getAttributes();

					NodeList fonts = cellElem.getChildNodes();

					for (int j = 0; j < fonts.getLength(); j++)
					{
						int fontNodeType = fonts.item(j).getNodeType();

						if (fontNodeType == 1)
						{
							Element font = (Element) fonts.item(j);

							fontAttrMaps = font.getAttributes();

							for (int k = 0; k < fontAttrMaps.getLength(); k++)
							{
								Node fontAtrrNode = xmlDocument.createAttribute(fontAttrMaps.item(k).getNodeName());
								fontAtrrNode.setNodeValue(fontAttrMaps.item(k).getNodeValue());
								cellsAttrMaps.setNamedItem(fontAtrrNode);
							}

							NodeList fontStyleNodes = font.getChildNodes();

							for (int l = 0; l < fontStyleNodes.getLength(); l++)
							{
								int fontStyleNodeType = fontStyleNodes.item(l).getNodeType();

								if (fontStyleNodeType == 1)
								{
									Element fontStyleNode = (Element) fontStyleNodes.item(l);

									if (fontStyleNode.getNodeName() == fontBoldXML)
									{
										Node boldNode = xmlDocument.createAttribute(fontStyleNode.getNodeName());

										if (fontStyleNode.getFirstChild() != null)
											boldNode.setNodeValue(fontStyleNode.getFirstChild().getNodeValue());
										else
											boldNode.setNodeValue("");

										cellsAttrMaps.setNamedItem(boldNode);
									}
									else if (fontStyleNode.getNodeName() == fontItalicXML)
									{
										Node italicNode = xmlDocument.createAttribute(fontStyleNode.getNodeName());

										if (fontStyleNode.getFirstChild() != null)
											italicNode.setNodeValue(fontStyleNode.getFirstChild().getNodeValue());
										else
											italicNode.setNodeValue("");

										cellsAttrMaps.setNamedItem(italicNode);
									}
									else if (fontStyleNode.getNodeName() == fontUnderlineXML)
									{
										Node underlineNode = xmlDocument.createAttribute(fontStyleNode.getNodeName());

										if (fontStyleNode.getFirstChild() != null)
											underlineNode.setNodeValue(fontStyleNode.getFirstChild().getNodeValue());
										else
											underlineNode.setNodeValue("");

										cellsAttrMaps.setNamedItem(underlineNode);
									}
								}
							}
						}
					}
				}
			}
		}

		return cellsAttrMaps;
	}

	@Deprecated
	public String getTableGlobalAttributeValue(TableStyle tableStyle)
	{

		String attrValue = "";
		switch (tableStyle)
		{

			case STUB_COLUMN_MIN_WIDTH:
			{
				attrValue = getStubColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case GUTTER_COLUMN_WIDTH:
			{
				attrValue = getGutterColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case GUTTER_COLUMN_ALIGNMENT:
			{
				attrValue = getGutterColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case GUTTER_COLUMN_IS_CREATE:
			{
				attrValue = getGutterColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case CURRENCY_COLUMN_WIDTH:
			{
				attrValue = getCurrencyColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case CURRENCY_COLUMN_ALIGNMENT:
			{
				attrValue = getCurrencyColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case CURRENCY_COLUMN_IS_CREATE:
			{
				attrValue = getCurrencyColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case VALUE_COLUMN_WIDTH:
			{
				attrValue = getValueColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case WIDTH_MIN_COLUMNS:
			{
				attrValue = getValueColumnAttribute(tableStyle.getAttributeName());
				break;
			}
			case WIDTH_MAX_COLUMNS:
			{
				attrValue = getValueColumnAttribute(tableStyle.getAttributeName());
				break;
			}
		}

		return attrValue;
	}

	private String getValueColumnAttribute(String attributeName)
	{
		String attrValue = "";

		attrValue = valueColumnAttributesMap.get(attributeName);

		return attrValue;
	}

	public boolean isTableTemplateValid()
	{
		return isValidTableTemplateXML;
	}

	private void setTableGlobalStylesAttributes(NodeList globalStylesNodes)
	{
		for (int i = 0; i < globalStylesNodes.getLength(); i++)
		{
			if (globalStylesNodes.item(i).getNodeType() == 1)
			{
				NodeList globalStyleChildrenNodes = ((Element) globalStylesNodes.item(i)).getChildNodes();

				for (int j = 0; j < globalStyleChildrenNodes.getLength(); j++)
				{
					if (globalStyleChildrenNodes.item(j).getNodeType() == 1)
					{
						Element childNode = (Element) globalStyleChildrenNodes.item(j);

						if (childNode.getNodeName().equals(stubColumnXMLTag))
						{
							stubColumnAttributesMap = childNode.getAttributes();
						}
						else if (childNode.getNodeName().equals(gutterColumnXMLTag))
						{
							gutterColumnAttributesMap = childNode.getAttributes();

							Node node = xmlDocument.createAttribute(childNode.getNodeName());
							if(null != childNode.getFirstChild())
							{
								node.setNodeValue(childNode.getFirstChild().getNodeValue());
							}
							gutterColumnAttributesMap.setNamedItem(node);
						}
						else if (childNode.getNodeName().equals(currencyColumnXMLTag))
						{
							currencyColumnAttributesMap = childNode.getAttributes();

							Node node = xmlDocument.createAttribute(childNode.getNodeName());
							if(null != childNode.getFirstChild())
							{
								node.setNodeValue(childNode.getFirstChild().getNodeValue());
							}
							currencyColumnAttributesMap.setNamedItem(node);
						}
						else if (childNode.getNodeName().equals(valueColumnXMLTag))
						{
							NodeList widthRuleNodes = childNode.getChildNodes();

							for (int k = 0; k < widthRuleNodes.getLength(); k++)
							{
								if (widthRuleNodes.item(k).getNodeType() == 1)
								{
									WidthRule rule = new WidthRule();
									NodeList widthRuleChildrenElement = ((Element) widthRuleNodes.item(k))
											.getChildNodes();

									for (int l = 0; l < widthRuleChildrenElement.getLength(); l++)
									{
										if (widthRuleChildrenElement.item(l).getNodeType() == 1)
										{
											Element widthRuleNode = (Element) widthRuleChildrenElement.item(l);

											if (widthRuleNode.getNodeName().equals(valueColumnMinColumnsXMLTag))
											{
												rule.setMinColumn(new Integer(widthRuleNode.getFirstChild()
														.getNodeValue()));
												valueColumnAttributesMap.put(widthRuleNode.getNodeName(), widthRuleNode
														.getFirstChild().getNodeValue());
											}
											else if (widthRuleNode.getNodeName().equals(valueColumnMaxColumnsXMLTag))
											{
												rule.setMaxColumn(new Integer(widthRuleNode.getFirstChild()
														.getNodeValue()));
												valueColumnAttributesMap.put(widthRuleNode.getNodeName(), widthRuleNode
														.getFirstChild().getNodeValue());
											}
											else if (widthRuleNode.getNodeName().equals(valueColumnWidthXMLTag))
											{
												rule.setWidth(new Integer(widthRuleNode.getFirstChild().getNodeValue()));
												valueColumnAttributesMap.put(widthRuleNode.getNodeName(), widthRuleNode
														.getFirstChild().getNodeValue());
											}
											widthRules.add(rule);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public double getValueColumnWidth(int numberOfColumns) throws ColumnWidthNotDefinedException
	{
		WidthRule myRule = null;
		for (Iterator<WidthRule> i = widthRules.iterator(); i.hasNext();)
		{
			WidthRule rule = i.next();
			if (rule.getMinColumn() == -1)
			{
				if (numberOfColumns < rule.getMaxColumn())
				{
					myRule = rule;
				}
			}
			else if (rule.getMaxColumn() == -1)
			{
				if (numberOfColumns > rule.getMinColumn())
				{
					myRule = rule;
				}
			}
			else if (rule.getMinColumn() == rule.getMaxColumn())
			{
				if (numberOfColumns == rule.getMinColumn())
				{
					myRule = rule;
					break;
				}
			}
		}
		if (myRule != null)
		{
			return myRule.getWidth();
		}
		else
		{
			throw new ColumnWidthNotDefinedException("Unable to match with given xml rules.");
		}
	}
}