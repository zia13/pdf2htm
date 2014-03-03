package htmltemplating.table;

import org.htmlparser.tags.TableColumn;

public class TableCellSpan extends TableCell
{
	public TableCellSpan(TableColumn tableColumn, int rowSpanIndex, int colSpanIndex)
	{
		super(tableColumn);
		this.rowSpanIndex = rowSpanIndex;
		this.colSpanIndex = colSpanIndex;
	}
}
