package htmltemplating.table;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.table.TableCell.TableCellType;
import htmltemplating.table.TableTemplate.TableStyle;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;


public class TableStructure
{

	private boolean isHeaderExist = false;
	private boolean isStubExist = false;
	private boolean isCurrencyCellExist = false;
	private boolean isGutterCellExist = false;
	private boolean isCurrencyGutterCellPrefixExist	= false;
	private boolean isCurrencyGutterVerticalAlignExist = false;
	private String	CURRENCY_COLUMN	= "currency";
	private String	GUTTER_COLUMN	= "gutter";
	public int[] minimumColspanForColumn;
	
	private ArrayList<TableCell> rowStructures = new ArrayList<TableCell>();
	private ArrayList<ArrayList<TableCell>> tableRows = new ArrayList<ArrayList<TableCell>>();
	private final TableTag tableTag;

	public TableStructure(TableTag tableTag)
	{
		this.tableTag = tableTag;
		int numRows = tableTag.getRowCount();

		for (int rowIndex = 0; rowIndex < numRows; rowIndex++)
		{
			if (rowIndex == 0)
			{
				ArrayList<TableCell> rowCells = new ArrayList<TableCell>();
				TableRow currentRow = tableTag.getRow(rowIndex);
				TableColumn[] tableColumns = currentRow.getColumns();
				for (TableColumn tableColumn : tableColumns)
				{
					TableCell currentCell = new TableCell(tableColumn);
					rowCells.add(currentCell);
					if (currentCell.getIsColSpan())
					{
						int colspan = currentCell.getCellColSpan();
						for (int colSpanIndex = 0; colSpanIndex < colspan - 1; colSpanIndex++)
						{
							rowCells.add(new TableCellSpan(tableColumn, 0, colSpanIndex + 1));
						}
					}
				}
				tableRows.add(rowCells);
			}
			else
			{
				ArrayList<TableCell> rowCells = new ArrayList<TableCell>();
				ArrayList<TableCell> aboveRowCells = tableRows.get(rowIndex - 1);
				TableRow currentRow = tableTag.getRow(rowIndex);
				int numColumns = aboveRowCells.size();
				TableColumn[] tableColumns = currentRow.getColumns();
				int currentTableColumnIndex = 0;
				for (int cellIndex = 0; cellIndex < numColumns; cellIndex++)
				{
					TableCell aboveCell = aboveRowCells.get(cellIndex);
					if (aboveCell.getIsRowSpan() && aboveCell.rowSpanIndex < aboveCell.getRowSpan())
					{
						TableColumn referenceTableColumn = aboveCell.getTableColumn();
						rowCells.add(new TableCellSpan(referenceTableColumn, aboveCell.rowSpanIndex + 1,
								aboveCell.colSpanIndex));
					}
					else
					{
						if (currentTableColumnIndex < tableColumns.length)
						{
							TableColumn tableColumn = tableColumns[currentTableColumnIndex++];
							TableCell currentCell = new TableCell(tableColumn);
							rowCells.add(currentCell);
							if (currentCell.getIsColSpan())
							{
								int colspan = currentCell.getCellColSpan();
								for (int colSpanIndex = 0; colSpanIndex < colspan - 1; colSpanIndex++)
								{
									rowCells.add(new TableCellSpan(tableColumn, 0, colSpanIndex + 1));
								}
							}
						}
						else
						{
							rowCells.add(new TableCellAbsent());
						}
					}
				}
				tableRows.add(rowCells);
			}
		}
	}

	private String getStyleString()
	{
		return tableTag.getAttribute("style");
	}

	public String toHTML()
	{
		return null;
	}

	public ArrayList<ArrayList<TableCell>> getTableCells()
	{
		return tableRows;
	}

	public void setIsHeaderExist(boolean isExist)
	{
		isHeaderExist = isExist;
	}

	public boolean getIsHeaderExist()
	{
		return isHeaderExist;
	}

	public void setIsStubExist(boolean isExist)
	{
		isStubExist = isExist;
	}

	public boolean getIsStubExist()
	{
		return isStubExist;
	}

	public void setIsCurrencyCellExist(boolean isExist)
	{
		isCurrencyCellExist = isExist;
	}

	public boolean getIsCurrencyCellExist()
	{
		return isCurrencyCellExist;
	}

	public void setIsGutterCellExist(boolean isExist)
	{
		isGutterCellExist = isExist;
	}

	public boolean getIsGutterCellExist()
	{
		return isGutterCellExist;
	}

	public ArrayList<TableCell> getRowByIndex(int row)
	{
		ArrayList<TableCell> rowArrayList = new ArrayList<TableCell>();
		rowArrayList = tableRows.get(row);

		return rowArrayList;
	}

	public ArrayList<TableCell> getCellsByType(TableCellType tableCellType)
	{
		ArrayList<TableCell> cellsList = new ArrayList<TableCell>();
		for (ArrayList<TableCell> rowCells : tableRows)
		{
			for (TableCell cell : rowCells)
			{
				if (cell.isCellType(tableCellType))
					cellsList.add(cell);
			}
		}
		return cellsList;
	}

	@Deprecated
	public ArrayList<ArrayList<TableCell>> getRowsByType(String rowType)
	{
		ArrayList<ArrayList<TableCell>> rowsArrayList = new ArrayList<ArrayList<TableCell>>();
		ArrayList<TableCell> row = new ArrayList<TableCell>();

		if (rowType.equals("header"))
		{
			row = tableRows.get(0);
			rowsArrayList.add(row);
		}
		else if (rowType.equals("even"))
		{
			for (int i = 1; i <= tableRows.size(); i++)
			{
				if (i % 2 == 0)
				{
					row = tableRows.get(i);
					rowsArrayList.add(row);
				}
			}
		}
		else if (rowType.equals("odd"))
		{
			for (int i = 1; i <= tableRows.size(); i++)
			{
				if (i % 2 != 0)
				{
					row = tableRows.get(i);
					rowsArrayList.add(row);
				}
			}
		}

		return rowsArrayList;
	}

	public ArrayList<TableCell> getColumnByIndex(int column)
	{
		ArrayList<TableCell> columnArrayList = new ArrayList<TableCell>();

		for (int i = 0; i < tableRows.size(); i++)
		{
			TableCell cell = tableRows.get(i).get(column);
			columnArrayList.add(cell);
		}

		return columnArrayList;
	}

