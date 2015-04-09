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

import android.annotation.SuppressLint;
import android.util.Log;

import com.duke.android.ufowatch.domain.FeedStructure;

public class RssFeedXmlHandler extends DefaultHandler {

	public static final String TAG = "RssFeedXmlHandler";

	private FeedStructure feedStr = new FeedStructure();
	private List<FeedStructure> rssList = new ArrayList<FeedStructure>();

	private int articlesAdded = 0;

	StringBuffer chars = new StringBuffer();

	public void startElement(String uri, String localName, String qName, Attributes atts) {
		chars = new StringBuffer();

		if (qName.equalsIgnoreCase("media:content")
				&& (feedStr.getImgLink() == null || feedStr.getImgLink().length() == 0)) {
			if (!atts.getValue("url").toString().equalsIgnoreCase("null")) {
				feedStr.setImgLink(atts.getValue("url").toString());
			} else {
				feedStr.setImgLink("");
			}
		}
		/*
		 * if (qName.equalsIgnoreCase("media:content") && (feedStr.getImgLink()
		 * == null || feedStr.getImgLink().length() == 0)) { if
		 * (!atts.getValue("url").toString().equalsIgnoreCase("null")) {
		 * feedStr.setImgLink(atts.getValue("url").toString()); } else {
		 * feedStr.setImgLink(""); } } if
		 * (qName.equalsIgnoreCase("content:encoded") && (feedStr.getImgLink()
		 * == null || feedStr.getImgLink().length() == 0)) { if
		 * (!atts.getValue("src").toString().equalsIgnoreCase("null")) {
		 * feedStr.setImgLink(atts.getValue("src").toString()); } else {
		 * feedStr.setImgLink(""); } }
		 */

	}

	@SuppressLint("DefaultLocale")
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equalsIgnoreCase("title")) {
			feedStr.setTitle(chars.toString());
		} else if (localName.equalsIgnoreCase("description")) {

			feedStr.setDescription(chars.toString());
		} else if (localName.equalsIgnoreCase("pubDate")) {

			feedStr.setPubDate(chars.toString());
		} else if (localName.equalsIgnoreCase("encoded")) {

			feedStr.setEncodedContent(chars.toString());
		} else if (qName.equalsIgnoreCase("media:content")) {

		} else if (localName.equalsIgnoreCase("link")) {
			String link = chars.toString();
			if (link.toLowerCase().startsWith("http")) {
				feedStr.setLink(link);
			} else {
				Log.i(TAG, "INVALID LINK: " + link);
				Log.i(TAG, "FOR TITLE: " + feedStr.getTitle());
			}
		}
		if (localName.equalsIgnoreCase("item")) {

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