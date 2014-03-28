package htmltemplating.list;

import java.util.ArrayList;
import java.util.HashMap;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.StyleTemplateProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;
import htmltemplating.list.ListCell.ListCellType;
import org.htmlparser.Node;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.CompositeTag;

import org.htmlparser.tags.TableTag;

public class ListTemplateProcessor extends StyleTemplateProcessor
{
	private ListTemplate listTemplate;
	private int tagNo = 0;

	public ListTemplateProcessor(ListTemplate listTemplate)
	{
		super();
		this.listTemplate = listTemplate;
	}

	public void visitRemarkNode(Remark remarkNode)
	{
		modifiedHTML.append(remarkNode.toHtml());
	}

	public void visitStringNode(Text stringNode)
	{
		;
	}

        @Override
	public void visitTag(Tag tag)
	{
		Node parent = tag.getParent();
		if (parent != null && parent.toHtml().contains("td") == false)
		{
			tagNo = 0;
		}
		if (tag instanceof TableTag)
		{

			List list = new List((TableTag) tag, listTemplate, tagNo);
			TableTag listTag = (TableTag) tag;
			//listTag.setAttribute("width", "100%");
			tagNo++;
			ListAnalyzer listAnalyzer = new ListAnalyzer(list);

			if (listAnalyzer.isList())
			{

				try
				{
					if (list.getStyleTemplateId() == listTemplate.getStyleTemplateId()
							|| list.getStyleTemplateId() == -1)
					{
						if (list.getStyleTemplateId() == -1)
						{
							list.setStyleTemplateId(listTemplate.getStyleTemplateId());
						}
					}

					ArrayList<ListCell> listCells = list.getCellsByType(ListCellType.TextCell);
					processListStyles(listCells);

				}
				catch (UnparsableStyleException e)
				{
					e.printStackTrace();
				}
			}
		}
		if (null == tag.getParent() && (!(tag instanceof CompositeTag) || null == ((CompositeTag) tag).getEndTag()))
		{
			modifiedHTML.append(tag.toHtml());

		}
	}

	private void processListStyles(ArrayList<ListCell> listCells) throws UnparsableStyleException
	{
		HashMap<ListCell.ListCellType, String> listCellTypeMap = new HashMap<ListCell.ListCellType, String>();
		listCellTypeMap.put(ListCellType.TextCell, "text");

		for (ListCell listCell : listCells)
		{

			for (ListCellType listCellType : listCellTypeMap.keySet())
			{

				if (listCell.isCellType(listCellType))
				{
					try
					{
						String align = listTemplate.getStyleValue(ListStyle.TEXTALIGN);
						if (!align.isEmpty())
							listCell.setTextAlign(align);

						String fontColor = listTemplate.getStyleValue(ListStyle.FONTCOLOR);
						String fontSize = listTemplate.getStyleValue(ListStyle.FONTSIZE);
						String fontFamily = listTemplate.getStyleValue(ListStyle.FONTFAMILY);
						listCell.setFontStyles(fontColor, fontSize, fontFamily);

						String boldStatus = listTemplate.getStyleValue(ListStyle.BOLD);
						listCell.setBoldStatus(Boolean.parseBoolean(boldStatus));
						String italicStatus = listTemplate.getStyleValue(ListStyle.ITALIC);
						listCell.setItalicStatus(Boolean.parseBoolean(italicStatus));
						String underlineStatus = listTemplate.getStyleValue(ListStyle.UNDERLINE);
						listCell.setUnderlineStatus(Boolean.parseBoolean(underlineStatus));

						// listCell.setContent("decimal");
					}
					catch (StyleNotDefinedException e)
					{
						e.printStackTrace();
					}

				}
			}
		}
	}


	public String getModifiedHTML()
	{
		return modifiedHTML.toString();
	}
}
