package com.github.brittlefoot.treeumph.script.bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.UUID;
import java.util.function.Function;

import static org.w3c.dom.Node.DOCUMENT_NODE;


@Component
public class XmlUtilsBinding implements IServiceBinding {

    private static final Logger log = LoggerFactory.getLogger(XmlUtilsBinding.class);

    /**
     * {@link XmlUtilsBinding#removeSignature(Node)} but wrapped with {@code to-bytes} and {@code parse} functions.
     *
     * @param envelope bytes
     * @return new byte array
     */
    public byte[] removeSignature(byte[] envelope) {
        return XmlUtils.toBytes(removeSignature(XmlUtils.parse(envelope).getDocumentElement()));
    }

    /**
     * removes all Signature.* tags from document and replace it with Signature.Object.* content.
     *
     * @param parsedEnvelope envelope to mutate.
     * @return given node
     */
    public Node removeSignature(Node parsedEnvelope) {
        NodeList signatures = XmlUtils.extractByXpath(parsedEnvelope, "//*[local-name()='Signature']");
        if (signatures.getLength() < 1) {
            log.warn("Given xml does not have 'Signature' tag");
            return parsedEnvelope;
        }

        for (int i = 0; i < signatures.getLength(); i++) {
            Node signature = signatures.item(i);
            NodeList objects = XmlUtils.extractByXpath(signature, "*[local-name()='Object']");

            if (objects.getLength() < 1) {
                log.warn("Given xml does not have 'Object' tag");
                continue;
            }

            Node object = XmlUtils.extractByXpath(objects.item(0), "*").item(0);
            Node signatureParentNode = signature.getParentNode();

            if (DOCUMENT_NODE == signatureParentNode.getNodeType()) {
                parsedEnvelope = object;
                continue;
            }

            signatureParentNode.insertBefore(object, signature);
            signatureParentNode.removeChild(signature);
        }
        return parsedEnvelope;
    }

    /**
     * {@link XmlUtilsBinding#changeGUID(Node)} but wrapped with {@code to-bytes} and {@code parse} functions.
     *
     * @param envelope bytes
     * @return new byte array
     */
    public byte[] changeGUID(byte[] envelope) {
        return XmlUtils.toBytes(changeGUID(XmlUtils.parse(envelope).getDocumentElement()));
    }

    /**
     * Change all {@code DocumentID} and {@code EnvelopeID} tags content to a {@link UUID#randomUUID()}
     *
     * @param parsedEnvelope envelope to change.
     * @return given node.
     */
    public Node changeGUID(Node parsedEnvelope) {
        String xpath = "//*[local-name()='DocumentID'] | //*[local-name()='EnvelopeID']";
        NodeList extracted = XmlUtils.extractByXpath(parsedEnvelope, xpath);

        if (extracted.getLength() < 1) {
            log.warn("Given xml does not have 'DocumentId' tag");
            return parsedEnvelope;
        }

        for (int i = 0; i < extracted.getLength(); i++) {
            Node node = extracted.item(i);
            node.setTextContent(UUID.randomUUID().toString());
        }

        return parsedEnvelope;
    }

    /**
     * Parse bytes to a valid {@link Node}
     *
     * @param bytes valid xml bytes representation
     * @return parsed xml dom
     */
    public Node parse(byte[] bytes) {
        return XmlUtils.parse(bytes).getDocumentElement();
    }

    /**
     * {@link XmlUtilsBinding#changeText(Node, String, Function)} with function yields given {@code newText}. <br/>
     * Equivalent to {@code changeText(node, xpath, oldText -> newText)}
     */
    public Node changeText(Node node, String xpath, String newText) {
        return changeText(node, xpath, oldText -> newText);
    }

    /**
     * For all subtrees of node that matches xpath apply {@code newTextGenerator(oldText)} function on it.
     *
     * @param node             root node to execute search.
     * @param xpath            xpath to extract nodes.
     * @param newTextGenerator String -> String function to mutate nodes text.
     * @return given node
     */
    public Node changeText(Node node, String xpath, Function<String, String> newTextGenerator) {
        XmlUtils.forEach(XmlUtils.extractByXpath(node, xpath), found ->
                found.setTextContent(newTextGenerator.apply(found.getTextContent()))
        );
        return node;
    }

    /**
     * Search for all nodes that satisfy given xpath.
     *
     * @param node  root node to execute search
     * @param xpath xpath to search nodes
     * @return NodeList which contains all subtrees of given tree found by xpath.
     */
    public NodeList extract(Node node, String xpath) {
        return XmlUtils.extractByXpath(node, xpath);
    }

    /**
     * Extracting all subtrees by xpath and yields first found.
     *
     * @param node  root node to execute search
     * @param xpath xpath to search node
     * @return first subnode of given node which satisfy xpath.
     */
    public Node findFirst(Node node, String xpath) {
        return XmlUtils.findFirstByXpath(node, xpath);
    }

    /**
     * Transform DOM to byte array.
     *
     * @param node root node to be transformed
     * @return valid xml encoded to byte array.
     */
    public byte[] toBytes(Node node) {
        return XmlUtils.toBytes(node);
    }

    @Override
    public String[] getName() {
        return new String[]{"xml"};
    }
}
