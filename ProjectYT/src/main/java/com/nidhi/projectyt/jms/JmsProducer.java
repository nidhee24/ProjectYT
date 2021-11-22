package com.nidhi.projectyt.jms;

public interface JmsProducer {

    void sendMessage(String queueName, String message) throws Exception;

}
