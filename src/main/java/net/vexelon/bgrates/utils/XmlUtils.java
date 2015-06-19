package net.vexelon.bgrates.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlUtils {

	/**
	 * Serialize an XML element recursively
	 * 
	 * @param node
	 * @param serializer
	 * @throws IOException
	 */
	private static void serializeXmlElement(Node node, XmlSerializer serializer) throws IOException {

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node current = children.item(i);

			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) current;
				serializer.startTag("", child.getNodeName());
				serializeXmlElement(child, serializer);
				serializer.endTag("", child.getNodeName());
			} else if (current.getNodeType() == Node.TEXT_NODE) {
				Text child = (Text) current;
				serializer.text(child.getData());
			} else if (current.getNodeType() == Node.CDATA_SECTION_NODE) {
				CDATASection child = (CDATASection) current;
				serializer.cdsect(child.getData());
			} else if (current.getNodeType() == Node.COMMENT_NODE) {
				Comment child = (Comment) current;
				serializer.comment(child.getData());
			}
		}
	}

	/**
	 * Serialize a Root element and all it's descendants
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @param serializer
	 * @throws Exception
	 */
	private static void serializeXml(Document document, XmlSerializer serializer) throws Exception {
		serializer.startDocument("UTF-8", true);
		document.getDocumentElement().normalize();
		serializeXmlElement(document, serializer);
		serializer.endDocument();
	}

	/**
	 * Parse org.w3c.dom Document and serialized to a String using Android
	 * Util.xml
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @return
	 * @throws RuntimeException
	 */
	public static String getXmlDoc(Document document) throws RuntimeException {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter(1024);

		try {
			serializer.setOutput(writer);
			serializeXml(document, serializer);
		} catch (Exception e) {
			throw new RuntimeException("Failed converting Xml to String!", e);
		}

		return writer.toString();
	}

	/**
	 * Parse org.w3c.dom Document and serialized to a file using Android
	 * Util.xml
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @param file
	 * @throws RuntimeException
	 */
	public static void saveXmlDoc(Document document, File file) throws RuntimeException {
		XmlSerializer serializer = Xml.newSerializer();

		try {
			FileWriter writer = new FileWriter(file);
			serializer.setOutput(writer);
			serializeXml(document, serializer);
		} catch (Exception e) {
			throw new RuntimeException("Failed save Xml to " + file.getName(), e);
		}
	}

}
