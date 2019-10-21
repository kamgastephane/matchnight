package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.SupportRequest;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class RequestRepositoryImpl implements RequestRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRepositoryImpl.class);

    private final MongoTemplate template;

    @Autowired
    public RequestRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public boolean updateRequest(@NotNull List<ObjectId> ids, @NotNull Update update) {

        Objects.requireNonNull(ids);
        Objects.requireNonNull(update);

        UpdateResult result = template.updateMulti(query(where("_id").in(ids)), update, SupportRequest.class);
        LOGGER.debug("purge requests: objectId: {}", ids);

        return result.getModifiedCount()>0;
      }

    @Override
    public void purgeById(@NotNull ObjectId id) {
        LOGGER.debug("purge request: objectId: {}", id);
    }

    @Override
    public boolean archiveByProjectId(@NotNull String projectId) {

        Update update = new Update();
        update.set(SupportRequest.ARCHIVED, true);
        UpdateResult result = template.updateMulti(Query.query(Criteria.where(SupportRequest.PROJECT_ID).is(projectId)), update, SupportRequest.class);
        LOGGER.debug("purge requests: projectId: {}", projectId);

        return (result.getModifiedCount() > 0);

    }

    @Override
    public List<SupportRequest> findByProjectId(@NotNull String projectId, @NotNull Pageable pageable) {
        Query query = Query.query (Criteria.where(SupportRequest.PROJECT_ID).is(projectId))
                .addCriteria(Criteria.where(SupportRequest.ARCHIVED).is(false))
                .with(pageable);
        return template.find(query, SupportRequest.class);
    }
}
