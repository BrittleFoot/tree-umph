package com.github.brittlefoot.treeumph.script.bindings;

import com.github.brittlefoot.treeumph.script.bindings.errors.ServiceError;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;


public class XmlUtils {

    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static final Transformer serializationTransformer;

    static {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            serializationTransformer = transformerFactory.newTransformer();
            serializationTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            serializationTransformer.setOutputProperty(OutputKeys.INDENT, "no");
        } catch (TransformerConfigurationException e) {
            throw new ServiceError("Xml transformer initialization error", e);
        }
    }

    public static Document parse(byte[] bytes) {
        try {
            return documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new ServiceError("Error during document parsing", e);
        }
    }

    public static NodeList extractByXpath(Node node, String xpath) {
        try {
            return (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new ServiceError("xpath exception", e);
        }
    }

    public static Node findFirstByXpath(Node node, String xpath) {
        try {
            return ((NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, node, XPathConstants.NODESET))
                    .item(0);
        } catch (XPathExpressionException e) {
            throw new ServiceError("xpath exception", e);
        }
    }

    public static byte[] toBytes(Node node) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            serializationTransformer.transform(new DOMSource(node), new StreamResult(baos));
        } catch (TransformerException e) {
            throw new ServiceError("Transformation exception", e);
        }
        return baos.toByteArray();
    }

    public static void forEach(NodeList nodeList, Consumer<Node> consumer) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            consumer.accept(nodeList.item(i));
        }
    }

}
