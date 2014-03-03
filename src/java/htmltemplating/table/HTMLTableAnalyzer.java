package htmltemplating.table;

import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.table.TableCell.TableCellType;

		
public class HTMLTableAnalyzer {

	private TableStructure tableStructure;
	private int[] columnPoints;
	private int[] rowPoints;
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
	private String[] currencySymbols = {"$","€","RM"};
	private int selectedCurrency = 0;
	private boolean financialTable = false;
		
	public HTMLTableAnalyzer(TableStructure tableStructure)
	{
		this.tableStructure = tableStructure;
		totalRows = tableStructure.getTotalNumberOfRowsInTable();
		totalColumns = tableStructure.getTotalNumberOfColumnsInTable();
		columnPoints = new int[totalColumns];
		rowPoints = new int[totalRows];
		stubIdentifyThreshold 		= 2*totalRows/3;
		currencyIdentifyThreshold	= 1*totalRows/3;
		gutterIdentifyThreshold		= 1*totalRows/4;
		headerIdentifyThreshold		= 2*totalColumns/3;
		identifyText();
		calculatePointsForText();
		calculatePointsForTextDensity();
		calculateColumnPointsStub();
		setStubColumns();
		initializeColumnVal();
		identifyNumber();
		calculatePointsForEndingBracket();
		calculatePointsForNumberStartWithOpenBracket();
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
	}
	
