package htmltemplating.table;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.table.TableCell.TableCellType;

public class TableAnalyzer
{

	private TableStructure tableStructure;
	private int[] columnPoints;
	private int[] gutterColumnPoints;
	private int[] rowPoints;
	private int[] numberColumn;
	private int totalRows;
	private int totalColumns;
	private int textPoints = 1;
	private int textDensityPoints = 1;
	private int pointForBoldData = 1;
	private int pointForCellAlignment = 1;
	private int pointForBelowValueCell = 1;
	private int pointForAllColumnText = 1;
	private int pointForLeftCellTextAndRightCellText = 1;
	private int stubIdentifyThreshold = 1;
	private int currencyIdentifyThreshold = 1;
	private int gutterIdentifyThreshold = 1;
	private int pointForCurrency = 1;
	private int pointsForRightSideNumber = 1;
	private int pointsForEndingBracket = 1;
	private int pointsForNumberStartWithOpenBracket = 1;
	private int headerIdentifyThreshold = 1;
	private String[] currencySymbols = { "$", "�", "RM" };
	private int selectedCurrency = 0;
	private boolean financialTable = false;

	private int firstNumberCellRowIndx = -1;
	private int firstNumberCellColumnIndx = -1;
	
	private int firstPercentCellRowIndx = -1;
	private int firstParcentCellColumnIndx = -1;
	
	public TableAnalyzer(TableStructure tableStructure)
	{
		this.tableStructure = tableStructure;
		totalRows = tableStructure.getTotalNumberOfRowsInTable();
		totalColumns = tableStructure.getTotalNumberOfColumnsInTable();
		columnPoints = new int[totalColumns];
		rowPoints = new int[totalRows];
		gutterColumnPoints = new int[totalColumns];

		stubIdentifyThreshold = 2 * totalRows / 5;
		currencyIdentifyThreshold = 1 * totalRows / 3;
		gutterIdentifyThreshold = 2;// 1*totalRows/4;
		headerIdentifyThreshold = 2 * totalColumns / 3;
		identifyText();
		calculatePointsForText();
		calculatePointsForTextDensity();
		calculateColumnPointsStub();
		setStubColumns();
		initializeColumnVal();
		identifyNumber();
		identifyPercent();
		calculatePointsForNumberWithStartAndEndingBracket();
		calculatePointForCurrency();
		calculatePointsForRightSideNumber();
		calculatePointForBoldData();
		calculatePointForCellAlignment();
		calculatePointForBelowValueCell();

		calculatePointForAllColumnText();
		calculatePointForLeftCellTextAndRightCellText();
		calculateColumnPointsCurrency();
		setCurrencyColumn();
		initializeColumnVal();
		calculateColumnPointsGutter();
		setGutterColumn();
		calculateRowPointsHeader();
		setHeaderRow();
		setEvenRowAndOddRow();
		setEvenColumnAndOddColumn();
		checkFinancialTable();
		setStubColumnsAgain();
	}

	public void reInitialize()
	{
		totalColumns = tableStructure.getTotalNumberOfColumnsInTable();
		//setEvenColumnAndOddColumnAgain();
	}
	
	private void checkFinancialTable()
	{
		int totalNumberRows = 0;
		int totalValidRows = 1;
		numberColumn = new int[totalColumns];
		
		for(int columnIndex=0; columnIndex<totalColumns; columnIndex++){
			for(int rowIndex=0; rowIndex<totalRows; rowIndex++){
				TableCell tempCell = tableStructure.getCellByIndex(rowIndex, columnIndex);
				if(!tempCell.isCellType(TableCell.TableCellType.HeaderCell)){
					if(tempCell.isCellType(TableCell.TableCellType.NumberCell) || 
							tempCell.isCellType(TableCell.TableCellType.CurrencyCell) ||
							tempCell.isCellType(TableCell.TableCellType.PercentCell) )
					{
						totalNumberRows++;
					}
					totalValidRows++;
				}
			}
			if((100*totalNumberRows)/totalValidRows >= 50){
				numberColumn[columnIndex] = 1;
			}
			totalNumberRows = 0;
			totalValidRows = 1;
		}
		
		for(int i=0; i<totalColumns; i++){
			if(numberColumn[i] == 1 && i != 0){
				financialTable = true;
				break;
			}
		}
	}

	public boolean isFinancialTable()
	{
		return financialTable;
	}

