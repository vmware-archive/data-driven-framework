/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmware.qe.framework.datadriven.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XMLUtil encapsulates general XML related utility methods.<br>
 */
public class XMLUtil {
    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);

    /**
     * Finds all the children node whose tag names are in tags ArrayList. For example to get <one>
     * and <two> in Vector of Node from the following XML node : <parent> <one> something </one>
     * <two> something </two> <three> something </three> </parent> you pass <parent> node along with
     * this list {"one","two"}
     * 
     * @param parentNode : parentNode to lookup into for tags
     * @param tags : list of tags to be found among parentNode children
     * @return map of <NodeName, Node> that is found based on tags given in tags arrayList
     */
    public static HashMap<String, Node> getChildrenByTagNames(Node parentNode,
            ArrayList<String> tags) {
        HashMap<String, Node> resultList = new HashMap<String, Node>();
        String tagFromList = null;
        Node foundNode = null;
        if (tags.size() > 0 || parentNode != null) {
            for (int tagNo = 0; tagNo < tags.size(); tagNo++) {
                tagFromList = tags.get(tagNo).toString();
                foundNode = XMLUtil.getInnerNodeByTagName(parentNode, tagFromList);
                if (!resultList.containsKey(tagFromList)) {
                    resultList.put(tagFromList, foundNode);
                }
            }
        }
        return resultList.size() == 0 ? null : resultList;
    }

    /**
     * Puts the node attributes (if it has any) into HashMap<String,String>
     * 
     * @param node : XML node to look for its attributes
     * @return Hashmap<String,String> of the node attributes
     */
    public static HashMap<String, String> getAttributes(Node node) {
        String attrName = null;
        String attrValue = null;
        HashMap<String, String> attributesMap = new HashMap<String, String>();
        NamedNodeMap attributes = node.getAttributes();
        for (int attrIndex = 0; attributes != null && attrIndex < attributes.getLength(); attrIndex++) {
            attrName = attributes.item(attrIndex).getNodeName();
            attrValue = attributes.item(attrIndex).getNodeValue();
            attributesMap.put(attrName, attrValue);
        }
        return attributesMap.size() == 0 ? null : attributesMap;
    }

    /**
     * Puts the node attributes (if it has any) into HashMap<String,String> Retrieves only those
     * attributes that are in attr ArrayList Ignores other attributes values that are in node
     * 
     * @param node : XML node to look for its attributes
     * @param attrNames : attributes to fetch from node
     * @return Hashmap<String,String> of the node attributes
     */
    public static HashMap<String, String> getAttributesByName(Node node, ArrayList<String> attrNames) {
        String attrName = null;
        String attrValue = null;
        HashMap<String, String> attributesMap = new HashMap<String, String>();
        NamedNodeMap attributes = node.getAttributes();
        for (int attrIndex = 0; attrIndex < attributes.getLength(); attrIndex++) {
            attrName = attributes.item(attrIndex).getNodeName();
            attrValue = attributes.item(attrIndex).getNodeValue();
            if (attrNames.contains(attrName)) {
                attributesMap.put(attrName, attrValue);
            }
        }
        return attributesMap.size() == 0 ? null : attributesMap;
    }

    /**
     * Goes through inner nodes of parentNode and find the first node with specified tagName
     * 
     * @param parentNode : Node of the parent
     * @param tagName : tag name
     * @return Node : inner node of the parent that has the specific tag name
     */
    public static Node getInnerNodeByTagName(Node parentNode, String tagName) {
        Node node = null;
        Node child = null;
        if (parentNode != null) {
            NodeList children = parentNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE
                        && child.getNodeName().equalsIgnoreCase(tagName)) {
                    node = child;
                    break;
                }
            }
        }
        return node;
    }

    /**
     * Extracts the inner text value from a xml node accept : <node>value</node> and return value
     * string
     * 
     * @param node XML node to extract inner text from
     * @return innerText if its not empty otherwise null.
     */
    private static String getNodeTextValue(Node node) {
        String innerText = null;
        if (node != null) {
            Node textNode = node.getFirstChild();
            if (textNode != null && textNode.getNodeType() == Node.TEXT_NODE) {
                innerText = textNode.getNodeValue();
                innerText = innerText.trim();
                if (innerText.length() == 0) {
                    innerText = null;
                }
            }
        }
        return innerText;
    }

    /**
     * Finds all the children node and put it in HashMap<String,Node> where it represents tagName
     * and Node For example to get all the children of tag <parent> from the following xml node :
     * <parent> <one> something </one> <two> something </two> <three> something </three> </parent>
     * you pass <parent> node and it returns {{("one",<one>) ("two",<two>) ("three",<three>)}
     * 
     * @param parentNode : parentNode to lookup into for tags
     * @return HashMap of <NodeName,Node> that is found based on tags given in tags arrayList
     */
    public static HashMap<String, Node> getChildrenByTagNames(Node parentNode) {
        HashMap<String, Node> resultList = new HashMap<String, Node>();
        Node child = null;
        if (parentNode != null) {
            NodeList children = parentNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    resultList.put(child.getNodeName(), child);
                }
            }
        }
        return resultList.size() == 0 ? null : resultList;
    }

    /**
     * Finds all the children node from the XML node, not considering the tag name. For example if
     * below is the input XML. <parent> <child> zero </child> <child> one </child> <child> two
     * </child> </parent> The result List will have below three elements in a sequence. zero, one,
     * two
     * 
     * @param parentNode parentNode to lookup into for child nodes.
     * @return List of element nodes.
     */
    public static List<Node> getChildren(Node parentNode) {
        List<Node> resultList = new ArrayList<Node>();
        Node child = null;
        if (parentNode != null) {
            NodeList children = parentNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    resultList.add(child);
                }
            }
        }
        return resultList.size() == 0 ? null : resultList;
    }

    /**
     * Creates the parser instance based on dom parser factory Validates the xml file against the
     * XSD schema Return the documentElement of the xml document if validations succeeded
     * 
     * @param xmlFile - XML file to be parsed
     * @param xmlSchema - XSD that XML file should be validated against
     * @return documentElement of the XML file
     */
    public static Document getXmlDocumentElement(String xmlFile, String xmlSchema) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(true);
        factory.setNamespaceAware(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", XMLUtil.class
                .getResource(xmlSchema).getFile());
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());
        // Parse the document from the classpath.
        URL xmlFileUri = XMLUtil.class.getResource(xmlFile);
        if (null == xmlFileUri) {
            log.error("Unable to find file on classpath: " + xmlFile);
            return null;
        }
        return builder.parse(xmlFileUri.getFile());
    }

    /**
     * Creates the parser instance based on DOM parser factory, using the contents of a XML file.
     * Validation is not done. Return the documentElement of the XML document
     * 
     * @param xmlFile - XML file to be parsed
     * @return documentElement of the XML file
     */
    public static Document getXmlDocumentElement(String xmlFileContents) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Reader reader = new StringReader(xmlFileContents);
        InputSource inputSource = new InputSource(reader);
        factory.setCoalescing(true);
        factory.setNamespaceAware(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputSource);
    }

    /**
     * Gets the descendant elements which have an attribute with the specified name and value
     * 
     * @param parentNode - Node whose children are to be searched
     * @param attrId - attribute name
     * @param attrVal - attribute value
     * @return List of all children nodes
     * @throws Exception
     */
    public static List<Element> getElements(Node parentNode, String attrId, String attrVal)
            throws Exception {
        NodeList nodeList = parentNode.getChildNodes();
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (attrVal.equalsIgnoreCase(element.getAttribute(attrId))) {
                    elements.add(element);
                }
                if (element.hasChildNodes()) {
                    // recursive call to search for child's children
                    List<Element> children = getElements(element, attrId, attrVal);
                    if (children != null) {
                        elements.addAll(children);
                    }
                }
            }
        }
        return elements.isEmpty() ? null : elements;
    }

    /**
     * Gets the descendant elements which have an attribute with the specified name and value
     * 
     * @param xmlInput - XML data as string
     * @param attrId - attribute name
     * @param attrVal - attribute value
     * @return List of all children nodes
     * @throws Exception
     */
    public static List<Element> getElements(String xmlInput, String attrId, String attrVal)
            throws Exception {
        Document doc = XMLUtil.getXmlDocumentElement(xmlInput);
        return XMLUtil.getElements(doc, attrId, attrVal);
    }

    /**
     * Validates an XML file with its schema.
     * 
     * @param xmlFileContents - The XML file contents
     * @param xmlSchemaFilePath - XSD file path
     * @throws SAXException
     * @throws Exception
     */
    public static void validateXML(String xmlFileContents, String xmlSchemaFilePath)
            throws Exception {
        Document document = XMLUtil.getXmlDocumentElement(xmlFileContents);
        validateDocument(document, new FileInputStream(xmlSchemaFilePath));
    }

    /**
     * Validates an XML file with its schema.
     * 
     * @param xmlFileStream - the inputStream of the xml content
     * @param xsdFileStream - the inputStream of the xsd content
     * @throws Exception
     */
    public static void validateXML(InputStream xmlFileStream, InputStream xsdFileStream)
            throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();
        Document document = parser.parse(xmlFileStream);
        validateDocument(document, xsdFileStream);
    }

    /**
     * Validates the given document with its schema.
     * 
     * @param document - The XML file contents
     * @param schemaFileStream - the inputStream of the schema content
     * @throws Exception
     */
    public static void validateDocument(Document document, InputStream schemaFileStream)
            throws Exception {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(schemaFileStream);
        Schema schema = factory.newSchema(schemaFile);
        // create a Validator instance, which can be used to validate an
        // instance document
        Validator validator = schema.newValidator();
        // validate the DOM tree
        validator.validate(new DOMSource(document));
    }

    /**
     * Get child nodes of the first element that has the specified tag name and also contains the
     * specified attribute id and value
     * 
     * @param xmlFile XML file contents
     * @param elementName name of the required element
     * @param attrId Id of an attribute in element
     * @param attrVal attribute value for the given attribute
     * @return NodeList childNodes if the given element name with the given attributeId and
     *         attributeValue exists, otherwise null
     * @throws Exception
     */
    public static NodeList getChildNodes(String xmlFile, String elementName, String attrId,
            String attrVal) throws Exception {
        NodeList childNodes = null;
        Document xmlDoc = getXmlDocumentElement(xmlFile);
        NodeList elements = xmlDoc.getElementsByTagName(elementName);
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (element.hasAttribute(attrId)) {
                if (element.getAttribute(attrId).equals(attrVal)) {
                    childNodes = element.getChildNodes();
                    break;
                }
            }
        }
        return childNodes;
    }

    /**
     * Saves Document instance to XML file to the specified filePath.
     * 
     * @param xmlDoc - Document instance
     * @param destPath - dest path where the XML file is to be saved.
     * @throws Exception
     */
    public static void saveDocument(Document xmlDoc, String destPath) throws Exception {
        Source xmlSource = new DOMSource(xmlDoc);
        OutputStream os = new FileOutputStream(destPath);
        Result result = new StreamResult(os);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        /* output to file */
        transformer.transform(xmlSource, result);
    }

    /**
     * Returns the text value of a node where a sibling tag name and value is known.
     * <p>
     * For example, in the XML snippet below, if you pass in "cat" as the parentTagName, "name" as
     * the childTagName, and "MrBiggles" as the childTagValue, and specify "color" as the
     * siblingTagName, the method would return "Grey".
     * <p>
     * Example XML:
     * 
     * <pre>
     * &lt;cat&gt;
     *    &lt;name&gt;MrBiggles&lt;/name&gt;
     *    &lt;color&gt;Grey&lt;/color&gt;
     * &lt;/cat&gt;
     * &lt;cat&gt;
     *    &lt;name&gt;Boris&lt;/name&gt;
     *    &lt;color&gt;Black&lt;/color&gt;
     * &lt;/cat&gt;
     * </pre>
     * 
     * @param xmlDocAsString The XML document as a String
     * @param parentTagName The tag name of the parent elements that should contain the sibling
     *            nodes
     * @param childTagName The tag name of the known child element
     * @param childTagValue The known text value of the child element
     * @param siblingTagName The tag name of the sibling node
     * @return String The text value of the sibling node
     * @throws Exception
     */
    public static String findSiblingNodeValue(String xmlDocAsString, String parentTagName,
            String childTagName, String childTagValue, String siblingTagName) throws Exception {
        String siblingNodeValue = null;
        Document xmlDoc = XMLUtil.getXmlDocumentElement(xmlDocAsString);
        NodeList parentNodes = xmlDoc.getElementsByTagName(parentTagName);
        if (parentNodes != null) {
            for (int i = 0; i < parentNodes.getLength(); i++) {
                Node parentNode = parentNodes.item(i);
                HashMap<String, Node> childNodes = XMLUtil.getChildrenByTagNames(parentNode);
                Node childNode = childNodes.get(childTagName);
                String tmpChildNodeVal = XMLUtil.getNodeTextValue(childNode);
                if (childTagValue.equals(tmpChildNodeVal)) {
                    log.info("Found node: " + childTagName + " with value: " + childTagValue);
                    Node siblingNode = childNodes.get(siblingTagName);
                    if (siblingNode != null) {
                        siblingNodeValue = XMLUtil.getNodeTextValue(siblingNode);
                        log.info("Found the sibling node: " + siblingTagName + " = "
                                + siblingNodeValue);
                        break;
                    } else {
                        log.warn("No sibling node named: " + siblingTagName + " was found.");
                    }
                }
            }
        } else {
            log.warn("There were no elements with tag name: " + parentTagName);
        }
        return siblingNodeValue;
    }

    /**
     * Returns the values of all tags under the root element with the specified tag name.
     * 
     * @param xml XML String to search
     * @param childTagName The name of the tag to get the value of
     * @return List of Strings - the text of the desired metadata.
     * @throws Exception
     */
    public static List<String> getNodeTextValuesByTagName(String xml, String childTagName)
            throws Exception {
        List<String> values = new ArrayList<String>();
        Document xmldoc = XMLUtil.getXmlDocumentElement(xml);
        Node parentNode = xmldoc.getDocumentElement();
        if (parentNode != null) {
            NodeList children = parentNode.getChildNodes();
            Node child = null;
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE
                        && child.getNodeName().equalsIgnoreCase(childTagName)) {
                    String nodeVal = XMLUtil.getNodeTextValue(child);
                    values.add(nodeVal);
                }
            }
        }
        return values;
    }

    /**
     * Returns the text value of the first tag under the root element with the specified tag name.
     * 
     * @param xml XML String to search
     * @param childTagName The name of the tag to get the value of
     * @return List of Strings - the text of the desired metadata.
     * @throws Exception
     */
    public static String getNodeTextValueByTagName(String xml, String childTagName)
            throws Exception {
        Document xmldoc = XMLUtil.getXmlDocumentElement(xml);
        Node parentNode = xmldoc.getDocumentElement();
        Node metadataNode = XMLUtil.getInnerNodeByTagName(parentNode, childTagName);
        String nodeVal = XMLUtil.getNodeTextValue(metadataNode);
        return nodeVal;
    }

    public static Vector<String> getValueOnTag(Document doc, String elementName) {
        Vector<String> elementNames = new Vector<String>();
        NodeList list = doc.getElementsByTagName(elementName);
        log.info("XML Elements: ");
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            elementNames.add(element.getAttribute("name"));
            log.info("Element Name : " + element.getLocalName() + " Value :"
                    + element.getAttribute("name"));
        }
        return elementNames;
    }

    public static Document getXMLDocument(String xmlFileName) {
        Document doc = null;
        try {
            File file = new File(xmlFileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
        } catch (Exception e) {
            log.error("Exception  " + e.getMessage());
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Given a W3C Node, gives the XML string representation
     * 
     * @param node a W3C XML Node
     * @return node as string
     */
    public static String getNodeAsString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            log.error("Caught Exception ", e);
        } catch (TransformerException e) {
            log.error("Caught Exception ", e);
        }
        return null;
    }

    public static void writeToXML(HierarchicalConfiguration config, String fileName)
            throws Exception {
        XMLConfiguration xmlConfiguration = new XMLConfiguration();
        xmlConfiguration.addNodes("test-data", config.getRootNode().getChildren());
        xmlConfiguration.save(fileName);
    }
}
