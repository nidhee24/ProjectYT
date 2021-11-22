package com.nidhi.projectyt.jms.impl;

import com.nidhi.projectyt.Config;
import com.nidhi.projectyt.jms.JmsConsumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsumerImplementation implements JmsConsumer {

    private static final Logger LOG = LogManager.getLogger(ConsumerImplementation.class);
    private static final ConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory(Config.PRESENT_ADDRESS.getString());

    public ConsumerImplementation() {
    }

    @Override
    public String getMessage(String queueName) throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            connection = CONNECTION_FACTORY.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();


            TextMessage message = (TextMessage) consumer.receive();
            LOG.debug("Received: \n" + message.getText());

            return message.getText();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
