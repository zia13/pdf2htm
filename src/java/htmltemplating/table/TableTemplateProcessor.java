package htmltemplating.table;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.StyleTemplateProcessor;
import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.table.TableCell.TableCellType;
import htmltemplating.table.TableTemplate.TableStyle;

import java.util.ArrayList;
import java.util.HashMap;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

public class TableTemplateProcessor extends StyleTemplateProcessor
{
	private TableTemplate tableTemplate;

	public TableTemplateProcessor(TableTemplate tableTemplate)
	{
		super();
		this.tableTemplate = tableTemplate;
	}

	public void visitRemarkNode(Remark remarkNode)
	{
		modifiedHTML.append(remarkNode.toHtml());
	}

	public void visitStringNode(Text stringNode)
	{
		//
	}

	public void visitTag(Tag tag)
	{
		if (tag instanceof TableTag)
		{
			TableStructure tableStructure = new TableStructure((TableTag) tag);
			TableTag tableTag = (TableTag) tag;
			// TODO make the tableAnalyzer analyzing static(if possible)
			// TODO handle column widths
			TableAnalyzer tableAnalyzer = new TableAnalyzer(tableStructure);
			if (tableAnalyzer.isFinancialTable())
			{
				try
				{
					if (tableStructure.getStyleTemplateId() == tableTemplate.getStyleTemplateId()
							|| tableStructure.getStyleTemplateId() == -1)
					{
						tableTag.setAttribute("cellpadding", "0");
						tableTag.setAttribute("cellspacing", "0");
						tableTag.setAttribute("border", "0");
						
						if (tableStructure.getStyleTemplateId() == -1)
						{
							tableStructure.setStyleTemplateId(tableTemplate.getStyleTemplateId());
						}
						
						String defaultBooleanValue		= "false";
						String currencyColumnStatus		= tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_IS_CREATE, defaultBooleanValue);
						String gutterColumnStatus		= tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_IS_CREATE, defaultBooleanValue);
						String removeEmptyCellStatus	= tableTemplate.getStyleValue(htmltemplating.table.TableStyle.REMOVE_EMPTY_COLUMN, defaultBooleanValue);
						
						if (!removeEmptyCellStatus.isEmpty())
						{
							if (Boolean.parseBoolean(removeEmptyCellStatus))
							{
								tableStructure.processRemoveEmptyRowColumn();
								tableAnalyzer.reInitialize();
							}
						}
						
						if (!currencyColumnStatus.isEmpty())
						{
							if (Boolean.parseBoolean(currencyColumnStatus))
							{
								tableStructure.processAddingCurrencyColumn(tableTemplate);
							}
						}
						if (!gutterColumnStatus.isEmpty())
						{
							if (Boolean.parseBoolean(gutterColumnStatus))
							{
								tableStructure.processAddingGutterColumn(tableTemplate);
							}
						}

						ArrayList<TableCell> evenRowCells = tableStructure.getCellsByType(TableCellType.EvenRowCell);
						processRowStyles(evenRowCells, "even", tableStructure);

						ArrayList<TableCell> oddRowCells = tableStructure.getCellsByType(TableCellType.OddRowCell);
						processRowStyles(oddRowCells, "odd", tableStructure);

						ArrayList<TableCell> headerRowCells = tableStructure.getCellsByType(TableCellType.HeaderCell);
						processRowStyles(headerRowCells, "header", tableStructure);
						
						//tableTag.setAttribute("width", "100%");
						
						tableTag.setAttribute("template", tableTemplate.getStyleTemplateId()+"");

					}
				}
				catch (UnparsableStyleException e)
				{
					e.printStackTrace();
				}
				catch (StyleNotDefinedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//tableTag.setAttribute("width", "100%");
			}
		}
		if (null == tag.getParent() && (!(tag instanceof CompositeTag) || null == ((CompositeTag) tag).getEndTag()))
		{
			modifiedHTML.append(tag.toHtml());
		}
	}

	private void processRowStyles(ArrayList<TableCell> rowCellsList, String rowIdentifierString,
			TableStructure tableStructure) throws UnparsableStyleException
	{
		HashMap<TableCell.TableCellType, String> columnCellTypeMap = new HashMap<TableCell.TableCellType, String>();
		columnCellTypeMap.put(TableCellType.StubCell, "stub");
		columnCellTypeMap.put(TableCellType.OddColumnCell, "odd");
		columnCellTypeMap.put(TableCellType.EvenColumnCell, "even");
		double valueColumnWidth = 0;

		boolean stubColumnMinWidthRuleExists = true;
		boolean currencyColumnWidthRule = false;

		String currencyWidth = "";
		String currencyAlign = "";
		String gutterWidth = "";
		String gutterAligh = "";
		String defaultEmptyValue   = "";
		String defaultBooleanValue = "false";
		String currencyColumnStatus = tableTemplate
				.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_IS_CREATE, defaultBooleanValue);
		if (!currencyColumnStatus.isEmpty())
		{
			if (Boolean.parseBoolean(currencyColumnStatus))
			{
				currencyWidth = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_WIDTH, defaultEmptyValue);
				currencyAlign = tableTemplate
						.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_ALIGNMENT, defaultEmptyValue);
			}
		}
		String gutterColumnStatus = tableTemplate
				.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_IS_CREATE, defaultBooleanValue);
		if (!gutterColumnStatus.isEmpty())
		{
			if (Boolean.parseBoolean(gutterColumnStatus))
			{
				gutterWidth = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_WIDTH, defaultEmptyValue);
				gutterAligh = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_ALIGNMENT, defaultEmptyValue);
			}
		}

		try
		{
			valueColumnWidth = tableTemplate.getValueColumnWidth(tableStructure.getTotalNumberOfColumnsInTable());
		}
		catch (ColumnWidthNotDefinedException e)
		{

			String stubColumnMinWidth;
			stubColumnMinWidth = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.STUBCOLUMNMINWIDTH,"");

			if (!stubColumnMinWidth.isEmpty())
			{

				valueColumnWidth = ((double) 100 - Double.parseDouble(stubColumnMinWidth))
						/ (tableStructure.getTotalNumberOfColumnsInTable() - 1);

			}
			else
			{
				stubColumnMinWidthRuleExists = false;
			}
		}
		int index = 0;
		String prevbgColor = "";
		String prevBorder = "";
		String prevBorderStyle = "";
		for (TableCell tableCell : rowCellsList)
		{

			for (TableCellType tableCellType : columnCellTypeMap.keySet())
			{

				if (tableCell.isCellType(tableCellType))
				{
					String width = "";
					String align = "";
					String columnIdentifierString = columnCellTypeMap.get(tableCellType);

					if (align.isEmpty())
						align = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString, "align");

					boolean currencyOrGutterFound = false;
					if (tableCellType == TableCellType.OddColumnCell || tableCellType == TableCellType.EvenColumnCell
							&& stubColumnMinWidthRuleExists)
					{
						if (width.isEmpty())
							width = Double.toString(valueColumnWidth);
						if (tableCell.isCellType(TableCellType.CurrencyCell)
								&& !tableCell.isCellType(TableCellType.NumberCell))
						{
							TableColumn tableColumn = tableCell.getTableColumn();
							NodeFilter textFilter = new NodeClassFilter(TextNode.class);
						    NodeList textNodesList = new NodeList();
						    tableColumn.collectInto(textNodesList, textFilter);
						    Node[] textNodeArray = textNodesList.toNodeArray();
						    for (Node currentNode : textNodeArray)
						    {
						    	TextNode currentTextNode = (TextNode) currentNode;
						        String tempText = currentTextNode.getText();

						        if (tempText.startsWith("$") || tableCell.getContent().equals("$")){						        	
						        	if(tempText.startsWith("$")){
							        	currentTextNode.setText("$");						        		
						        	}
									tableCell.setCellStyle(CSSStyle.WIDTH, currencyWidth + "%");
									tableCell.setCellStyle(CSSStyle.TEXTALIGN, currencyAlign);
									tableCell.setTextAlign(currencyAlign);
						        }
						        else{
									tableCell.setCellStyle(CSSStyle.TEXTALIGN, align);
									tableCell.setTextAlign(align);
						        }
						    }
						    
							currencyOrGutterFound = true;
						}
						else if ((tableCell.isCellType(TableCellType.GutterCell) 
								|| tableCell.isCellType(tableCellType.PercentCell))
								&& !tableCell.isCellType(TableCellType.NumberCell) 
								&& !tableCell.isCellType(tableCellType.HeaderCell))
						{
							if(tableCell.getContent().equals(")") || tableCell.getContent().equals("%")
									|| tableCell.getContent().equals("%)")){
								tableCell.setCellStyle(CSSStyle.WIDTH, gutterWidth + "%");
								tableCell.setCellStyle(CSSStyle.TEXTALIGN, gutterAligh);
								tableCell.setTextAlign(gutterAligh);
								currencyOrGutterFound = true;								
							}
							else{
								tableCell.setCellStyle(CSSStyle.WIDTH, width + "%");
								tableCell.setCellStyle(CSSStyle.TEXTALIGN, align);								
							}
						}
						else{
							tableCell.setCellStyle(CSSStyle.WIDTH, width + "%");
							tableCell.setCellStyle(CSSStyle.TEXTALIGN, align);
						}
					}

					String bgcolor = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							"bgcolor");
//					if (!currencyOrGutterFound)
//					{
//						tableCell.setTextAlign(align);
//
//					}
					tableCell.setCellStyle(CSSStyle.BGCOLOR, bgcolor);

					String fontColor = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							TableStyle.FONTCOLOR.getAttributeName());
					String fontSize = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							TableStyle.FONTSIZE.getAttributeName());
					String fontFamily = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							TableStyle.FONTFAMILY.getAttributeName());
					tableCell.setFontStyles(fontColor, fontSize, fontFamily);

					String boldStatus = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							TableStyle.BOLD.getAttributeName());
					if (!boldStatus.isEmpty())
						tableCell.setBoldStatus(Boolean.parseBoolean(boldStatus));
					String italicStatus = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString,
							TableStyle.ITALIC.getAttributeName());
					if (!italicStatus.isEmpty())
						tableCell.setBoldStatus(Boolean.parseBoolean(italicStatus));
					String underlineStatus = tableTemplate.getAttributeValue(rowIdentifierString,
							columnIdentifierString, TableStyle.UNDERLINE.getAttributeName());
					if (!underlineStatus.isEmpty())
						tableCell.setBoldStatus(Boolean.parseBoolean(underlineStatus));
					
					if(rowIdentifierString.contains("header")){
						align = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString, "align");
						width = tableTemplate.getAttributeValue(rowIdentifierString, columnIdentifierString, "width");
						tableCell.setTextAlign(align);
						tableCell.setCellWidth(width);
					}

				}
			}
			
			if(!tableCell.isCellType(TableCellType.HeaderCell) && 
					!tableCell.isCellType(TableCellType.StubCell)){
						if(!tableCell.getContent().equals("$")){
							tableCell.removeCellStyle(CSSStyle.TEXTINDENT);
							//tableCell.removeCellStyle(CSSStyle.MARGIN);
							tableCell.removeCellStyle(CSSStyle.TEXTALIGN);
							tableCell.removeCellStyle(CSSStyle.PADDINGLEFT);
							tableCell.removeCellStyle(CSSStyle.BORDERWIDTH);
							tableCell.removeCellStyle(CSSStyle.BORDERSTYLE);										
						}
				}
				
				if(tableCell.isCellType(TableCellType.StubCell)){
					tableCell.removeWidth();
				}
			index++;
		}
	}

	public String getModifiedHTML()
	{
		return modifiedHTML.toString();
	}
}