	public ArrayList<ArrayList<TableCell>> getColumnsByType(String colType)
	{
		ArrayList<ArrayList<TableCell>> columnsArrayList = new ArrayList<ArrayList<TableCell>>();
		ArrayList<TableCell> column = new ArrayList<TableCell>();

		if (colType.equals("stub"))
		{
			for (int i = 0; i < tableRows.size(); i++)
			{
				TableCell cell = tableRows.get(i).get(0);
				column.add(cell);
			}

			columnsArrayList.add(column);

		}
		else if (colType.equals("even"))
		{
			for (int j = 1; j <= rowStructures.size(); j++)
			{
				for (int i = 0; i < tableRows.size(); i++)
				{
					if (j % 2 == 0)
					{
						TableCell cell = tableRows.get(i).get(j);
						column.add(cell);
					}
				}

				columnsArrayList.add(column);
			}
		}
		else if (colType.equals("odd"))
		{
			for (int j = 1; j <= rowStructures.size(); j++)
			{
				for (int i = 0; i < tableRows.size(); i++)
				{
					if (j % 2 != 0)
					{
						TableCell cell = tableRows.get(i).get(j);
						column.add(cell);
					}
				}

				columnsArrayList.add(column);
			}
		}

		return columnsArrayList;
	}

	public int getNumberOfColumnInRow(int row)
	{
		return tableRows.get(row).size();
	}

	public TableCell getCellByIndex(int row, int column)
	{
		TableCell cell = null;

		try
		{
			cell = tableRows.get(row).get(column);
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}

		return cell;
	}

	public int getTotalNumberOfRowsInTable()
	{
		return tableRows.size();
	}

	public int getTotalNumberOfColumnsInTable()
	{
		return tableRows.get(0).size();
	}

