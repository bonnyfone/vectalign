package com.bonnyfone.vectalign;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.NodeChangeEvent;

/**
 * Created by ziby on 26/08/15.
 */
public class SVGParser {

    /**
     * Check if file is an SVG
     * @param f
     * @return
     */
    public static boolean isSVGImage(File f){
        try {
            Document document = getXMLDocumentFromFile(f);
            return document.getDoctype().getName().toLowerCase().equals("svg");
        } catch (Exception e) {}

        return false;
    }

    /**
     * Parse path elements of an SVG file into a single big path sequence.
     * @param f
     * @return
     */
    public static String getPathDataFromSVGFile(File f){
        try {
            StringBuilder sb = new StringBuilder();
            Document document = getXMLDocumentFromFile(f);
            NodeList nList = document.getElementsByTagName("path");
            for(int i=0; i<nList.getLength(); i++){
                Element e = (Element) nList.item(i);
                sb.append(e.getAttribute("d"));
            }

            return sb.toString();
        } catch (Exception e) {}

        return null;

    }

    private static Document getXMLDocumentFromFile(File f) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(f);
        return document;
    }


}
