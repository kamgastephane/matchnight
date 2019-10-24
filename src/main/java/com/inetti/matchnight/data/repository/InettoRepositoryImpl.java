package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.Inetto;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class InettoRepositoryImpl implements InettoRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(InettoRepositoryImpl.class);
    private static final String PURGE_MSG = "purge inetti: all entries";
    private final MongoTemplate mongoTemplate;

    @Autowired
    public InettoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Inetto> findInettiCached(Pageable pageable) {
        return mongoTemplate.find(new Query().with(pageable), Inetto.class);
    }

    @Override
    public boolean update(String id, Update update) {
         UpdateResult result = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)), update, Inetto.class);
        LOGGER.debug(PURGE_MSG);
        return result.getModifiedCount() > 0;
    }

    @Override
    public void purgeCache() {
        LOGGER.debug(PURGE_MSG);
    }

    @Override
    public Inetto saveAndInvalidate(Inetto inetto) {
        Inetto result = mongoTemplate.save(inetto);
        LOGGER.debug(PURGE_MSG);
        return result;

    }

    @Override
    public Long getInettiCount() {
        return mongoTemplate.count(new Query(), Inetto.class);
    }
}