	public int getStyleTemplateId() throws UnparsableStyleException
	{
		try
		{
			return Integer.parseInt(StyleStringProcessor.getCSSStyle(getStyleString(), CSSStyle.STYLETEMPLATEID));
		}
		catch (NumberFormatException e)
		{
			System.out.println("TableStructure-->getStyleTemplateId()-->NumberFormatException");
			return -1;
			//throw new UnparsableStyleException("given style-template-id is not parsable as int");
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
		this.tableTag.setAttribute("style", styleString);
	}
	
	public ArrayList<ArrayList<TableCell>> cloneTableRows()
	{
		ArrayList<ArrayList<TableCell>> rowsList = new ArrayList<ArrayList<TableCell>>();
		
		for (ArrayList<TableCell> rowCells : tableRows)
		{
			ArrayList<TableCell> cellsList = new ArrayList<TableCell>();
			for (TableCell cell : rowCells)
			{
				cellsList.add(cell);
			}
			rowsList.add(cellsList);
			
		}
		return rowsList;
	}
	
	
	
	public int[] updateCurrencyColumnIndexArray(int currentIndexValue, int[] currencyColumnArray)
	{
		int currencyLen	= currencyColumnArray.length;
		for(int curIndex = 0; curIndex < currencyLen; curIndex++)
		{
			if(currentIndexValue >= currencyColumnArray[curIndex])
				currencyColumnArray[curIndex] = currencyColumnArray[curIndex]+1;
		}
		return currencyColumnArray;
	}
	
	private int getColspanValue(TableColumn tableColumn)
	{
		int colspan	= 1;
		String strColspan	= "";
		try
		{
			strColspan = tableColumn.getAttribute("colspan");
			colspan = Integer.parseInt(strColspan);
		}
		catch(Exception ex)
		{
			//System.out.println("EXCEPTION: "+ex.getMessage()+" getColspanValue: "+strColspan);
		}
		
		return colspan;
	}
	
	private int getRowspanValue(TableColumn tableColumn)
	{
		int rowspan	= 1;
		String strRowspan	= "";
		try
		{
			strRowspan = tableColumn.getAttribute("rowspan");
			rowspan = Integer.parseInt(strRowspan);
		}
		catch(Exception ex)
		{
			//System.out.println("EXCEPTION: "+ex.getMessage()+" getColspanValue: "+strColspan);
		}
		
		return rowspan;
	}
	
	private int getTotalNumberOfCell(NodeList tableColumnList)
	{
		int noOfCell = 0;
		int cellSize = tableColumnList.size();
		noOfCell = cellSize;
		//System.out.println("physical size: "+cellSize);
		for (int columnIndex = 0; columnIndex < cellSize; columnIndex++)
		{
			TableColumn tempCurColumn				= (TableColumn) tableColumnList.elementAt(columnIndex);
			int colspan = this.getColspanValue(tempCurColumn);
			if(colspan > 1)	noOfCell += (colspan-1);
		}
		//System.out.println("original size: "+noOfCell);
		return noOfCell;
	}
	
	private TableColumn createTableColumnByText(String tagText, Node parentNode)
	{
		TableColumn newTableColumn	= null;
		TextNode textNode = new TextNode(tagText);
		newTableColumn = new TableColumn();
		newTableColumn.setChildren(new NodeList(textNode));
		newTableColumn.setParent(parentNode);
		return newTableColumn;
	}
	
	
	private void updateTableColumnText(TableColumn tableColumnNode, String newText)
	{
		NodeFilter textFilter = new NodeClassFilter(TextNode.class);
		NodeList textNodesList = new NodeList();
		tableColumnNode.collectInto(textNodesList, textFilter);
		Node[] textNodeArray = textNodesList.toNodeArray();
		for (Node currentNode : textNodeArray)
		{
			TextNode currentTextNode = (TextNode) currentNode;
			//System.out.println("currentText1: "+currentTextNode.getText());
			currentTextNode.setText(newText);
			//System.out.println("currentText2: "+currentTextNode.getText());
		}
	}
	
	private void cleanCurrencyColumn(TableCell currentCell)
	{ 
		String currencyCellInitText = "";
		String columnSymbol = "$";
		String currentCellText = currentCell.getContent().trim();
		if(currentCellText.startsWith(" ") && currentCellText.contains(columnSymbol)){
			currentCellText = currentCellText.substring(currentCellText.indexOf(columnSymbol));
		}
		//if (currentCellText.startsWith("$") || currentCellText.startsWith(" $"))
		if (currentCellText.startsWith(columnSymbol))	
		{	
			isCurrencyGutterCellPrefixExist	= true;
			currentCellText = currentCellText.replace(columnSymbol, "");
			currentCell.setContent(currentCellText);

			TableColumn nextNode = currentCell.getTableColumn();
			NodeFilter textFilter = new NodeClassFilter(TextNode.class);
			NodeList textNodesList = new NodeList();
			nextNode.collectInto(textNodesList, textFilter);
			Node[] textNodeArray = textNodesList.toNodeArray();
			for (Node currentNode : textNodeArray)
			{
				TextNode currentTextNode = (TextNode) currentNode;
				String tempText = currentTextNode.getText();
				tempText = tempText.trim();
				
				if (tempText.startsWith(columnSymbol))
					tempText = tempText.replace(columnSymbol, "");
				currentTextNode.setText(tempText);
			}
		}
		
	}
	
	private String cleanGutterColumn(TableCell currentCell)
	{ 
		TableColumn currentTableColumn	= currentCell.getTableColumn();
		String gutterCellInitText = "";
		String currentCellText = currentCell.getContent().trim();
		if(!currentCell.isCellType(TableCellType.HeaderCell)){
			if(currentCell.isCellType(TableCellType.NumberCell)){
				if (currentCellText.endsWith(")"))
				{
					if(currentCellText.endsWith("%)")){
						gutterCellInitText = "%)";
						currentCellText = currentCellText.replace("%)", "");
					}
					else{
						gutterCellInitText = ")";
						currentCellText = currentCellText.replace(")", "");							
					}
					currentCell.setContent(currentCellText);
		
					//TableColumn nextNode = currentCell.getTableColumn();
					
					NodeFilter textFilter = new NodeClassFilter(TextNode.class);
					NodeList textNodesList = new NodeList();
					currentTableColumn.collectInto(textNodesList, textFilter);
					Node[] textNodeArray = textNodesList.toNodeArray();
					for (Node currentNode : textNodeArray)
					{
						TextNode currentTextNode = (TextNode) currentNode;
						String tempText = currentTextNode.getText();
						if (tempText.endsWith("%)")){
							tempText = tempText.replace("%)", "");
						}
						else{
							tempText = tempText.replace(")", "");
						}
						currentTextNode.setText(tempText);
					}
		
				}
				if (currentCellText.endsWith("%"))
				{
					gutterCellInitText = "%";
					currentCellText = currentCellText.replace("%", "");
					currentCell.setContent(currentCellText);
		
					//TableColumn nextNode = (TableColumn) tableColumnList.elementAt(columnIndex);
					NodeFilter textFilter = new NodeClassFilter(TextNode.class);
					NodeList textNodesList = new NodeList();
					currentTableColumn.collectInto(textNodesList, textFilter);
					Node[] textNodeArray = textNodesList.toNodeArray();
					for (Node currentNode : textNodeArray)
					{
						TextNode currentTextNode = (TextNode) currentNode;
						String tempText = currentTextNode.getText();
						if (tempText.endsWith("%"))
							tempText = tempText.replace("%", "");
						currentTextNode.setText(tempText);
					}
				}
			}			
		}
		return gutterCellInitText;
	}
	
	private String getCellVerticalAlignmentStyle(TableCell currentCell)
	{
		String verticalAlignStyleString = "";
		TableColumn currentColumn = currentCell.getTableColumn();
		String styleString = currentColumn.getAttribute("style");
		Pattern p = Pattern.compile("vertical-align:", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(styleString);
		if(m.find())
		{
			verticalAlignStyleString = styleString.substring(m.end());
			isCurrencyGutterVerticalAlignExist = true;
		}
		return verticalAlignStyleString;
	}
	
	private void applyTemplatingForCell(TableCell currentTableCell, TableCell newTableCell, TableColumn newTableColumn, TableTemplate tableTemplate, int rowIndex, int columnIndex, String columnType, boolean addNewColumn)
	throws UnparsableStyleException, StyleNotDefinedException
	{
		String borderWidth = "";
		String borderStyle 	= "";
		if(borderWidth.isEmpty())
		{
			try
			{	
				borderWidth	= currentTableCell.getCellStyle(CSSStyle.BORDER);
				borderStyle	= currentTableCell.getCellStyle(CSSStyle.BORDERSTYLE);
			}
			catch (CellStyleNotFoundException e1)
			{
				//e1.printStackTrace();
				//System.out.println("TableStructure->applyTemplatingForCell => "+e1.getMessage());
			}
		}
		String rowStyleTag = "odd";
		String colStyleTag = "odd";
				
		//if (rowIndex % 2 == 0)		rowStyleTag = "even";
		//if (columnIndex % 2 == 0)	colStyleTag = "odd";
		if(currentTableCell.isCellType(TableCellType.EvenRowCell))
		{
			rowStyleTag = "even";
		}
		else if(currentTableCell.isCellType(TableCellType.OddRowCell))
		{
			rowStyleTag = "odd";
		}
		if(currentTableCell.isCellType(TableCellType.OddColumnCell))
		{
			colStyleTag = "odd";
		}
		else if(currentTableCell.isCellType(TableCellType.EvenColumnCell))
		{
			colStyleTag = "even";
		}
		if(currentTableCell.isCellType(TableCellType.HeaderCell))
		{
			rowStyleTag = "header";
		}
				
		String logStr = "["+rowIndex+"]["+columnIndex+"] ";
		logStr +=" rowStyleTag: "+rowStyleTag+" colStyleTag: "+colStyleTag;
		//System.out.println(logStr);
				
		String width 			= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, "width");
		String align 			= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, "align");
		String bgcolor 			= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, "bgcolor");
		String boldStatus 		= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.BOLD.getAttributeName());
		String italicStatus 	= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.ITALIC.getAttributeName());
		String underlineStatus 	= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.UNDERLINE.getAttributeName());
		String fontColor 		= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.FONTCOLOR.getAttributeName());
		String fontSize 		= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.FONTSIZE.getAttributeName());
		String fontFamily 		= tableTemplate.getAttributeValue(rowStyleTag, colStyleTag, TableStyle.FONTFAMILY.getAttributeName());
		
		String templetApplyStatus = "";
		if(columnType.equals(CURRENCY_COLUMN))
		{
			templetApplyStatus = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_IS_CREATE);
			if (!templetApplyStatus.isEmpty())
			{
				if (Boolean.parseBoolean(templetApplyStatus))
				{
					width = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_WIDTH);
					align = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_ALIGNMENT);
				}
			}
		}
		else if(columnType.equals(GUTTER_COLUMN))
		{
			templetApplyStatus = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_IS_CREATE);
			if (!templetApplyStatus.isEmpty())
			{
				if (Boolean.parseBoolean(templetApplyStatus))
				{
					width = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_WIDTH);
					align = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_ALIGNMENT);
				}
			}
		}
				
		newTableColumn.setAttribute("align", "\""+align+"\"");
		isCurrencyGutterVerticalAlignExist = false;
		String verticalAlignStyleString = this.getCellVerticalAlignmentStyle(currentTableCell);
		String verticalAlign = "";
		if(isCurrencyGutterVerticalAlignExist)
		{
			StringTokenizer st = new StringTokenizer(verticalAlignStyleString, " ;");
			verticalAlign = st.nextToken();
		}
				
		if(addNewColumn)
		{
			String rawCellText = newTableCell.getContent();
			String newText = "<p style=\"text-align:"+align+"; vertical-align:"+verticalAlign+"; text-indent:0px; padding-left:0px; margin: 0px 0px 0px 0px;\">";
			//newText = newText.concat("<font style=\"font-size:"+fontSize+";font-family:"+fontFamily+";color:"+fontColor+";\">");
			//newText = newText.concat(rawCellText+"</font></div></TD>");
			newText = newText.concat(rawCellText+"</p></TD>");
			this.updateTableColumnText(newTableColumn, newText);
			newTableCell.setContent(newText);
			//System.out.println("newTableCell: "+newTableCell.getContent());
		}
				
				
		newTableCell.setCellStyle(CSSStyle.BGCOLOR, bgcolor);
		newTableCell.setCellStyle(CSSStyle.WIDTH, width + "%");
		//newTableCell.setCellStyle(CSSStyle.BGCOLOR, bgcolor);
		//newTableCell.setCellStyle(CSSStyle.BORDERWIDTH, borderWidth);
