package com.nidhi.projectyt.jms;

public class JmsFactory {

    private JmsConsumer jmsConsumer;
    private JmsProducer jmsProducer;

    private JmsFactory() {
    }

    public static JmsFactory getInstance() {
        return ElementHolder.INSTANCE;
    }

    public JmsConsumer getJmsConsumer() throws Exception {
        if (jmsConsumer == null) {
            jmsConsumer = (JmsConsumer) getImplementation(JmsConsumer.class);
        }
        return jmsConsumer;
    }

    public JmsProducer getJmsProducer() throws Exception {
        if (jmsProducer == null) {
            jmsProducer = (JmsProducer) getImplementation(JmsProducer.class);
        }
        return jmsProducer;
    }

    private Object getImplementation(Class<?> class1) throws Exception {
        String name = class1.getPackage().getName()  + class1.getSimpleName();
        Class<?> forName = Class.forName(name);
        Object newInstance = forName.getDeclaredConstructor().newInstance();
        return newInstance;
    }

    private static class ElementHolder {

        private static final JmsFactory INSTANCE = new JmsFactory();
    }
}
