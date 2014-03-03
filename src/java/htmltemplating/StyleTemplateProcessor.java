package htmltemplating;

import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class StyleTemplateProcessor extends NodeVisitor
{
	protected final StringBuffer modifiedHTML;

	public StyleTemplateProcessor()
	{
		super(true, true);
		modifiedHTML = new StringBuffer();
	}

	public String getModifiedHTML()
	{
		return null;
	}

	public final void visitEndTag(Tag tag)
	{
		Node parent;

		parent = tag.getParent();

		if (null == parent)
		{
			modifiedHTML.append(tag.toHtml());
		}
		else if (null == parent.getParent())
		{
			// This following if condition is implemented to avoid a specific
			// problem with
			// 'font', 'b', 'i', 'u' tags.
			ArrayList<String> invalidTagNames = new ArrayList<String>();
			invalidTagNames.add("FONT");
			invalidTagNames.add("B");
			invalidTagNames.add("I");
			invalidTagNames.add("U");

			if (!invalidTagNames.contains(tag.getTagName()))
				modifiedHTML.append(parent.toHtml());
		}
	}
}
