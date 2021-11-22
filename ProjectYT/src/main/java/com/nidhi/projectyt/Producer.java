package com.nidhi.projectyt;

import com.nidhi.projectyt.jms.JmsFactory;
import com.nidhi.projectyt.jms.JmsProducer;
import com.nidhi.projectyt.youtube.SearchVideo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.services.youtube.model.SearchResult;

public class Producer {

    private static final Logger LOG = LogManager.getLogger(Producer.class);
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final ObjectMapper OBJ_MAPPER = new XmlMapper();

    public static void main(String[] args) {
        try {
            Producer producer = new Producer();
            producer.start();
        } catch (Exception t) {
            LOG.fatal(t);
            System.exit(-1);
        }
    }

    public Producer() {
    }

    public void start() {
        try {
            List<SearchResult> searchResults = queryYouTube();
            JmsProducer jmsProducer = JmsFactory.getInstance().getJmsProducer();
            for (SearchResult searchResult : searchResults) {
                searchResult.getId().put("URL", BASE_YOUTUBE_URL + searchResult.getId().getVideoId());
                String xml = getXML(searchResult);
                jmsProducer.sendMessage(Config.QUEUE_A.getString(), xml);
            }

            LOG.info("Number of videos sent : " + searchResults.size());
        } catch (Exception e) {
            LOG.fatal(e.getMessage(), e);
            System.exit(-1);
        }
    }

    private String getXML(SearchResult searchResult) throws JsonProcessingException {
        OBJ_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        String xml = OBJ_MAPPER.writeValueAsString(searchResult);
        LOG.debug("getXML\n" + xml);
        return xml;
    }

    private List<SearchResult> queryYouTube() throws Exception {
        SearchVideo search = new SearchVideo();
        return search.queryYouTube(Config.QUERY_STRING.getString());
    }

}