	private void initializeColumnVal()
	{
		for (int i = 0; i < totalColumns; i++)
		{
			columnPoints[i] = 0;
		}
	}

	private void setEvenColumnAndOddColumn()
	{
		int columnNumberIdentifier = 1;
		for (int i = 0; i < totalColumns; i++)
		{
			String columnData = "";
			for (int j = 0; j < totalRows; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnData = columnData + tempCell.getContent();
			}
			if (columnData.length() == 0)
			{
				// continue; //Need to improve if the column content is empty
			}
			columnData = filterString(columnData);
			if (columnData.length() == 0)
			{
				// continue; //Need to improve if the column content is empty
			}
			if (columnNumberIdentifier % 2 == 0)
			{
				for (int j = 0; j < totalRows; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(j, i);
					if (!tempCell.isCellType(TableCellType.StubCell))
						tempCell.setCellType(TableCell.TableCellType.EvenColumnCell);
				}
			}
			else
			{
				for (int j = 0; j < totalRows; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(j, i);
					if (!tempCell.isCellType(TableCellType.StubCell))
						tempCell.setCellType(TableCell.TableCellType.OddColumnCell);
				}
			}
			columnNumberIdentifier++;
		}
	}

	private String filterString(String data)
	{
		String tempString = "";
		for (int i = 0; i < data.length(); i++)
		{
			char ch = data.charAt(i);
			if ((int) ch < 126 && (int) ch > 32)
			{
				tempString = tempString + ch;
			}
		}
		return tempString;
	}

