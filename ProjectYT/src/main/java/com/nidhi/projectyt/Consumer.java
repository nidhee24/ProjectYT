package com.nidhi.projectyt;

import com.nidhi.projectyt.jms.JmsConsumer;
import com.nidhi.projectyt.jms.JmsFactory;
import com.nidhi.projectyt.jms.JmsProducer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Consumer {

    private static final Logger LOG = LogManager.getLogger(Consumer.class);

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.start();
    }

    public Consumer() {
    }

    public void start() {
        try {
            JmsConsumer jmsConsumer = JmsFactory.getInstance().getJmsConsumer();
            JmsProducer jmsProducer = JmsFactory.getInstance().getJmsProducer();
            int in = 0;
            int out = 0;

            while (true) {
                String xml = jmsConsumer.getMessage(Config.QUEUE_A.getString());
                in++;
                LOG.info("Number of videos: " + in + " for " + Config.QUEUE_A.getString());

                String newXml = change(xml, "(?i)telecom", "telco");
                if (newXml != null && newXml.length() != 0) {
                    jmsProducer.sendMessage(Config.QUEUE_B.getString(), newXml);
                    out++;
                    LOG.info("Message number " + out + " sent to " + Config.QUEUE_B.getString());
                }
            }
        } catch (Exception e) {
            LOG.fatal(e.getMessage(), e);
            System.exit(-1);
        }
    }

    private String change(String newString, String before, String after) {
        // Step1: get document builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//to create new instance
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder(); // created document builder

            // step 2: get document
            Document document = docBuilder.parse(new ByteArrayInputStream(newString.getBytes()));

            // step 3: normalization of xml file
            document.getDocumentElement().normalize();

            // step 4: get elements by tag name
            NodeList nodeList = document.getElementsByTagName("snippet");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeName().equals("title")) {
                    String textContent = child.getTextContent();
                    String replaced = textContent.replaceAll(before, after);
                    child.setTextContent(replaced);
                }
            }

            TransformerFactory t_factory = TransformerFactory.newInstance();
            Transformer transformer = t_factory.newTransformer();

            DOMSource input_source = new DOMSource(document);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult output_result = new StreamResult(byteArrayOutputStream);
            transformer.transform(input_source, output_result);

            String newFileXml = byteArrayOutputStream.toString();
            return newFileXml;
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            LOG.error("Error: " + ex.getMessage(), ex);
            LOG.error("Input error: \n" + newString);
        }
        return newString;
    }
}
