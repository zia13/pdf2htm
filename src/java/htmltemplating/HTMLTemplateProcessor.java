package htmltemplating;

import htmltemplating.table.TableTemplate;
import htmltemplating.table.TableTemplateProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;

public class HTMLTemplateProcessor
{
	StringBuffer outputHTML = new StringBuffer();
	StringBuffer inputHTML = new StringBuffer();

	private Parser parser;

	@Deprecated
	public HTMLTemplateProcessor(InputStream hTMLInputStream) throws IOException
	{
		BufferedReader hTMLBufferedReader = new BufferedReader(new InputStreamReader(hTMLInputStream));
		String line;
		while ((line = hTMLBufferedReader.readLine()) != null)
		{
			inputHTML.append(line);
		}
		parser = Parser.createParser(inputHTML.toString(), null);
	}

	@Deprecated
	public HTMLTemplateProcessor(InputStream hTMLInputStream, InputStream xMLInputStream) throws IOException
	{
		inputHTML = readInputStream(hTMLInputStream);
		StringBuffer templateXML = readInputStream(xMLInputStream);

		TableTemplate tableTemplate = new TableTemplate(templateXML.toString());
		TableTemplateProcessor tableTemplateProcessor = new TableTemplateProcessor(tableTemplate);
		parser = Parser.createParser(inputHTML.toString(), null);
		try
		{
			parser.visitAllNodesWith(tableTemplateProcessor);
			outputHTML.append(tableTemplateProcessor.getModifiedHTML());
		}
		catch (ParserException e)
		{
			outputHTML.append(e.getMessage());
			e.printStackTrace();
		}
	}

	public HTMLTemplateProcessor(InputStream hTMLInputStream, InputStream xMLInputStream, String styleTemplateID)
			throws IOException
	{
		inputHTML = readInputStream(hTMLInputStream);
		StringBuffer templateXML = readInputStream(xMLInputStream);
		ArrayList<StyleTemplateProcessor> styleTemplateProcessors = StyleTemplateProcessorFactory
				.getStyleTemplateProcessors(templateXML.toString(), Integer.parseInt(styleTemplateID));
		String currentHTML = inputHTML.toString();
		for (StyleTemplateProcessor styleTemplateProcessor : styleTemplateProcessors)
		{
			Parser currentParser = Parser.createParser(currentHTML, null);
			try
			{
				currentParser.visitAllNodesWith(styleTemplateProcessor);
				currentHTML = styleTemplateProcessor.getModifiedHTML();
			}
			catch (ParserException e)
			{
				e.printStackTrace();
			}
		}
		outputHTML.append(currentHTML);
	}

	public String processedHTML()
	{
		return outputHTML.toString();
	}

	private StringBuffer readInputStream(InputStream inputStream) throws IOException
	{
		StringBuffer stream = new StringBuffer();
		BufferedReader hTMLBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = hTMLBufferedReader.readLine()) != null)
		{
			stream.append(line);
		}
		return stream;
	}

}