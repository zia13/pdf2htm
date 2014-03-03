package htmltemplating.image;

import htmltemplating.StyleNotDefinedException;
import htmltemplating.StyleTemplateProcessor;
import htmltemplating.cssstyle.UnparsableStyleException;

import org.htmlparser.Node;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.ImageTag;

public class ImageTemplateProcessor extends StyleTemplateProcessor
{
	private ImageTemplate imageTemplate;

	public ImageTemplateProcessor(ImageTemplate imageTemplate)
	{
		super();
		this.imageTemplate = imageTemplate;
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
		if (tag instanceof ImageTag)
		{
			Image image = new Image((ImageTag) tag);

			if (true)
			{
				try
				{
					if (image.getStyleTemplateId() == imageTemplate.getStyleTemplateId()
							|| image.getStyleTemplateId() == -1)
					{
						if (image.getStyleTemplateId() == -1)
						{
							image.setStyleTemplateId(imageTemplate.getStyleTemplateId());
						}
					}
					processImageStyle(image);
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

	public String getModifiedHTML()
	{
		return modifiedHTML.toString();
	}

	private void processImageStyle(Image image)
	{
		try
		{
			String margin = imageTemplate.getStyleValue(ImageStyle.MARGIN);
			image.setMargin(margin);
			String align = imageTemplate.getStyleValue(ImageStyle.ALIGN);
			image.setImageAlign(align);
			String border = imageTemplate.getStyleValue(ImageStyle.BORDER);
			image.setBorder(border);
		}
		catch (StyleNotDefinedException e)
		{
			e.printStackTrace();
		}
	}
}
