package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.SupportRequest;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface RequestRepositoryCustom {

    /**
     * apply the same update to multiple request and invalidate the cache
     * @param ids a list of ids
     * @param update an update
     * @return true if the operation was done with success
     */
    @CacheEvict(value = RepositoryConstants.REQUEST_CACHE_NAME, allEntries = true)
    boolean updateRequest(List<ObjectId> ids, Update update);


    /**
     * clean the cache corresponding to the item with this id
     * @param id the id
     */
    @CacheEvict(value = RepositoryConstants.REQUEST_CACHE_NAME, key = RepositoryConstants.REQUEST_CACHE_KEY_ID)
    void purgeById(ObjectId id);

    /**
     * Archive a project (which correspond to archiving all the requests belonging to that project)
     * @param projectId the id of the project
     * @return true if the operation was done with success
     */
    @CacheEvict(value = RepositoryConstants.REQUEST_CACHE_NAME, allEntries = true)
    boolean archiveByProjectId(String projectId);


    /**
     * Find all request belonging to this project
     * @param projectId the id
     * @param pageable the pageable
     * @return a list of request
     */
    List<SupportRequest> findByProjectId(String projectId, Pageable pageable);




}
