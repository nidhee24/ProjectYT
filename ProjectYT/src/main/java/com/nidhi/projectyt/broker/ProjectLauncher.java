package com.nidhi.projectyt.broker;

import com.nidhi.projectyt.Config;

import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectLauncher {

    private static final Logger LOG = LogManager.getLogger(ProjectLauncher.class);

    public static void main(String[] args) {
        try {
            LOG.info("JMS starts*********");
            BrokerService b = new BrokerService();
            b.addConnector(Config.PRESENT_ADDRESS.getString()); // merge new connector for given address
            b.start();
        } catch (Exception ex) {
            LOG.fatal(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

}
