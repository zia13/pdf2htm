package htmltemplating.cssstyle;

public class StyleStringProcessor
{

	public static String getCSSStyle(String styleString, CSSStyle cssStyle) throws UnparsableStyleException,
			CellStyleNotFoundException
	{
		int[] styleValueIndexes = getCSSStyleValueIndexes(styleString, cssStyle);
		return styleString.substring(styleValueIndexes[0], styleValueIndexes[1]);
	}

	public static String setCSSStyle(String styleString, CSSStyle cssStyle, String styleValue)
			throws UnparsableStyleException
	{
		int[] styleValueIndexes;
		String returnString;
		try
		{
			styleValueIndexes = getCSSStyleValueIndexes(styleString, cssStyle);
			String preString = styleString.substring(0, styleValueIndexes[0]);
			String postString = styleString.substring(styleValueIndexes[1]);
			returnString = preString + styleValue.trim() + postString;
		}
		catch (CellStyleNotFoundException e)
		{
			if (null == styleString)
			{
				returnString = cssStyle.styleName() + ":" + styleValue + ";";
			}
			else
			{
				if (!styleString.endsWith(";"))
					styleString = styleString + ";";
				returnString = styleString + cssStyle.styleName() + ":" + styleValue + ";";
			}
		}
		return returnString;
	}

	private static int[] getCSSStyleValueIndexes(String styleString, CSSStyle cssStyle)
			throws UnparsableStyleException, CellStyleNotFoundException
	{
		String styleName = cssStyle.styleName();
		if (null == styleString || styleString.isEmpty())
			throw new CellStyleNotFoundException("CSS style [" + cssStyle.styleName() + "] is not found in ["
					+ styleString + "]");
		int styleStart = styleString.indexOf(styleName);
		if (styleStart != -1)
		{
			int styleEnd = styleString.indexOf(";", styleStart);
			if (styleEnd == -1)
				styleEnd = styleString.length();
			int valueStart = styleString.indexOf(":", styleStart);
			if (valueStart != -1)
			{
				int[] indexes = { valueStart + 1, styleEnd };
				return indexes;
			}
			else
			{
				throw new UnparsableStyleException("Unable to parse style (" + styleString + ")");
			}
		}
		else
		{
			throw new CellStyleNotFoundException("CSS style [" + cssStyle.styleName() + "] is not found in ["
					+ styleString + "]");
		}
	}

}
