package com.nidhi.projectyt.jms;

public interface JmsConsumer {

    String getMessage(String queueName) throws Exception;

}