	private void setEvenRowAndOddRow()
	{
		int rowNumberIdentifier = 1;
		for (int i = 0; i < totalRows; i++)
		{
			String rowData = "";
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				rowData = rowData + tempCell.getContent();
			}
			rowData = filterString(rowData);
			if (rowNumberIdentifier % 2 == 0)
			{
				for (int j = 0; j < totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					tempCell.setCellType(TableCell.TableCellType.EvenRowCell);
				}
			}
			else
			{
				for (int j = 0; j < totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					tempCell.setCellType(TableCell.TableCellType.OddRowCell);
				}
			}
			rowNumberIdentifier++;
		}
	}

	private void addCurrencySymbol(String symbol)
	{
		// TODO need to change variable to arraylist
	}

	private int setSelectedCurrency(int currencyPosition)
	{
		if (currencyPosition > -1 && currencySymbols.length < currencyPosition)
		{
			selectedCurrency = currencyPosition;
			return 9;
		}
		return -1;
	}

	private void identifyText()
	{
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);

				String tempContent = tempCell.getContent();
				tempContent = tempContent.trim();
				tempContent = tempContent.replaceAll("\\s+", " ");
				tempCell.setContent(tempContent);

				if (tempCell.getContent().matches(".*[a-zA-Z]+.*"))
				{
					tempCell.setCellType(TableCell.TableCellType.TextCell);
				}
			}
		}
	}

	private void setHeaderRow()
	{
		/*for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 0; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				if (rowPoints[rowIndx] > headerIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.HeaderCell);
				}
			}
		}*/
		for(int colIndx = 0; colIndx<totalColumns; colIndx++){
			TableCell tempCell = tableStructure.getCellByIndex(0, colIndx);
			tempCell.setCellType(TableCell.TableCellType.HeaderCell);
		}
	}

	private void setCurrencyColumn()
	{
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 0; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);

				if (columnPoints[colIndx] > currencyIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.CurrencyCell);
				}
			}
		}
	}

	private void setGutterColumn()
	{
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 0; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				if (gutterColumnPoints[colIndx] == gutterIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.GutterCell);
				}
			}
		}
	}

	private void identifyNumber()
	{
		boolean firstNumberCellFound = false;
		//String regexSymbols = "[$]?[(]?[0-9]+([,][0-9][0-9][0-9])*([.][0-9][0-9])?[)]?";
		String regexSymbols = "[$]?[(]?[0-9]+([,][0-9][0-9][0-9])*([.][0-9]*)?[)]?";
		int count = 0;
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				String content = tempCell.getContent();
				
				content = content.replace("\t", "");
				content = content.replace(" ", "");
				if (content.matches(regexSymbols))
				{
					if (!firstNumberCellFound)
					{
						firstNumberCellRowIndx = i;
						firstNumberCellColumnIndx = j;
						firstNumberCellFound = true;
					}
					tempCell.setCellType(TableCell.TableCellType.NumberCell);
					count++;
				}
			}
		}
	}
	
	private void identifyPercent()
	{
		boolean firstPercentCellFound = false;
		String regexSymbols = "[0-9]+([,][0-9][0-9][0-9])*([.][0-9][0-9])?[%]";
		int count = 0;
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				String content = tempCell.getContent();
				content = content.replace("\t", "");
				content = content.replace(" ", "");
				if (content.matches(regexSymbols))
				{
					if (!firstPercentCellFound)
					{
						firstPercentCellRowIndx = i;
						firstPercentCellRowIndx = j;
						firstPercentCellFound = true;
					}
					tempCell.setCellType(TableCell.TableCellType.PercentCell);
					count++;
				}
			}
		}
	}

	private void setStubColumns()
	{
		/*for (int i=0; i<totalRows; i++){
			TableCell tempCell = tableStructure.getCellByIndex(i, 0);
			tempCell.setCellType(TableCell.TableCellType.StubCell);
		}*/
		String logStr = "";
		for (int rowIndex = 0; rowIndex < totalRows; rowIndex++)
		{
			for (int colIndex = 0; colIndex < totalColumns; colIndex++)
			{
				logStr = "["+rowIndex+"]["+colIndex+"] = ";
				TableCell tempCell = tableStructure.getCellByIndex(rowIndex, colIndex);
				logStr += " columnPoints: "+columnPoints[colIndex]+" thresold: "+stubIdentifyThreshold;
				//System.out.println(logStr);
				if (columnPoints[colIndex] > stubIdentifyThreshold || colIndex == 0)
				{
					tempCell.setCellType(TableCell.TableCellType.StubCell);
				}
			}
		}
	}
	
	private void setStubColumnsAgain()
	{	
		int firstNumberCellIndex = 0;
		int numberColSize = numberColumn.length;
		for(int colIndex=0; colIndex<numberColSize; colIndex++)
		{
			if(numberColumn[colIndex] == 1)
			{
				firstNumberCellIndex = colIndex;
				break;
			}
		}
		//System.out.println("firstNumberCellIndex: "+firstNumberCellIndex+" numberColSize: "+numberColSize);
		for (int rowIndex = 0; rowIndex < totalRows; rowIndex++)
		{
			for (int colIndex = 0; colIndex < totalColumns; colIndex++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndex, colIndex);
				boolean isStubCellAlready = tempCell.isCellType(TableCell.TableCellType.StubCell);
				boolean isGutterCell	= tempCell.isCellType(TableCell.TableCellType.GutterCell);
				boolean isCurrencyCell	= tempCell.isCellType(TableCell.TableCellType.CurrencyCell);
				String logStr = "";
				if(isGutterCell == true || isCurrencyCell == true)
				{	
					for(int colIndex2 = colIndex; colIndex2 < totalColumns; colIndex2++)
					{
						TableCell tempCell2 = tableStructure.getCellByIndex(rowIndex, colIndex2);
						logStr = "["+rowIndex+"]["+colIndex2+"] = ";
						/*if(rowIndex== 4)
						{
							System.out.println(logStr+" 1 content: "+tempCell2.getContent()+" StubCell: "+tempCell2.isCellType(TableCell.TableCellType.StubCell)
									+" EvenColumnCell: "+tempCell2.isCellType(TableCell.TableCellType.EvenColumnCell)
									+" OddColumnCell: "+tempCell2.isCellType(TableCell.TableCellType.OddColumnCell)
									+" NumberCell: "+tempCell2.isCellType(TableCell.TableCellType.NumberCell)
									+" PercentCell: "+tempCell2.isCellType(TableCell.TableCellType.PercentCell));
						}*/
						tempCell2.removeCellType(TableCell.TableCellType.StubCell);
						if(colIndex2 % 2 == 1)
							tempCell2.setCellType(TableCell.TableCellType.EvenColumnCell);
						else
							tempCell2.setCellType(TableCell.TableCellType.OddColumnCell);
					}
					break;
				}
				else{
					if (colIndex < firstNumberCellIndex && isStubCellAlready == false )
					{
						tempCell.setCellType(TableCell.TableCellType.StubCell);
						tempCell.removeCellType(TableCell.TableCellType.EvenColumnCell);
						tempCell.removeCellType(TableCell.TableCellType.OddColumnCell);
					}
					if (colIndex >= firstNumberCellIndex && isStubCellAlready == true )
					{
						tempCell.removeCellType(TableCell.TableCellType.StubCell);
						if(colIndex % 2 == 1)
							tempCell.setCellType(TableCell.TableCellType.EvenColumnCell);
						else
							tempCell.setCellType(TableCell.TableCellType.OddColumnCell);
					}	
				}
			}
		}
	}
	
	public void setPointsForEndingBracket(int pointsForEndingBracket)
	{
		this.pointsForEndingBracket = pointsForEndingBracket;
	}
	
	public void setPointsForNumberStartWithOpenBracket(int pointsForNumberStartWithOpenBracket)
	{
		this.pointsForNumberStartWithOpenBracket = pointsForNumberStartWithOpenBracket;
	}

	private void calculatePointsForNumberWithStartAndEndingBracket()
	{
		String regExBothBracket = "";
		String regExStartBracket = "";
		String regExPercent = "";
		
		String regExSymbols = "^[(]+[" + currencySymbols[selectedCurrency]
				+ "]?[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)$|^[(]+[" + currencySymbols[selectedCurrency]
				+ "]?[-]?([0-9]{1,14})?([.][0-9]{1,4})$|^[(]+[" + currencySymbols[selectedCurrency]
				+ "]?[-]?[0-9]{1,14}$";

		regExBothBracket = "(\\([0-9]+([,][0-9]{3}){0,4}([.][0-9]{0,4})?)\\)";
		regExStartBracket = "(\\([0-9]+([,][0-9]{3}){0,4}([.][0-9]{0,4})?)";
		regExPercent = "[0-9]+([,][0-9][0-9][0-9])*([.][0-9][0-9])?[%]";
		boolean gutterNotFound = false;
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 1; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				if (colIndx == 1 && tempCell.getColSpanIndex() > 0)
				{
					if (tempCell.getCellColSpan() - tempCell.getColSpanIndex() + colIndx >= totalColumns)
					{
						for (int cellSpanIndx = 1; cellSpanIndx < totalColumns; cellSpanIndx++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(rowIndx, cellSpanIndx);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket, 0);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket,0);
						}
						break;
					}
					else
					{
						int tempCellSpanIndx = 0;
						for (tempCellSpanIndx = 1; tempCellSpanIndx < (tempCell.getCellColSpan() - tempCell.getColSpanIndex() + 1); tempCellSpanIndx++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(rowIndx, tempCellSpanIndx);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket, 0);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket,0);
						}
						colIndx = tempCellSpanIndx - 1;
						continue;
					}
				}

				String tempCellText = tempCell.getContent();
				Pattern pattern = Pattern.compile(regExPercent, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(tempCellText);
				if (matcher.find())
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket,
							pointsForNumberStartWithOpenBracket);
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket,
							pointsForEndingBracket);
					gutterNotFound = false;
				}				
				tempCellText = tempCell.getContent();
				pattern = Pattern.compile(regExBothBracket, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(tempCellText);
				if (matcher.find())
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket,
							pointsForNumberStartWithOpenBracket);
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket,
							pointsForEndingBracket);
					gutterNotFound = false;
				}
				else
				{
					pattern = Pattern.compile(regExStartBracket, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(tempCellText);
					if (matcher.find())
					{
						if (colIndx < totalColumns - 1)
						{
							TableCell tempCellRight = tableStructure.getCellByIndex(rowIndx, colIndx + 1);
							if (tempCellRight.getContent().equalsIgnoreCase(")"))
							{
								tempCellRight.setPointParameter(
										TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket,
										pointsForNumberStartWithOpenBracket);
								tempCellRight.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket,
										pointsForEndingBracket);

								gutterNotFound = true;
							}
							else
							{
								gutterNotFound = true;
							}
						}
						else
						{
							gutterNotFound = true;
						}

					}
					else
					{
						gutterNotFound = true;
					}
				}
				if (gutterNotFound)
				{
					int startBracketPoint = 0;
					int endBracketPoint = 0;
					try
					{
						startBracketPoint = (Integer) tempCell
								.getPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket);
					}
					catch (Exception ex)
					{
						startBracketPoint = 0;
					}

					try
					{
						endBracketPoint = (Integer) tempCell
								.getPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket);
					}
					catch (Exception ex)
					{
						endBracketPoint = 0;
					}

					if (startBracketPoint == 0)
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket, 0);
					}
					if (endBracketPoint == 0)
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket, 0);
					}
				}
			}
			try
			{
				
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, 0);
				tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket, 0);
				tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket, 0);
			}
			catch(Exception ex)
			{
				//if(tableStructure.getTableRows().get(rowIndx).get(0)==null)
			//	System.out.println("null");
				System.out.println("calculatePointsForNumberWithStartAndEndingBracket rowIndx: "+rowIndx);
				//ex.printStackTrace();
			}
		}
	}

	public void setPointForCurrency(int pointForCurrency)
	{
		this.pointForCurrency = pointForCurrency;
	}

	private void calculatePointForCurrency()
	{
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 0; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				String tempContent = tempCell.getContent();

				if (tempContent.equals(currencySymbols[selectedCurrency])
						|| tempContent.startsWith(currencySymbols[selectedCurrency]))
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCurrency,
							currencyIdentifyThreshold + 1);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCurrency, 0);
				}
			}
		}
	}

	public void setPointsForRightSideNumber(int pointsForRightSideNumber)
	{
		this.pointsForRightSideNumber = pointsForRightSideNumber;
	}

	private void calculatePointsForRightSideNumber()
	{
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int topColIndx = 0; topColIndx < totalColumns - 1; topColIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, topColIndx);
				if (topColIndx + tempCell.getCellColSpan() - tempCell.getColSpanIndex() >= totalColumns)
				{
					for (int innerColIndx = topColIndx; innerColIndx < totalColumns - 1; innerColIndx++)
					{
						TableCell tempCellNew = tableStructure.getCellByIndex(rowIndx, innerColIndx);
						tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
					}
					break;
				}
				TableCell tempCellRight = tableStructure.getCellByIndex(rowIndx,
						topColIndx + tempCell.getCellColSpan() - tempCell.getColSpanIndex());
				if (tempCellRight.isCellType(TableCellType.NumberCell))
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, pointsForRightSideNumber);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
				}
			}
			
			if(totalColumns > 0)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, totalColumns - 1);
				tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
			}
		}
	}

	public void setPointForBoldData(int pointForBoldData)
	{
		this.pointForBoldData = pointForBoldData;
	}

	private void calculatePointForBoldData()
	{
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			for (int colIndx = 0; colIndx < totalColumns; colIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				if (tempCell.isCellBold())
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBoldData, pointForBoldData);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBoldData, 0);
				}
			}
		}
	}

	public void setPointForCellAlignment(int pointForCellAlignment)
	{
		this.pointForCellAlignment = pointForCellAlignment;
	}

	private void calculatePointForCellAlignment()
	{
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				boolean isCenterAligned;
				try
				{
					isCenterAligned = "center".equals(tempCell.getCellStyle(CSSStyle.ALIGN).toLowerCase());
					if (isCenterAligned)
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment,
								pointForCellAlignment);
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment, 0);
					}
				}
				catch (UnparsableStyleException e)
				{
					e.printStackTrace();
				}
				catch (CellStyleNotFoundException e)
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment, 0);
				}
			}
		}
	}

	public void setPointForBelowValueCell(int pointForBelowValueCell)
	{
		this.pointForBelowValueCell = pointForBelowValueCell;
	}

	private void calculatePointForBelowValueCell()
	{
		// int trackSpan[]= new Array();

		int[] trackRowSpan = new int[totalColumns];
		int[] trackColSpan = new int[totalRows];

		for (int i = 0; i < totalRows; i++)
		{
			if (i < totalRows - 1)
			{
				for (int j = 0; j < totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);

					if (tempCell == null)
						continue;
					if (tempCell.getRowSpan() > 1)
					{
						trackRowSpan[j] += tempCell.getRowSpan();
					}

					if ((i - trackRowSpan[j]) <= totalColumns)
					{

						if (j >= j - trackRowSpan[j]) continue;
						TableCell tempCellBelow = tableStructure.getCellByIndex(i - trackRowSpan[j], j);
						if (tempCellBelow == null)
							continue;
						if (tempCellBelow.isCellType(TableCell.TableCellType.NumberCell))
						{
							tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell,
									pointForBelowValueCell);
						}
						else
						{
							tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell, 0);
						}
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell, 0);
					}
				}
			}
			else
			{
				for (int j = 0; j < totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell, 0);
				}
			}
		}
	}

	public void setPointForAllColumnText(int pointForAllColumnText)
	{
		this.pointForAllColumnText = pointForAllColumnText;
	}

	private void calculatePointForAllColumnText()
	{
		for (int i = 0; i < totalRows; i++)
		{
			int numberOfColumnsWithText = 0;
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					numberOfColumnsWithText++;
				}
			}
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (numberOfColumnsWithText == totalColumns)
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForAllColumnText,
							pointForAllColumnText);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForAllColumnText, 0);
				}
			}

		}
	}

	public void setPointForLeftCellAndRightCell(int pointForLeftCellTextAndRightCellText)
	{
		this.pointForLeftCellTextAndRightCellText = pointForLeftCellTextAndRightCellText;
	}

	private void calculatePointForLeftCellTextAndRightCellText()
	{
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCellTop = tableStructure.getCellByIndex(i, j);
				if (j > 0 && j < totalColumns - 1)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					if (j + tempCell.getCellColSpan() - tempCell.getColSpanIndex() >= totalColumns)
					{
						for (int k = j; k < totalColumns; k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(
									TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText, 0);
						}
						break;
					}
					if ((j - tempCell.getColSpanIndex() - 1) < 0)
					{
						int k = 0;
						for (k = j; k < (tempCell.getCellColSpan() - tempCell.getColSpanIndex() + 1); k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(
									TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText, 0);
						}
						j = k - 1;
						continue;
					}
					TableCell tempCellLeft = tableStructure.getCellByIndex(i, j - tempCell.getColSpanIndex() - 1);
					TableCell tempCellRight = tableStructure.getCellByIndex(i,
							j + tempCell.getCellColSpan() - tempCell.getColSpanIndex());
					if (tempCellLeft.isCellType(TableCell.TableCellType.TextCell)
							&& tempCellRight.isCellType(TableCell.TableCellType.TextCell))
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText,
								pointForLeftCellTextAndRightCellText);
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText,
								0);
					}
				}
				else
				{
					tempCellTop.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText,
							0);
				}
			}
		}
	}

	public void setTextPoints(int textPoints)
	{
		this.textPoints = textPoints;
	}

	public void setHeaderIdentifyThreshold(int headerIdentifyThreshold)
	{
		headerIdentifyThreshold = headerIdentifyThreshold;
	}

	public void setGutterIdentifyThreshold(int gutterIdentifyThreshold)
	{
		gutterIdentifyThreshold = gutterIdentifyThreshold;
	}

	public void setStubIdentifyThreshold(int stubIdentifyThreshold)
	{
		stubIdentifyThreshold = stubIdentifyThreshold;
	}

	public void setCurrencyIdentifyThreshold(int currencyIdentifyThreshold)
	{
		currencyIdentifyThreshold = currencyIdentifyThreshold;
	}

	public void setTextDensityPoints(int textDensityPoints)
	{
		this.textDensityPoints = textDensityPoints;
	}

	private void calculateColumnPointsGutter()
	{
		int tempPointsForNumberStartWithOpenBracket = 0;
		int tempPointsForEndingBracket = 0;
		for (int colIndx = 0; colIndx < totalColumns; colIndx++)
		{
			for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				tempPointsForNumberStartWithOpenBracket = (Integer) tempCell
						.getPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket);
				
				tempPointsForEndingBracket = (Integer) tempCell
							.getPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket);
				if ((tempPointsForNumberStartWithOpenBracket + tempPointsForEndingBracket) == gutterIdentifyThreshold)
				{
					gutterColumnPoints[colIndx] = tempPointsForNumberStartWithOpenBracket + tempPointsForEndingBracket;
				}
			}
		}
	}

	private void calculateColumnPointsCurrency()
	{
		int minimumColspan = 1;
		tableStructure.minimumColspanForColumn	= new int[totalColumns];
		for (int colIndx = 0; colIndx < totalColumns; colIndx++)
		{
			minimumColspan	= 99;
			for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, colIndx);
				// columnPoints[i] = columnPoints[i] +
				// (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber)
				// +
				// (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForCurrency);
				columnPoints[colIndx] = columnPoints[colIndx]
						+ (Integer) tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForCurrency);
				int tempColspan = tempCell.getCellColSpan();
				if(minimumColspan > tempColspan) minimumColspan = tempColspan;
			}
			if (minimumColspan < 1) minimumColspan = 1;
			tableStructure.minimumColspanForColumn[colIndx]	= minimumColspan;
		}
	}

	private void calculateRowPointsHeader()
	{
		boolean isFoundNonEmptyCell = false;
		boolean headerDetectionThreshold = false;
		boolean isFirstCellEmpty = false;

		boolean headerRowDetectionComplete = false;
		int emptyCellHeaderRowIndx = -1;
		int headerRowPoint = headerIdentifyThreshold + 1;
		int initialEmptyCell = 0;
		int headerRowCount = 0;
		int noOfNonEmptyCell = -1;
		int noOfNonEmptyCellAll = -1;
		int lastHeaderRowIndx = -1;
		String tempCellText = "";
		int cellColspan = 0;
		for (int rowIndx = 0; rowIndx < totalRows; rowIndx++)
		{
			initialEmptyCell = 0;
			isFoundNonEmptyCell = false;
			isFirstCellEmpty = false;
			emptyCellHeaderRowIndx = -1;
			noOfNonEmptyCell = 0;
			noOfNonEmptyCellAll = 0;
			cellColspan = 0;
			for (int cellIndx = 0; cellIndx < totalColumns; cellIndx++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(rowIndx, cellIndx);

				if (tempCell instanceof TableCellSpan)
				{
					cellColspan++;
				}
				String tempContent = tempCell.getContent();

				if (tempContent.length() < 1 && !isFoundNonEmptyCell && cellIndx < firstNumberCellColumnIndx
						&& !headerRowDetectionComplete && !isFirstCellEmpty)
				{
					initialEmptyCell++;
					emptyCellHeaderRowIndx = rowIndx;
				}

				if (tempContent.length() > 1)
				{
					if (!tempContent.equals(tempCellText))
					{
						noOfNonEmptyCell++;
						tempCellText = tempContent;
					}
					noOfNonEmptyCellAll++;

					isFoundNonEmptyCell = true;
					if (cellIndx == 0)
					{
						isFirstCellEmpty = true;
						headerDetectionThreshold = true;
					}
				}

			}
			if (rowIndx < firstNumberCellRowIndx && (noOfNonEmptyCell < 2 && cellColspan == totalColumns - 1))
			{
				rowPoints[rowIndx] = headerRowPoint;
				lastHeaderRowIndx = rowIndx;
				headerRowCount++;
				headerDetectionThreshold = false;
			}
			else if (emptyCellHeaderRowIndx != -1 && initialEmptyCell == firstNumberCellColumnIndx
					&& !headerRowDetectionComplete)
			{
				rowPoints[rowIndx] = headerRowPoint;
				lastHeaderRowIndx = rowIndx;
				headerRowCount++;
			}
			if (headerDetectionThreshold)
			{
				headerRowDetectionComplete = true;
			}
			if (headerRowDetectionComplete)
				break;
		}

		if (headerRowCount < 1)
		{
			for (int rowIndx = 0; rowIndx < firstNumberCellRowIndx; rowIndx++)
			{
				rowPoints[rowIndx] = headerRowPoint;
			}
		}
	}

	private void calculateColumnPointsStub()
	{
		for (int i = 0; i < totalColumns; i++)
		{
			for (int j = 0; j < totalRows; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnPoints[i] = columnPoints[i]
						+ (Integer) tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForTextDensity)
						+ (Integer) tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForText);
			}
		}
	}

	private void calculatePointsForText()
	{
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForText, textPoints);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForText, 0);
				}
			}
		}
	}

	// TODO Noticed bugs, cleanup and refactor
	private void calculatePointsForTextDensity()
	{
		for (int i = 0; i < totalRows; i++)
		{
			for (int j = 0; j < totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					if (i > 0 && i < totalRows - 1 && i - 1 - tempCell.getRowSpanIndex() >= 0)
					{
						TableCell tempCellTop = tableStructure.getCellByIndex(i - 1 - tempCell.getRowSpanIndex(), j);
						if (tempCellTop.isCellType(TableCell.TableCellType.TextCell)
								&& i + tempCell.getRowSpan() - tempCell.getRowSpanIndex() < totalRows)
						{
							TableCell tempCellBottom = tableStructure.getCellByIndex(i + tempCell.getRowSpan()
									- tempCell.getRowSpanIndex(), j);
							if (tempCellBottom.isCellType(TableCell.TableCellType.TextCell))
							{
								tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity,
										textDensityPoints);
							}
							else
							{
								tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity, 0);
							}
						}
						else
						{
							tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity, 0);
						}
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity, 0);
					}
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity, 0);
				}
			}
		}
	}

}
