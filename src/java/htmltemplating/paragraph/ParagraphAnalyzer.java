package htmltemplating.paragraph;


import org.htmlparser.Node;

import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableColumn;



public class ParagraphAnalyzer
{
	public static void analyze(Paragraph paragraph)
	{
		Div refDiv=  paragraph.getDivTag();
		
		Node parentNode;
		parentNode = refDiv.getParent();
		
		
		if(parentNode instanceof TableColumn)
		{
			paragraph.setStylableParagraph(false);
		}
		
	}
}
