package htmltemplating.list;

import htmltemplating.list.ListCell;
import htmltemplating.list.List;

public class ListAnalyzer
{
	private List list;
	private int totalCellInSingleRow;
	private boolean isList = false;

	public ListAnalyzer(List list)
	{
		this.list = list;
		totalCellInSingleRow = list.getNumberOfColumnInRow(0);
		identifyText();
		checkList();
	}

	private void checkList()
	{
		if (totalCellInSingleRow == 3)
		{
			isList = true;
		}
	}

	public boolean isList()
	{
		return isList;
	}

	private void identifyText()
	{
		for (int j = 0; j < totalCellInSingleRow; j++)
		{
			ListCell tempCell = list.getCellByIndex(0, j);
			if (tempCell.getContent().matches(".*[a-zA-Z]+.*"))
			{
				tempCell.setCellType(ListCell.ListCellType.TextCell);
			}
		}
	}
}
