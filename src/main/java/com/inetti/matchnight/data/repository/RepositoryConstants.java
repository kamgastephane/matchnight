package com.inetti.matchnight.data.repository;

public final class RepositoryConstants {

    //inetto repository
    static final String INETTO_BY_USERNAME = "{ 'username' : ?0 }";
    public static final String INETTO_CACHE_NAME = "inetto_cache";

    //event repository
    static final String EVENT_BY_ID = "{ 'externalId' : ?0 }";
    public static final String MATCH_EVENT_CACHE_NAME = "match_event_cache";
    static final String EVENT_CACHE_KEY_MONTH = "#year.toString().concat('-').concat(#month.toString())";

    //request
    static final String REQUEST_BY_ID = "{ '_id' : ?0 }";
    public static final String REQUEST_CACHE_NAME = "request_cache";
    static final String REQUEST_BY_PROJECT_ID = "{ 'projectId' : ?0, 'archived' : false }";
    static final String REQUEST_CACHE_KEY_ID = "#id.toString()";
}