//		if(!borderStyle.contains("none"))
//			newTableCell.setCellStyle(CSSStyle.BORDER, "1px solid black");
		//newTableCell.setCellStyle(CSSStyle.BORDERSTYLE,borderStyle);
		if(borderWidth.isEmpty()){
			newTableCell.setCellStyle(CSSStyle.BORDERWIDTH, "1.0px 1.0px 1.0px 1.0px");
		}
		else{
			newTableCell.setCellStyle(CSSStyle.BORDERWIDTH, borderWidth);
		}
		if(borderStyle.isEmpty()){
			newTableCell.setCellStyle(CSSStyle.BORDERSTYLE, "none none none none");			
		}
		else{
			newTableCell.setCellStyle(CSSStyle.BORDERSTYLE, borderStyle);
		}
		//newTableCell.setCellStyle(CSSStyle.FONTSIZE, fontSize);
		//newTableCell.setCellStyle(CSSStyle.FONTFAMILY, fontFamily);
		//newTableCell.setCellStyle(CSSStyle.FONTCOLOR, fontColor);
		newTableCell.setCellStyle(CSSStyle.VERTICALALIGN, verticalAlign);
		newTableCell.setTextAlign(align);
		newTableCell.setBoldStatus(boldStatus);
		newTableCell.setItalicStatus(Boolean.parseBoolean(italicStatus));
		newTableCell.setUnderlineStatus(Boolean.parseBoolean(underlineStatus));
	}
	
	private void addCurrencyColumnNew(int[] currencyColumnIndexArray, TableTemplate tableTemplate)
	throws UnparsableStyleException, StyleNotDefinedException
	{
		NodeList tableRowList = new NodeList();
		NodeFilter tableRowFilter = new TagNameFilter("TR");
		tableTag.collectInto(tableRowList, tableRowFilter);
		Node[] tableRowNodeArray = tableRowList.toNodeArray();
		ArrayList<TableCell> rowCells = null;
		
		int rowIndex			= 0;
		int cellColumnIndex		= 0;
		int nodeColumnIndex		= 0;
		int currencyArrayIndx	= 0;
		int columnIndexReference	= 0;
		int totalCurrencyColumn		= 0;
		String newAddedCellSymbol	= "$";
		//int[] tempCurrencyColumnIndexArray = currencyColumnIndexArray;
		ArrayList<ArrayList<TableCell>> newTableRows = this.cloneTableRows();
		for (Node currentTableRow : tableRowNodeArray)
		{
			currencyArrayIndx = 0;
			columnIndexReference = currencyColumnIndexArray[currencyArrayIndx];
			totalCurrencyColumn		= currencyColumnIndexArray.length;
			NodeList tableColumnList = new NodeList();
			NodeFilter tableColumnFilter = new TagNameFilter("TD");
			currentTableRow.collectInto(tableColumnList, tableColumnFilter);
			rowCells = tableRows.get(rowIndex);
			
			NodeList newTableColumnList = new NodeList();
			
			int noOfCell	= getTotalNumberOfCell(tableColumnList);
			TableColumn tempCurColumn	= null;
			boolean colspanIsContinue = false;
			int startColspanIndex	= -1;
			int colspanRemaining = 0;
			nodeColumnIndex	= 0;
			String logRowCol = "";
			int numberOfCurrencyColumnInARow = 0;
			//System.out.println("============================");
			
			for (cellColumnIndex = 0; cellColumnIndex < noOfCell; cellColumnIndex++)
			{
				TableCell tempCurCell		= rowCells.get(cellColumnIndex);
				boolean isColspan = tempCurCell.isColSpan;
				//int currentColspan	= tempCurCell.getCellColSpan();
				String strColspan = "";
				logRowCol = " ["+rowIndex+"]["+cellColumnIndex+"]["+nodeColumnIndex+"]";
				int currentColspan = 1;
				if(colspanRemaining == 0)
				{
					tempCurColumn				= (TableColumn) tableColumnList.elementAt(nodeColumnIndex);
					currentColspan = this.getColspanValue(tempCurColumn);
					if(currentColspan > 1)
					{
						colspanRemaining = (currentColspan - 1);
						startColspanIndex	= cellColumnIndex;
						nodeColumnIndex++;
					}
					else
					{
						startColspanIndex = -1;
						nodeColumnIndex++;
					}	
				}
				else
				{
					currentColspan	= this.getColspanValue(tempCurColumn);
					colspanRemaining--;
				}
				
				String strCurCellText = tempCurCell.getContent().toString();
				//String strCurNodeText = tempCurColumn.getText().toString();
				//if(rowIndex==1)
				//System.out.println(logRowCol+" columnIndexReference:"+columnIndexReference+" strCurCellText: "+strCurCellText+" colspanIsContinue: "+colspanIsContinue+" isColspan: "+isColspan+" startColspanIndex: "+startColspanIndex+" colspanRemaining: "+colspanRemaining+" currentColspan: "+currentColspan);
				if(cellColumnIndex == columnIndexReference)
				{
					TableCell newTableCell		= null;
					TableColumn newTableColumn	= null;
					
					this.cleanCurrencyColumn(tempCurCell);
					String newCurrencyColumnText = "";
					if(isCurrencyGutterCellPrefixExist)
					{
						newCurrencyColumnText = newAddedCellSymbol;
						isCurrencyGutterCellPrefixExist = false;
					}
					boolean addNewColumn = false; 
					if(startColspanIndex==-1 || startColspanIndex == columnIndexReference)
					{
						//if(rowIndex==1)
						//System.out.println("           Add new Currency Column startColspanIndex: "+startColspanIndex+" columnIndexReference: "+columnIndexReference);
						addNewColumn = true;
						newTableColumn = this.createTableColumnByText(newCurrencyColumnText, currentTableRow);
						TagNode currentNode = (TagNode) newTableColumn;
						newTableColumnList.add(currentNode);
					}
					else
					{
						//System.out.println("          Increase Colspan Value: "+currentColspan+" startColspanIndex: "+startColspanIndex+" columnIndexReference: "+columnIndexReference);
						tempCurColumn.setAttribute("colspan",Integer.toString(currentColspan+1));
						newTableCell = new TableCell(tempCurColumn);
						newTableColumn	= tempCurColumn;
					}
					newTableCell = new TableCell(newTableColumn);
					this.applyTemplatingForCell(tempCurCell, newTableCell, newTableColumn, tableTemplate, rowIndex, cellColumnIndex, CURRENCY_COLUMN, addNewColumn);
					
					
					currencyArrayIndx++;
					if (currencyArrayIndx < totalCurrencyColumn)
						columnIndexReference = currencyColumnIndexArray[currencyArrayIndx];
					else
						columnIndexReference = -1;
					int newCellColumnIndex = cellColumnIndex+numberOfCurrencyColumnInARow;
					newTableRows.get(rowIndex).add( newCellColumnIndex , newTableCell);
					numberOfCurrencyColumnInARow++;
					
				}
				if(colspanRemaining==0)
					newTableColumnList.add(tempCurColumn);
			}
			currentTableRow.getChildren().removeAll();
			currentTableRow.setChildren(newTableColumnList);
			rowIndex++;
		}
		tableRows = newTableRows;
		
		/*for(int ci = 0; ci<tableRows.get(1).size(); ci++)
		{
			System.out.println("row: 1 column: "+ci+" text: "+tableRows.get(1).get(ci).getContent());
		}*/
	}
	
	private void addGutterColumnNew(int[] currencyColumnIndexArray, TableTemplate tableTemplate)
	throws UnparsableStyleException, StyleNotDefinedException
	{
		NodeList tableRowList = new NodeList();
		NodeFilter tableRowFilter = new TagNameFilter("TR");
		tableTag.collectInto(tableRowList, tableRowFilter);
		Node[] tableRowNodeArray = tableRowList.toNodeArray();
		ArrayList<TableCell> rowCells = null;
		
		int rowIndex			= 0;
		int cellColumnIndex		= 0;
		int nodeColumnIndex		= 0;
		int currencyArrayIndx	= 0;
		int columnIndexReference	= 0;
		int totalCurrencyColumn		= 0;
		String newAddedCellSymbol	= "$";
		//int[] tempCurrencyColumnIndexArray = currencyColumnIndexArray;
		ArrayList<ArrayList<TableCell>> newTableRows = this.cloneTableRows();
		for (Node currentTableRow : tableRowNodeArray)
		{
			currencyArrayIndx = 0;
			columnIndexReference = currencyColumnIndexArray[currencyArrayIndx];
			totalCurrencyColumn		= currencyColumnIndexArray.length;
			NodeList tableColumnList = new NodeList();
			NodeFilter tableColumnFilter = new TagNameFilter("TD");
			currentTableRow.collectInto(tableColumnList, tableColumnFilter);
			rowCells = tableRows.get(rowIndex);
			
			NodeList newTableColumnList = new NodeList();
			
			int noOfCell	= getTotalNumberOfCell(tableColumnList);
			//System.out.println("noOfCell: "+noOfCell +"");
			TableColumn tempCurColumn	= null;
			boolean colspanIsContinue = false;
			int startColspanIndex	= -1;
			int endColspanIndex		= -1;
			int colspanRemaining = 0;
			int numberOfGutterColumnInARow = 0;
			nodeColumnIndex	= 0;
			String logRowCol = "";
			//System.out.println("============================");
			for (cellColumnIndex = 0; cellColumnIndex < noOfCell; cellColumnIndex++)
			{
				TableCell tempCurCell		= rowCells.get(cellColumnIndex);
				boolean isColspan = tempCurCell.isColSpan;
				//int currentColspan	= tempCurCell.getCellColSpan();
				String strColspan = "";
				logRowCol = " ["+rowIndex+"]["+cellColumnIndex+"]["+nodeColumnIndex+"]";
				int currentColspan = 1;
				if(colspanRemaining == 0)
				{
					tempCurColumn				= (TableColumn) tableColumnList.elementAt(nodeColumnIndex);
					currentColspan = this.getColspanValue(tempCurColumn);
					if(currentColspan > 1)
					{
						colspanRemaining = (currentColspan - 1);
						startColspanIndex	= cellColumnIndex;
						endColspanIndex		= cellColumnIndex + colspanRemaining;
						nodeColumnIndex++;
					}
					else
					{
						startColspanIndex	= -1;
						endColspanIndex		= -1;
						nodeColumnIndex++;
					}	
				}
				else
				{
					currentColspan	= this.getColspanValue(tempCurColumn);
					colspanRemaining--;
				}
				
				String strCurCellText = tempCurCell.getContent().toString();
				//String strCurNodeText = tempCurColumn.getText().toString();
				//if(rowIndex==1)
				//System.out.println(logRowCol+" columnIndexReference:"+columnIndexReference+" strCurCellText: "+strCurCellText+" colspanIsContinue: "+colspanIsContinue+" isColspan: "+isColspan+" startColspanIndex: "+startColspanIndex+" endColspanIndex: "+endColspanIndex+" colspanRemaining: "+colspanRemaining+" currentColspan: "+currentColspan);
				if(colspanRemaining==0)
					newTableColumnList.add(tempCurColumn);
				if(cellColumnIndex == columnIndexReference)
				{
					TableCell newTableCell		= null;
					TableColumn newTableColumn	= null;
					String newCurrencyColumnText = "";
					newCurrencyColumnText = this.cleanGutterColumn(tempCurCell);
					
					if(isCurrencyGutterCellPrefixExist)
					{
						//newCurrencyColumnText = newAddedCellSymbol;
						isCurrencyGutterCellPrefixExist = false;
					}
					
					
					//if(startColspanIndex==-1 || startColspanIndex == columnIndexReference)
					boolean addNewColumn = false; 
					if(endColspanIndex==-1 || endColspanIndex == columnIndexReference)
					{
						//if(rowIndex==1)
						//System.out.println("           Add new Gutter Column startColspanIndex: "+startColspanIndex+" columnIndexReference: "+columnIndexReference);
						addNewColumn	= true;
						newTableColumn = this.createTableColumnByText(newCurrencyColumnText, currentTableRow);
						TagNode currentNode = (TagNode) newTableColumn;
						newTableColumnList.add(currentNode);
					}
					else
					{
						//System.out.println("          Increase Colspan Value: "+currentColspan+" startColspanIndex: "+startColspanIndex+" columnIndexReference: "+columnIndexReference);
						tempCurColumn.setAttribute("colspan",Integer.toString(currentColspan+1));
						newTableCell = new TableCell(tempCurColumn);
						newTableColumn	= tempCurColumn;
					}
					newTableCell = new TableCell(newTableColumn);
					
					this.applyTemplatingForCell(tempCurCell, newTableCell, newTableColumn, tableTemplate, rowIndex, cellColumnIndex, GUTTER_COLUMN, addNewColumn);
					
					currencyArrayIndx++;
					if (currencyArrayIndx < totalCurrencyColumn)
						columnIndexReference = currencyColumnIndexArray[currencyArrayIndx];
					else
						columnIndexReference = -1;
					
					int newCellColumnIndex = cellColumnIndex + numberOfGutterColumnInARow + 1;
					//if(rowIndex==1)
					//System.out.println("------newCellColumnIndex: "+newCellColumnIndex+" cellColumnIndex: "+cellColumnIndex+" numberOfGutterColumnInARow: "+numberOfGutterColumnInARow);
					newTableRows.get(rowIndex).add( newCellColumnIndex , newTableCell);
					numberOfGutterColumnInARow++;
				}
				
			}
			currentTableRow.getChildren().removeAll();
			currentTableRow.setChildren(newTableColumnList);
			
			rowIndex++;
			//tempCurrencyColumnIndexArray = currencyColumnIndexArray;
		}
		tableRows = newTableRows;
		/*System.out.println("====Gutter Column Processing End");
		for(int ci = 0; ci<tableRows.get(1).size(); ci++)
		{
			System.out.println("row: 1 column: "+ci+" text: "+tableRows.get(1).get(ci).getContent());
		}*/
	}

	
	
	public void processAddingCurrencyColumn(TableTemplate tableTemplate) throws UnparsableStyleException,
			StyleNotDefinedException
	{
		int rowsSize = tableRows.size();
		int cellSize = 0;
		ArrayList<TableCell> rowCells = null;
		TableCell currentCell = null;
		TableCell previousCell = null;
		boolean isCurrencyCell = false;
		boolean isNumberCell = false;
		boolean isHeaderCell = false;
		boolean isGutterCell = false;
		
		String currencyColumnIndexes = "";
		String existingCurrencyColumnIndexes = "";
		
		for (int rowIndx = 0; rowIndx < 1; rowIndx++)
		{
			rowCells = tableRows.get(rowIndx);
			cellSize = rowCells.size();
			isCurrencyCell = false;
			isNumberCell = false;
			for (int colIndx = 0; colIndx < cellSize; colIndx++)
			{
				currentCell = rowCells.get(colIndx);
				if (colIndx > 0) {
					previousCell = rowCells.get(colIndx - 1);
				}
				isCurrencyCell = currentCell.isCellType(TableCellType.CurrencyCell);
				isNumberCell = currentCell.isCellType(TableCellType.NumberCell);
				isHeaderCell = currentCell.isCellType(TableCellType.HeaderCell);
				isGutterCell = currentCell.isCellType(TableCellType.GutterCell);
				
				if (isHeaderCell) {
					isNumberCell = isNumberCellAvailable(colIndx);
				}
				if (isNumberCell) {
					boolean prevIsNumberCell = isNumberCellAvailable(colIndx - 1);
					try {
						if (!(previousCell.isCellType(TableCellType.CurrencyCell) && !prevIsNumberCell)) {
							currencyColumnIndexes += colIndx + "#";
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e.getMessage());
					}
				}
				if (isCurrencyCell) {
					existingCurrencyColumnIndexes += colIndx + "#";
				}
			}
		}

		if (currencyColumnIndexes.length() > 0) {
			String[] curIndexArr = currencyColumnIndexes.split("#");
			int totalCurrencyLen = curIndexArr.length;
			int[] curIndxIntArr = new int[totalCurrencyLen];
			for (int indx = 0; indx < totalCurrencyLen; indx++) {
				curIndxIntArr[indx] = Integer.parseInt(curIndexArr[indx]);
			}
			if (totalCurrencyLen > 0) {
				addCurrencyColumnNew(curIndxIntArr, tableTemplate);
			}
		} else {
			if (existingCurrencyColumnIndexes.length() > 0) {
				String[] colIndexArr = existingCurrencyColumnIndexes.split("#");
				int totalCurrencyColumn = colIndexArr.length;
				int[] currencyColIndexArr = new int[totalCurrencyColumn];
				for (int i = 0; i < totalCurrencyColumn; i++) {
					currencyColIndexArr[i] = Integer.parseInt(colIndexArr[i]);
				}

				if (totalCurrencyColumn > 0) {
					processExistingCurrencyColumn(rowsSize,
							currencyColIndexArr, tableTemplate);
				}
			}
		}
	}
		
		private void processExistingCurrencyColumn(int rowSize, int[] currencyColIndexArr,
				TableTemplate tableTemplate)
		{
			TableCell currencyCell = null;
			String currencyWidth = "";
			String currencyAlign = "";
			try
			{
				currencyWidth = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_WIDTH);
				currencyWidth = currencyWidth.concat("%");
				currencyAlign = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.CURRENCY_COLUMN_ALIGNMENT);			
			}
			catch (StyleNotDefinedException se) 
			{
				System.out.println("TableTemplateProcessor-->processRowStyles()-->sestyleNotDefinedException-->"+se.getMessage());
			}

			for(int i=0; i<rowSize; i++)
			{
				for(int j=0; j<currencyColIndexArr.length; j++)
				{	
					currencyCell = getCellByIndex(i, currencyColIndexArr[j]);
					try
					{
						currencyCell.setCellStyle(CSSStyle.WIDTH, currencyWidth);
						currencyCell.setCellStyle(CSSStyle.TEXTALIGN, currencyAlign);
					}
					catch (UnparsableStyleException e)
					{
						e.printStackTrace();
					}
				}
			}
	}
		
	public void processAddingGutterColumn(TableTemplate tableTemplate) throws UnparsableStyleException,
			StyleNotDefinedException
	{
		int rowsSize = tableRows.size();
		int cellSize = 0;
		ArrayList<TableCell> rowCells = null;
		TableCell currentCell = null;
		TableCell nextCell = null;
		boolean isCurrencyCell = false;
		boolean isGutterCell = false;
		boolean isNumberCell = false;
		boolean isHeaderCell = false;
		String gutterColumnIndexes = "";
		String existingGutterColumnIndexes = "";
		
		for (int rowIndx = 0; rowIndx < 1; rowIndx++)
		{
			rowCells = tableRows.get(rowIndx);
			cellSize = rowCells.size();
			isGutterCell = false;
			isNumberCell = false;
			for (int colIndx = 0; colIndx < cellSize; colIndx++)
			{
				currentCell = rowCells.get(colIndx);

				isCurrencyCell = currentCell.isCellType(TableCellType.CurrencyCell);
				isGutterCell = currentCell.isCellType(TableCellType.GutterCell);
				isNumberCell = currentCell.isCellType(TableCellType.NumberCell);
				isHeaderCell = currentCell.isCellType(TableCellType.HeaderCell);
				
				if (isHeaderCell) {
					isNumberCell = isNumberCellAvailable(colIndx);
					isGutterCell = isGutterCellAvailable(colIndx);
				}
				if (isNumberCell) {
					if (colIndx < cellSize - 1) {
						boolean nextIsNumberCell = isNumberCellAvailable(colIndx + 1);
						boolean nextIsGutterCell = isGutterCellAvailable(colIndx + 1);
						nextCell = rowCells.get(colIndx + 1);
						if (!(nextIsGutterCell && !nextIsNumberCell)) {
							gutterColumnIndexes += colIndx + "#";
						}
					} else {
						gutterColumnIndexes += colIndx + "#";
					}
				}
				if (isGutterCell) {
					existingGutterColumnIndexes += colIndx + "#";
				}
			}
		}

		if (gutterColumnIndexes.length() > 0)
		{
			String[] curIndexArr = gutterColumnIndexes.split("#");
			int totalCurrencyLen = curIndexArr.length;
			int[] curIndxIntArr = new int[totalCurrencyLen];
			for (int indx = 0; indx < totalCurrencyLen; indx++)
			{
				curIndxIntArr[indx] = Integer.parseInt(curIndexArr[indx]);
			}
			if (totalCurrencyLen > 0)
				addGutterColumnNew(curIndxIntArr, tableTemplate);
		} else {
			if (existingGutterColumnIndexes.length() > 0) {
				String[] colIndexArr = existingGutterColumnIndexes.split("#");
				int totalGutterColumn = colIndexArr.length;
				int[] gutterColIndexArr = new int[totalGutterColumn];
				for (int i = 0; i < totalGutterColumn; i++) {
					gutterColIndexArr[i] = Integer.parseInt(colIndexArr[i]);
				}

				if (totalGutterColumn > 0) {
					processExistingGutterColumn(rowsSize, gutterColIndexArr,
							tableTemplate);
				}
			}
		}
	}
	
	private void processExistingGutterColumn(int rowSize, int[] gutterColIndexArr,
			TableTemplate tableTemplate)
	{
		TableCell gutterCell = null;
		String gutterWidth = "";
		String gutterAlign = "";
		try{
			gutterWidth = tableTemplate.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_WIDTH);
			gutterWidth = gutterWidth.concat("%");
			gutterAlign = tableTemplate
					.getStyleValue(htmltemplating.table.TableStyle.GUTTER_COLUMN_ALIGNMENT);			
		}
		catch (StyleNotDefinedException se) {
			System.out.println("TableTemplateProcessor-->processRowStyles()-->sestyleNotDefinedException-->"+se.getMessage());
		}

		for(int i=0; i<rowSize; i++){
			for(int j=0; j<gutterColIndexArr.length; j++){				
				try
				{
					gutterCell = getCellByIndex(i, gutterColIndexArr[j]);
					gutterCell.setCellStyle(CSSStyle.WIDTH, gutterWidth);
					gutterCell.setCellStyle(CSSStyle.TEXTALIGN, gutterAlign);
				}
				catch (UnparsableStyleException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean isNumberCellAvailable(int columnIndx)
	{
		boolean numberCellAvailable = false;
		int rowsSize = tableRows.size();
		ArrayList<TableCell> rowCells = null;
		TableCell currentCell = null;
		for (int rowIndx = 0; rowIndx < rowsSize; rowIndx++)
		{
			rowCells = tableRows.get(rowIndx);
			try
			{
				currentCell = rowCells.get(columnIndx);
				if (currentCell.isCellType(TableCellType.NumberCell))
				{
					numberCellAvailable = true;
					break;
				}
			}
			catch(Exception ex)
			{
				//System.out.println("rowIndx:"+rowIndx+" isNumberCellAvailable: "+ex.getMessage());
			}
		}
		return numberCellAvailable;
	}
	
	private boolean isGutterCellAvailable(int columnIndx)
	{
		boolean gutterCellAvailable = false;
		int rowsSize = tableRows.size();
		ArrayList<TableCell> rowCells = null;
		TableCell currentCell = null;
		for (int rowIndx = 0; rowIndx < rowsSize; rowIndx++)
		{
			rowCells = tableRows.get(rowIndx);
			try
			{
				currentCell = rowCells.get(columnIndx);
				if (currentCell.isCellType(TableCellType.GutterCell) || currentCell.isCellType(TableCellType.PercentCell))
				{
					gutterCellAvailable = true;
					break;
				}
			}
			catch(Exception ex)
			{
				//System.out.println("rowIndx:"+rowIndx+" isNumberCellAvailable: "+ex.getMessage());
			}
		}
		return gutterCellAvailable;
	}

	private boolean isEmptyColumn(int columnIndex, int minimumColspan)
	{
		int totalNoOfRows	= this.getTotalNumberOfRowsInTable();
		boolean emptyColumn = false;
		TableCell currentCell = null;
		int emptyColumnCount	= 0;
		//minimumColspan = 1;
		for(int rowIndex = 0; rowIndex < totalNoOfRows; rowIndex++)
		{
			currentCell			= tableRows.get(rowIndex).get(columnIndex);
			int tempColspan		= currentCell.getCellColSpan();
			String currentText	= currentCell.getContent().trim();
			boolean isStubCell	= currentCell.isCellType(TableCellType.StubCell);
			if(currentText.equals("&#160;") || currentText.equals("&nbsp;") )
				currentText = "";
			if( ( currentText.isEmpty() || tempColspan > minimumColspan) && isStubCell == false) 
				emptyColumnCount++;
			else if(currentText.length() > 1)
				break;
			//System.out.println("["+rowIndex+"]["+columnIndex+"]"+ " currentText: ||"+currentText+"|| colspan: "+tempColspan+" emptyColumnCount: "+emptyColumnCount+" totalNoOfRows: "+totalNoOfRows);
		}
		if(totalNoOfRows==emptyColumnCount)
			emptyColumn = true;
		//System.out.println("========= ["+columnIndex+"]"+ " emptyColumn: "+emptyColumn+" minimumColspan: "+minimumColspan);
		return emptyColumn;
	}
	
	public void processRemoveEmptyRowColumn()
	{
		int totalNoOfColumn	= this.getTotalNumberOfColumnsInTable();
		boolean isEmptyColumn= false;
		String strEmptyColumnIndexes = "";
		int minimumColspan = 1;
		for(int columnIndex=0; columnIndex < totalNoOfColumn; columnIndex++)
		{
			minimumColspan	= minimumColspanForColumn[columnIndex];
			isEmptyColumn	= isEmptyColumn(columnIndex, minimumColspan);
			if(isEmptyColumn)
				strEmptyColumnIndexes += columnIndex + "#";
			int tmp = columnIndex;
			columnIndex = columnIndex + (minimumColspan-1);
			//System.out.println("columnIndex: "+columnIndex+" minimumColspan: "+minimumColspan+" prevColumn: "+tmp);
		}
		if (strEmptyColumnIndexes.length() > 0)
		{	
			String[] strEmptyColumnIndexArr = strEmptyColumnIndexes.split("#");
			//System.out.println(" === Empty Column "+strEmptyColumnIndexes);
			int totalEmptyColumnLen = strEmptyColumnIndexArr.length;
			int tempEmptyColumnIndex = 0;
			for (int emptyColIndx = 0; emptyColIndx < totalEmptyColumnLen; emptyColIndx++)
			{
				tempEmptyColumnIndex	= Integer.parseInt(strEmptyColumnIndexArr[emptyColIndx]);
				int minimumColspans		= minimumColspanForColumn[tempEmptyColumnIndex];
				int tempPrevValue		= tempEmptyColumnIndex;
				tempEmptyColumnIndex	= tempEmptyColumnIndex - emptyColIndx + (minimumColspans-1);
				//System.out.println("##############============= Empty Column @ "+tempEmptyColumnIndex+" ["+tempPrevValue+"] ================================ minimumColspans: "+minimumColspans+" emptyColIndx: "+emptyColIndx);
				this.removeColumn(tempEmptyColumnIndex, tempPrevValue);
			}	
		}
	}
	
	private void removeColumn(int columnIndexReference, int oldColumnIndexReference)
	{	
		NodeList tableRowList = new NodeList();
		NodeFilter tableRowFilter = new TagNameFilter("TR");
		tableTag.collectInto(tableRowList, tableRowFilter);
		Node[] tableRowNodeArray = tableRowList.toNodeArray();
		int rowIndex			= 0;
		int tableColumnIndex	= 0;
		int currentColspan		= 0;
		int currentRowspan		= 0;
		int rowspanRemaining	= 1;
		TableColumn tempCurColumn	= null;
		int minimumColspan	= minimumColspanForColumn[oldColumnIndexReference];
		
		for (Node tableRow : tableRowNodeArray)
		{
			NodeList tableCellList = new NodeList();
			NodeFilter tableCellFilter = new TagNameFilter("TD");
			tableRow.collectInto(tableCellList, tableCellFilter);
			String currentText	= tableRows.get(rowIndex).get(columnIndexReference).getContent();
			//System.out.println("   #  ==== PROCESSING ROW: "+rowIndex+", Column: "+columnIndexReference+" ====### currentText: "+currentText);
			try
			{
				//totalColspanARow	= getTotalNumberOfColspan(tableCellList, columnIndexReference);
				//tempColumnIndex		= columnIndexReference - totalColspanARow;
				tableColumnIndex	= getTableColumnIndex(tableCellList, columnIndexReference);
				
				//System.out.println("\t    INDEXES ["+rowIndex+"]["+columnIndexReference+"] tableColumnIndex: "+tableColumnIndex+" tableCellIndex: "+columnIndexReference);
				tempCurColumn		= (TableColumn) tableCellList.elementAt(tableColumnIndex);
				currentColspan 		= this.getColspanValue(tempCurColumn);
				currentRowspan		= this.getRowspanValue(tempCurColumn);
				
				tableRows.get(rowIndex).remove(columnIndexReference);
				if(rowspanRemaining < 2)
				{
						//System.out.println("            ["+rowIndex+"]["+columnIndexReference+"] - REMOVE tempColumnIndex: "+tempColumnIndex + " totalColspanARow: "+totalColspanARow+" tempColumnIndex: "+tempColumnIndex+" currentColspan: "+currentColspan+" currentRowspan: "+currentRowspan+" rowspanRemaining: "+rowspanRemaining);
					if(currentColspan > minimumColspan)
					{
						tempCurColumn.setAttribute("colspan",Integer.toString(currentColspan - minimumColspan));
						//System.out.println("            SHRINK colspan : ["+rowIndex+"]["+columnIndexReference+"] - tableColumnIndex: "+tableColumnIndex + " newColspan: "+(currentColspan-minimumColspan));
					}
					else
					{	
						tableCellList.remove(tableColumnIndex);
						//System.out.println("            REMOVE column  tableColumnIndex: "+tableColumnIndex + " currentColspan: "+currentColspan);
					}
					
					if(currentRowspan > 1)	
						rowspanRemaining = currentRowspan;
					else 
						rowspanRemaining = 1;
					tableRow.getChildren().removeAll();
					tableRow.setChildren(tableCellList);
				}
				else
				{
					//System.out.println("             SKIPPING column  rowspanRemaining: "+rowspanRemaining);
					rowspanRemaining--;
				}
			}
			catch(Exception ex)
			{
				//System.out.println("             EXCEPTION =====  ["+rowIndex+"]["+columnIndexReference+"] ERROR: " + ex.getLocalizedMessage());
			}
			//System.out.println();
			rowIndex ++;
		}
	}
	
	public int getTableColumnIndex(NodeList tableColumnList, int columnIndexCheckLimit)
	{
		int tableColumnIndex = 0;
		//int tempLimit = columnIndexCheckLimit;
		int totalColumnCount = 0;
		int columnCountCheckLimit	= columnIndexCheckLimit + 1;
		int columnIndex = 0;
		for (columnIndex = 0; columnIndex < columnIndexCheckLimit; columnIndex++)
		{
			TableColumn tempCurColumn	= (TableColumn) tableColumnList.elementAt(columnIndex);
			int colspan = this.getColspanValue(tempCurColumn);
			//int tempColspan = colspan;
			if(colspan > 1)
			{
				if( ( totalColumnCount + colspan) >  columnCountCheckLimit)
				{
					int lessColspan = (totalColumnCount + colspan) -  columnCountCheckLimit;
					colspan			= colspan - lessColspan;
				}
				columnIndexCheckLimit	= columnIndexCheckLimit - (colspan - 1);
			}
			totalColumnCount	= totalColumnCount	+ (colspan - 1) + 1;
			//System.out.println("        getTableColumnIndex columnIndex: "+columnIndex+" prevColspan: "+tempColspan+ " newColspan: "+colspan+" totalColumnCount: "+totalColumnCount+" columnIndexCheckLimit: "+columnIndexCheckLimit +" prevLimit: "+tempLimit);
		}
		tableColumnIndex	= columnIndexCheckLimit;
		//System.out.println("    ==== getTableColumnIndex tableCellIndex: "+tempLimit+" tableColumnIndex: "+tableColumnIndex);
		return tableColumnIndex;
	}
	
	public void addColumn(int columnIndexReference, boolean isAddToRight)
	{
		NodeList tableRowList = new NodeList();
		NodeFilter tableRowFilter = new TagNameFilter("TR");
		tableTag.collectInto(tableRowList, tableRowFilter);
		Node[] tableRowNodeArray = tableRowList.toNodeArray();
		for (Node tableRow : tableRowNodeArray)
		{
			NodeList tableCellList = new NodeList();
			NodeFilter tableCellFilter = new TagNameFilter("TD");
			tableRow.collectInto(tableCellList, tableCellFilter);

			NodeList newTableCellList = new NodeList();
			boolean columnAdded = false;
			for (int index = 0; index < tableCellList.size() + 1; index++)
			{
				if (index == columnIndexReference)
				{
					TextNode currentTextNode = new TextNode("Empty cell with no style");
					currentTextNode.setText("<td>" + currentTextNode.getText() + "</td>");

					newTableCellList.add(currentTextNode);
					columnAdded = true;
				}
				else
				{
					if (columnAdded == false)
						newTableCellList.add(tableCellList.elementAt(index));
					else
						newTableCellList.add(tableCellList.elementAt(index - 1));
				}
			}

			tableRow.setChildren(newTableCellList);
		}
	}
	

}
