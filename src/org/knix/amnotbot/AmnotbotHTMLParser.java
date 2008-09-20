
package org.knix.amnotbot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

public class AmnotbotHTMLParser
{

    private Parser parser;
    private String title = null;
    private String keywords = null;	
    
    private Logger logger;
	
    public AmnotbotHTMLParser(String url)
    {
        this.parser = new Parser();
        this.setUrl(url);
        this.logger = BotLogger.getDebugLogger();
    }
	
    private boolean isValidURL(String url)
    {
        URL u = null;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {		
            logger.debug(e.getMessage());	
			return false;
		}
		
		URLConnection uc = null;
		try {
			uc = u.openConnection();
		} catch (IOException e) {
			BotLogger.getDebugLogger().debug(e.getMessage());
		}
		
		String type = uc.getContentType();
        logger.debug("type:" + type);		
		if (type != null) {
			if (!type.startsWith ("text"))
				return false;
		}
		
		return true;
	}
	
	public void setUrl(String url)
	{
		if (this.isValidURL(url)) {
			
			try	{
				this.parser.setURL(url);		
			} catch (ParserException pe) {
				logger.debug(pe.getMessage());		
				return;
			}
			this.parse();
			
		}
	}
	
	private void setTitle(String title)
	{
		if (title != null) {
			if (title.trim().length() == 0)
				title = null;
		}
		this.title = title;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	private void setKeywords(String keywords)
	{
		this.keywords = keywords;		
	}
	
    public String getKeywords()
	{
		return this.keywords;
	}
	
	public String getUrl()
	{
		return this.parser.getURL();
	}
	
	private void parse()
	{
		this.parseHeaders();
		this.parseBody();
	}
	
	private void parseHeaders()
	{
		NodeList nl;
		
		try {
			nl = this.parser.parse(null);
							
			NodeList titles = nl.extractAllNodesThatMatch( new NodeClassFilter(TitleTag.class), true );
            logger.debug("Titles: " + titles.size());
			if (titles.size() > 0) {
				TitleTag titletag = (TitleTag)titles.elementAt(0);
				this.setTitle( Translate.decode( titletag.getTitle().trim() ) );			
			}
				
			NodeList meta = nl.extractAllNodesThatMatch( 
					new AndFilter(new NodeClassFilter(MetaTag.class), 
									new HasAttributeFilter ("name", "keywords")), true);
			if (meta.size() > 0) {	    	    	
				MetaTag metatag = (MetaTag)meta.elementAt(0);
				this.setKeywords( metatag.getMetaContent() );
			}		
        } catch (ParserException pe) {
            logger.debug(pe.getMessage());			
            return;
		}
	}
	
	private void parseBody()
	{
	}	
}