	private void checkFinancialTable()
	{
		int totalValidColumns = 0;
		int totalNumberColumns = 0;
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(tempCell.isCellType(TableCell.TableCellType.NumberCell)||tempCell.isCellType(TableCell.TableCellType.CurrencyCell))
				{
					totalNumberColumns = totalNumberColumns + 1;
				}
				if(tempCell.isCellType(TableCell.TableCellType.NumberCell) ||
						tempCell.isCellType(TableCell.TableCellType.CurrencyCell) ||
						tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					totalValidColumns++;
				}
				
			}
		}
		if(totalValidColumns < totalNumberColumns*3)
		{
			financialTable = true;
		}
	}
	
	public boolean IsFinancialTable()
	{
		return financialTable;
	}
	
	private void initializeColumnVal()
	{
		for(int i = 0;i<totalColumns;i++)
		{
			columnPoints[i] = 0;
		}
	}
	
	private void setEvenColumnAndOddColumn()
	{
		int columnNumberIdentifier = 1;
		for(int i=0;i<totalColumns;i++)
		{
			String columnData = "";
			for(int j = 0;j<totalRows; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnData = columnData + tempCell.getContent(); 
			}
			if(columnData.length() == 0)
			{
				continue;
			}
			columnData = filterString(columnData);
			if(columnData.length() == 0)
			{
				continue;
			}
			if(columnNumberIdentifier%2 == 0)
			{
				for(int j = 0;j<totalRows; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(j, i);
					if(!tempCell.isCellType(TableCellType.StubCell))
						tempCell.setCellType(TableCell.TableCellType.EvenColumnCell); 
				}
			}
			else
			{
				for(int j = 0;j<totalRows; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(j, i);
					if(!tempCell.isCellType(TableCellType.StubCell))
						tempCell.setCellType(TableCell.TableCellType.OddColumnCell); 
				}
			}
			columnNumberIdentifier++;
		}		
	}
	
	private String filterString(String data)
	{
		String tempString = "";
		for(int i=0;i<data.length();i++)
		{
			char ch = data.charAt(i);
			if((int)ch < 126 && (int)ch > 32)
			{
				tempString = tempString + ch;
			}
		}
		return tempString;
	}
	
	private void setEvenRowAndOddRow()
	{
		int rowNumberIdentifier = 1;
		for(int i=0;i<totalRows;i++)
		{
			String rowData = "";
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				rowData = rowData + tempCell.getContent(); 
			}
			if(rowData.length() == 0)
			{
				continue;
			}
			rowData = filterString(rowData);
			if(rowData.length() == 0)
			{
				continue;
			}
			if(rowNumberIdentifier%2 == 0)
			{
				for(int j = 0;j<totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					tempCell.setCellType(TableCell.TableCellType.EvenRowCell); 
				}
			}
			else
			{
				for(int j = 0;j<totalColumns; j++)
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
		//need to change variable to arraylist
	}
	
	private int setSelectedCurrency(int currencyPosition)
	{
		if(currencyPosition>-1 && currencySymbols.length < currencyPosition)
		{
			selectedCurrency = currencyPosition;
			return 9;
		}
		return -1;
	}
	
	private void identifyText()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.getContent().matches(".*[a-zA-Z]+.*")) 
				{
					tempCell.setCellType(TableCell.TableCellType.TextCell);
				}
			}
		}
	}
	
	
	private void setHeaderRow()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(rowPoints[i] > headerIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.HeaderCell);
				}
			}
		}
	}
	
	
	private void setCurrencyColumn()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(columnPoints[j] > currencyIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.CurrencyCell);
				}
			}
		}
	}
	
	private void setGutterColumn()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(columnPoints[j] > gutterIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.GutterCell);
				}
			}
		}
	}
	
	private void identifyNumber()
	{
		String regexSymbols = "^[(]?["+currencySymbols[selectedCurrency]+"][ ]*?[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)[)]?$|^[(]?["+
								currencySymbols[selectedCurrency]+"]?[ ]*[-]?([0-9]{1,14})?([.][0-9]{1,4})[)]?$|^[(]?["+
								currencySymbols[selectedCurrency]+"]?[ ]*[-]?[0-9]{1,14}[)]?$" +
								"^["+currencySymbols[selectedCurrency]+"]?[ ]*[(]?[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)[)]?$|^["+
								currencySymbols[selectedCurrency]+"]?[ ]*[(]?[-]?([0-9]{1,14})?([.][0-9]{1,4})[)]?$|^["+
								currencySymbols[selectedCurrency]+"]?[ ]*[(]?[-]?[0-9]{1,14}[)]?$"+
								"^["+currencySymbols[selectedCurrency]+"]?[ ]*[-]?[(]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)[)]?$|^["+
								currencySymbols[selectedCurrency]+"]?[ ]*[-]?[(]?([0-9]{1,14})?([.][0-9]{1,4})[)]?$|^["+
								currencySymbols[selectedCurrency]+"]?[ ]*[-]?[(]?[0-9]{1,14}[)]?$";
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.getContent().matches(regexSymbols)) 
				{
					tempCell.setCellType(TableCell.TableCellType.NumberCell);
				}
			}
		}
	}
	
	private void setStubColumns()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(columnPoints[j] > stubIdentifyThreshold)
				{
					tempCell.setCellType(TableCell.TableCellType.StubCell);
				}
			}
		}
	}

	
	private void calculatePointsForEndingBracket()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.getContent() == ")") 
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket, pointsForEndingBracket);
				}
				else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket, 0);
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
	
	private void calculatePointsForNumberStartWithOpenBracket()
	{
		String regexSymbols = "^[(]+["+currencySymbols[selectedCurrency]+"]?[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)$|^[(]+["+
		currencySymbols[selectedCurrency]+"]?[-]?([0-9]{1,14})?([.][0-9]{1,4})$|^[(]+["+
		currencySymbols[selectedCurrency]+"]?[-]?[0-9]{1,14}$";
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 1;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(j == 1 && tempCell.getColSpanIndex()>0)
				{
					if(tempCell.getCellColSpan()-tempCell.getColSpanIndex()+j >= totalColumns)
					{
						for(int k = 1;k<totalColumns;k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket , 0);
						}
						break;
					}
					else
					{
						int k = 0;
						for(k = 1;k<(tempCell.getCellColSpan()-tempCell.getColSpanIndex()+1);k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket , 0);
						}
						j = k-1;
						continue;
					}
				}
				TableCell tempCellLeft = tableStructure.getCellByIndex(i, j-tempCell.getColSpanIndex()-1);
				if (tempCellLeft.getContent().matches(regexSymbols)) 
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket , pointsForNumberStartWithOpenBracket);
				}else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket , 0);
				}
			}
			TableCell tempCell = tableStructure.getCellByIndex(i, 0);
			tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket , 0);
		}
	}
	
	public void setPointForCurrency(int pointForCurrency)
	{
		this.pointForCurrency = pointForCurrency;
	}
	
	private void calculatePointForCurrency()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if (tempCell.getContent() == currencySymbols[selectedCurrency]) 
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCurrency, pointForCurrency);
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
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns-1; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(j+tempCell.getCellColSpan()-tempCell.getColSpanIndex() >= totalColumns)
				{
					for(int k = j;k<totalColumns-1;k++)
					{
						TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
						tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
					}
					break;
				}
				TableCell tempCellRight = tableStructure.getCellByIndex(i, j+tempCell.getCellColSpan()-tempCell.getColSpanIndex());
				if(tempCellRight.isCellType(TableCellType.NumberCell))
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, pointsForRightSideNumber);
				}else
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
				}
			}
			TableCell tempCell = tableStructure.getCellByIndex(i,totalColumns-1 );
			tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber, 0);
		}
	}
	
	public void setPointForBoldData(int pointForBoldData)
	{
		this.pointForBoldData = pointForBoldData;
	}
	
	private void calculatePointForBoldData()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(tempCell.isCellBold())
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
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				boolean isCenterAligned;
				try
				{
					isCenterAligned = "center".equals(tempCell.getCellStyle(CSSStyle.ALIGN).toLowerCase());
					if(isCenterAligned)
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment, pointForCellAlignment);
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment, 0);
					}
				}
				catch (UnparsableStyleException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (CellStyleNotFoundException e)
				{
					// TODO Auto-generated catch block
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
		for(int i = 0;i<totalRows; i++)
		{
			if(i < totalRows-1)
			{
				for(int j = 0;j<totalColumns; j++)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					if((i+tempCell.getRowSpan()-tempCell.getRowSpanIndex())<=totalColumns)
					{
						TableCell tempCellBelow = tableStructure.getCellByIndex(i+tempCell.getRowSpan()-tempCell.getRowSpanIndex(), j);
						if(tempCellBelow.isCellType(TableCell.TableCellType.NumberCell))
						{
							tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell, pointForBelowValueCell);
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
			}else
			{
				for(int j = 0;j<totalColumns; j++)
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
		for(int i = 0;i<totalRows; i++)
		{
			int numberOfColumnsWithText = 0;
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					numberOfColumnsWithText++;
				}
			}
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(numberOfColumnsWithText == totalColumns)
				{
					tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForAllColumnText, pointForAllColumnText);
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
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCellTop = tableStructure.getCellByIndex(i, j);
				if(j>0 && j<totalColumns-1)
				{
					TableCell tempCell = tableStructure.getCellByIndex(i, j);
					if(j+tempCell.getCellColSpan()-tempCell.getColSpanIndex() >= totalColumns)
					{
						for(int k = j;k<totalColumns;k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText, 0);
						}
						break;
					}
					if((j-tempCell.getColSpanIndex()-1) < 0)
					{
						int k = 0;
						for(k = j;k<(tempCell.getCellColSpan()-tempCell.getColSpanIndex()+1);k++)
						{
							TableCell tempCellNew = tableStructure.getCellByIndex(i, k);
							tempCellNew.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText , 0);
						}
						j = k - 1;
						continue;
					}
					TableCell tempCellLeft = tableStructure.getCellByIndex(i, j-tempCell.getColSpanIndex()-1);
					TableCell tempCellRight = tableStructure.getCellByIndex(i, j+tempCell.getCellColSpan()-tempCell.getColSpanIndex());
					if(tempCellLeft.isCellType(TableCell.TableCellType.TextCell) && tempCellRight.isCellType(TableCell.TableCellType.TextCell))
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText , pointForLeftCellTextAndRightCellText);
					}
					else
					{
						tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText , 0);
					}
				}
				else
				{
					tempCellTop.setPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText , 0);
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
		for(int i = 0;i<totalColumns;i++)
		{
			for(int j = 0; j< totalRows;j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnPoints[i] = columnPoints[i] + (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForNumberStartWithOpenBracket); 
				columnPoints[i] = columnPoints[i] + (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForEndingBracket);
			}
		}
	}
	
	
	private void calculateColumnPointsCurrency()
	{
		for(int i = 0;i<totalColumns;i++)
		{
			for(int j = 0; j< totalRows;j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnPoints[i] = columnPoints[i] + (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForRightSideNumber) 
				+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForCurrency);
			}
		}
	}
	
	private void calculateRowPointsHeader()
	{
		for(int i = 0;i<totalRows;i++)
		{
			for(int j = 0; j<totalColumns ;j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				
				rowPoints[i] = rowPoints[i] 
				                         + (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForLeftCellTextAndRightCellText) 
													+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForBoldData)
													+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForCellAlignment)
													+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForBelowValueCell)
													+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForAllColumnText)
													;
			}
		}
	}
	
	private void calculateColumnPointsStub()
	{
		for(int i = 0;i<totalColumns;i++)
		{
			for(int j = 0; j< totalRows;j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(j, i);
				columnPoints[i] = columnPoints[i] + (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForTextDensity) 
													+ (Integer)tempCell.getPointParameter(TableCell.CellCustomParameter.pointsForText);
			}
		}
	}
	
	private void calculatePointsForText()
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(tempCell.isCellType(TableCell.TableCellType.TextCell))
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
	//buggy
	private void calculatePointsForTextDensity() 
	{
		for(int i = 0;i<totalRows; i++)
		{
			for(int j = 0;j<totalColumns; j++)
			{
				TableCell tempCell = tableStructure.getCellByIndex(i, j);
				if(tempCell.isCellType(TableCell.TableCellType.TextCell))
				{
					if(i>0 && i<totalRows-1 && i-1-tempCell.getRowSpanIndex() >= 0)
					{	
						TableCell tempCellTop = tableStructure.getCellByIndex(i-1-tempCell.getRowSpanIndex(), j);
						if(tempCellTop.isCellType(TableCell.TableCellType.TextCell) && i+tempCell.getRowSpan()-tempCell.getRowSpanIndex() < totalRows)
						{
							TableCell tempCellBottom = tableStructure.getCellByIndex(i+tempCell.getRowSpan()-tempCell.getRowSpanIndex(), j);
							if(tempCellBottom.isCellType(TableCell.TableCellType.TextCell))
							{
								tempCell.setPointParameter(TableCell.CellCustomParameter.pointsForTextDensity, textDensityPoints);
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
