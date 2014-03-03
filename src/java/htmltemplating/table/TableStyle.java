package htmltemplating.table;

import htmltemplating.TemplateStyle;

public enum TableStyle implements TemplateStyle
{
	STUBCOLUMNMINWIDTH("minWidth", "stub_column"), GUTTER_COLUMN_WIDTH("width", "gutter_column"), GUTTER_COLUMN_ALIGNMENT(
			"align", "gutter_column"), GUTTER_COLUMN_IS_CREATE("gutter_column", "gutter_column"),

	CURRENCY_COLUMN_WIDTH("width", "currency_column"), CURRENCY_COLUMN_ALIGNMENT("align", "currency_column"), CURRENCY_COLUMN_IS_CREATE(
			"currency_column", "currency_column"), REMOVE_EMPTY_COLUMN("remove_empty_column","remove_empty_column"),
			REMOVE_EMPTY_ROW("remove_empty_row", "remove_empty_row");
	private TableStyle(String refXMLString, String refTagName)
	{
		this.refXMLString = refXMLString;
		this.refTagName = refTagName;
	}

	public String refXMLString()
	{
		return refXMLString;
	}

	public String refTagName()
	{
		return refTagName;
	}

	public boolean isAttribute()
	{
		return !refXMLString.equals(refTagName);
	}

	private final String refXMLString;
	private final String refTagName;
}
