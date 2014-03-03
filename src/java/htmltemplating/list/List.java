package htmltemplating.list;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.cssstyle.CSSStyle;
import htmltemplating.cssstyle.CellStyleNotFoundException;
import htmltemplating.cssstyle.StyleStringProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.list.ListCell;
import htmltemplating.list.ListCell.ListCellType;

import java.util.ArrayList;

import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.Node;

public class List
{
	private ArrayList<ArrayList<ListCell>> listRow = new ArrayList<ArrayList<ListCell>>();
	private final TableTag listTag;
	private ListTemplate listTemplate;
	private int tagNo = 0;

	public List(TableTag listTag)
	{
		this.listTag = listTag;

		int rowIndex = 0;
		ArrayList<ListCell> rowCells = new ArrayList<ListCell>();
		TableRow currentRow = listTag.getRow(rowIndex);
		TableColumn[] listColumns = currentRow.getColumns();
		for (TableColumn listColumn : listColumns)
		{
			listColumn = setListTypeSymbol(listColumn);

			ListCell currentCell = new ListCell(listColumn);

			rowCells.add(currentCell);
		}

		listRow.add(rowCells);
	}

	public List(TableTag listTag, ListTemplate listTemplate, int tagNo)
	{
		this.listTag = listTag;
		this.listTemplate = listTemplate;
		this.tagNo = tagNo;
		int rowIndex = 0;
		ArrayList<ListCell> rowCells = new ArrayList<ListCell>();
		TableRow currentRow = listTag.getRow(rowIndex);
		TableColumn[] listColumns = currentRow.getColumns();
		for (TableColumn listColumn : listColumns)
		{
			listColumn = setListTypeSymbol(listColumn);

			ListCell currentCell = new ListCell(listColumn);

			rowCells.add(currentCell);
		}

		listRow.add(rowCells);
	}

	public String Int2stringRoman(int decimalValue, boolean isLower)
	{
		String roman = "";
		int values[] = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
		String lowerRoman[] = { "m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i" };
		String upperRoman[] = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

		for (int index = 0; index < values.length; index++)
		{
			while (decimalValue >= values[index])
			{
				if (isLower == false)
					roman += upperRoman[index];
				else
					roman += lowerRoman[index];
				decimalValue -= values[index];
			}
		}

		return roman;

	}

	public TableColumn setListTypeSymbol(TableColumn listColumn)
	{

		try
		{
			if (listColumn.getChildCount() != 3)
				return listColumn;
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		Node firstChild = listColumn.childAt(1);

		tagNo++;

		String listStyleStatus = "";
		try
		{
			listStyleStatus = listTemplate.getStyleValue(ListStyle.LISTSTYLETYPE);
		}
		catch (StyleNotDefinedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (listStyleStatus.equalsIgnoreCase("none"))
		{

			firstChild.setText("");
		}
		else if (listStyleStatus.equalsIgnoreCase("square"))
		{

			firstChild.setText("&#9744;");
		}
		else if (listStyleStatus.equalsIgnoreCase("disc"))
		{

			firstChild.setText("&#8226;");
		}
		else if (listStyleStatus.equalsIgnoreCase("circle"))
		{

			firstChild.setText("&#959;");
		}
		else if (listStyleStatus.equalsIgnoreCase("decimal"))
		{

			firstChild.setText(tagNo + ")");
		}
		else if (listStyleStatus.equalsIgnoreCase("lower-alpha"))
		{
			char lowerChar = (char) ('a' + tagNo - 1);
			firstChild.setText(lowerChar + ")");
		}
		else if (listStyleStatus.equalsIgnoreCase("upper-alpha"))
		{
			char upperChar = (char) ('A' + tagNo - 1);
			firstChild.setText(upperChar + ")");
		}
		else if (listStyleStatus.equalsIgnoreCase("lower-roman"))
		{

			firstChild.setText(Int2stringRoman(tagNo, true) + ".");
		}
		else if (listStyleStatus.equalsIgnoreCase("upper-roman"))
		{

			firstChild.setText(Int2stringRoman(tagNo, false) + ".");
		}

		return listColumn;

	}

	private String getStyleString()
	{
		return listTag.getAttribute("style");
	}

	public String toHTML()
	{
		return null;
	}

	public ArrayList<ArrayList<ListCell>> getListCells()
	{
		return listRow;
	}

	public ArrayList<ListCell> getRowByIndex(int row)
	{
		ArrayList<ListCell> rowArrayList = new ArrayList<ListCell>();
		rowArrayList = listRow.get(row);

		return rowArrayList;
	}

	public ArrayList<ListCell> getCellsByType(ListCellType ListCellType)
	{
		ArrayList<ListCell> cellsList = new ArrayList<ListCell>();
		for (ArrayList<ListCell> rowCells : listRow)
		{
			for (ListCell cell : rowCells)
			{
				if (cell.isCellType(ListCellType))
					cellsList.add(cell);
			}
		}
		return cellsList;
	}

	public int getNumberOfColumnInRow(int row)
	{
		return listRow.get(row).size();
	}

	public void setCellByIndex(int row, int column, ListCell value)
	{
		listRow.get(row).set(column, value);

		// listTag.removeChild(row);

	}

	public ListCell getCellByIndex(int row, int column)
	{
		ListCell cell = listRow.get(row).get(column);

		return cell;
	}

	public int getTotalNumberOfRowsInList()
	{
		return listRow.size();
	}

	public int getTotalNumberOfColumnsInList()
	{
		return listRow.get(0).size();
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
		this.listTag.setAttribute("style", styleString);
	}
}
