package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.SupportRequest;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface RequestRepositoryCustom {

    boolean updateRequest(List<ObjectId> ids, Update update);

    void purgeById(ObjectId id);

    boolean archiveByProjectId(String projectId);

    List<SupportRequest> findByProjectId(String id, Pageable pageable);




}
