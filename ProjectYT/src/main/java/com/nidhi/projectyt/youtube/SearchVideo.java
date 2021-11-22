package com.nidhi.projectyt.youtube;

import com.nidhi.projectyt.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

public class SearchVideo {

    private static final Logger LOG = LogManager.getLogger(SearchVideo.class);
    public static final JsonFactory JSON = new JacksonFactory();
    public static final HttpTransport httpTransport = new NetHttpTransport();

    private Properties properties = new Properties();

    public SearchVideo() throws IOException {
        try {
            InputStream in = SearchVideo.class.getResourceAsStream("/" + Config.YT_PROPERTIES_FILENAME.getString());
            properties.load(in);
        } catch (IOException e) {
            LOG.fatal("There was an error reading " + Config.YT_PROPERTIES_FILENAME.getString() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    public List<SearchResult> queryYouTube(String query) throws GoogleJsonResponseException, IOException {
        try {
            YouTube youtube = new YouTube.Builder(httpTransport, JSON, (HttpRequest request) -> {
            }).setApplicationName("projectyt-producer").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(properties.getProperty("youtube.apikey"));
            search.setQ(Config.QUERY_STRING.getString());
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search = search.setMaxResults(Config.RESULTS_OF_VIDEOS.getLong());
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null && LOG.isTraceEnabled()) {
                prettyPrint(searchResultList.iterator(), Config.QUERY_STRING.getString());
            }
            return searchResultList;
        } catch (GoogleJsonResponseException e) {
            LOG.fatal("Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e);
            throw e;
        } catch (IOException e) {
            LOG.fatal("Input output error: " + " : " + e.getMessage(), e);
            throw e;
        } catch (Throwable t) {
            LOG.fatal(t.getMessage(), t);
            throw t;
        }
    }

    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
        LOG.debug("\n=============================================================");
        LOG.debug("   First " + Config.RESULTS_OF_VIDEOS.getLong() + " videos for search on \"" + query + "\".");
        LOG.debug("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            LOG.debug(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                LOG.debug(" Video Id:  " + rId.getVideoId());
                LOG.debug(" Title      : " + singleVideo.getSnippet().getTitle());
                LOG.debug(" Thumbnail  : " + thumbnail.getUrl());
                LOG.debug("\n-------------------------------------------------------------\n");
            }
        }
    }
}
