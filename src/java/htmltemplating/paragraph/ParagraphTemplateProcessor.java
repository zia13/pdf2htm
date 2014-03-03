package htmltemplating.paragraph;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.StyleTemplateProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import org.htmlparser.Node;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.Div;

public class ParagraphTemplateProcessor extends StyleTemplateProcessor
{
	private ParagraphTemplate paragraphTemplate;
	private boolean styleTemplateMatch = false;

	public ParagraphTemplateProcessor(ParagraphTemplate paragraphTemplate)
	{
		super();
		this.paragraphTemplate = paragraphTemplate;
	}

	public void visitRemarkNode(Remark remarkNode)
	{
		modifiedHTML.append(remarkNode.toHtml());
	}

	public void visitStringNode(Text stringNode)
	{
		;
	}

	public void visitTag(Tag tag)
	{

		if (tag instanceof Div)
		{
			Paragraph paragraph = new Paragraph((Div) tag);
			ParagraphAnalyzer.analyze(paragraph);

			
			if (paragraph.isStylableParagraph())
			{
                                String html = "";
                                if(tag.getParent() != null && tag.getParent().getParent() != null){
                                    html = tag.getParent().getParent().toHtml();
                                }
                                else{
                                    html = tag.toHtml();
                                }
				int position = html.indexOf(tag.toHtml());
				html = html.substring(position);
				if(html.contains("style-template-id:"+paragraphTemplate.getStyleTemplateId())){
					styleTemplateMatch = true;
				}
				try
				{
					if (paragraph.getStyleTemplateId() == paragraphTemplate.getStyleTemplateId()
							|| paragraph.getStyleTemplateId() == 0
							|| paragraph.getStyleTemplateId() == -1)
					{
						if(!styleTemplateMatch || paragraph.getStyleTemplateId() == paragraphTemplate.getStyleTemplateId()){
							paragraph.setStyleTemplateId(paragraphTemplate.getStyleTemplateId());
							processParagraphStyle(paragraph);							
						}
					}
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

	private void processParagraphStyle(Paragraph paragraph)
	{
		try
		{
			String textAlign = paragraphTemplate.getStyleValue(ParagraphStyle.TEXTALIGN);
			if (!textAlign.isEmpty())
			{
				paragraph.setTextAlign(textAlign);
			}
			String margin = paragraphTemplate.getStyleValue(ParagraphStyle.MARGIN);
			if (!margin.isEmpty())
			{
				paragraph.setMargin(margin);
			}
			String fontColor = paragraphTemplate.getStyleValue(ParagraphStyle.FONTCOLOR);
			String fontSize = paragraphTemplate.getStyleValue(ParagraphStyle.FONTSIZE);
			String fontFamily = paragraphTemplate.getStyleValue(ParagraphStyle.FONTFAMILY);
			paragraph.setFontStyles(fontColor, fontSize, fontFamily);
			String boldStatus = paragraphTemplate.getStyleValue(ParagraphStyle.BOLD);

			String firstLineIndent = paragraphTemplate.getStyleValue(ParagraphStyle.FIRSTLINEINDENT);

			String hangingIndent = paragraphTemplate.getStyleValue(ParagraphStyle.HANGINGINDENT);

			if (!firstLineIndent.isEmpty() && !hangingIndent.isEmpty())
				paragraph.setTextIndent(firstLineIndent, hangingIndent);

			if (!boldStatus.isEmpty())
			{
				paragraph.setBoldStatus(Boolean.parseBoolean(boldStatus));
			}
			String italicStatus = paragraphTemplate.getStyleValue(ParagraphStyle.ITALIC);
			if (!italicStatus.isEmpty())
			{
				paragraph.setItalicStatus(Boolean.parseBoolean(italicStatus));
			}
			String underlineStatus = paragraphTemplate.getStyleValue(ParagraphStyle.UNDERLINE);
			if (!underlineStatus.isEmpty())
			{
				paragraph.setUnderlineStatus(Boolean.parseBoolean(underlineStatus));
			}
		}
		catch (StyleNotDefinedException e)
		{
			e.printStackTrace();
		}
	}


	public String getModifiedHTML()
	{
		return modifiedHTML.toString();
	}
}
