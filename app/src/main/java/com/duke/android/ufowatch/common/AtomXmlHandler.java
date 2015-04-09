package com.duke.android.ufowatch.common;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.duke.android.ufowatch.domain.FeedStructure;

public class AtomXmlHandler extends DefaultHandler {

	public static final String TAG = "AtomXmlHandler";

	private FeedStructure feedStr = new FeedStructure();
	private List<FeedStructure> rssList = new ArrayList<FeedStructure>();

	private int articlesAdded = 0;

	StringBuffer chars = new StringBuffer();

	public void startElement(String uri, String localName, String qName, Attributes atts) {
		chars = new StringBuffer();

		if (qName.equalsIgnoreCase("link")) {
			String rel = atts.getValue("rel").toString();
			if (!rel.equalsIgnoreCase("null")) {
				String link = atts.getValue("href").toString();
				// Log.d(TAG, "Link to BLOG ARTICLE: " + link);
				feedStr.setLink(link);
			}
		}

	}
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equalsIgnoreCase("title")) {
			feedStr.setTitle(chars.toString());
		} else if (localName.equalsIgnoreCase("published")) {
			feedStr.setPubDate(chars.toString());
		}
		// else if (localName.equalsIgnoreCase("encoded")) {
		//
		// feedStr.setEncodedContent(chars.toString());
		// } else if (qName.equalsIgnoreCase("media:content")) {
		//
		// } else if (localName.equalsIgnoreCase("link")) {
		// String link = chars.toString();
		// if (link.toLowerCase().startsWith("http")) {
		// feedStr.setLink(link);
		// } else {
		// Log.i(TAG, "INVALID LINK: " + link);
		// Log.i(TAG, "FOR TITLE: " + feedStr.getTitle());
		// }
		// }
		if (localName.equalsIgnoreCase("entry")) {

			rssList.add(feedStr);

			feedStr = new FeedStructure();
			articlesAdded++;
			if (articlesAdded >= SharedConstants.FEED_ITEM_LIMIT) {
				throw new SAXException();
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}

	public List<FeedStructure> getLatestArticles(String feedUrl) {
		URL url = null;
		try {

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			url = new URL(feedUrl);
			xr.setContentHandler(this);
			xr.parse(new InputSource(url.openStream()));
		} catch (IOException e) {
		} catch (SAXException e) {

		} catch (ParserConfigurationException e) {

		}

		return rssList;
	}

}