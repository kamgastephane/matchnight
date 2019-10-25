package com.inetti.matchnight.controller.validators;

import com.inetti.matchnight.controller.exception.InvalidInputArgumentException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * this class will contain all the logic to validate input parameter passed to {@link com.inetti.matchnight.controller.SupportRequestController}
 */
@Service
public class SupportRequestValidator {

    public SupportRequestValidator() {
        //empty constructor
    }

    public void validate(String id) {
        if (StringUtils.isEmpty(id) || !ObjectId.isValid(id)) {
            throw new InvalidInputArgumentException("id");
        }
    }

    /**
     * As a rule, only one of the input parameter should be a valid one
     * @param projectId the projectId
     * @param requestIds the list of requestIds
     */
    public void validateArchiveRequest(String projectId, List<String> requestIds) {
        if (projectId != null && requestIds != null) {
            throw new InvalidInputArgumentException("", "only one of \"projectId\" and \"requestId\" should be provided");
        } else if (projectId == null && requestIds != null) {
            throw new InvalidInputArgumentException("", "one of \"projectId\" and \"requestId\" should be not null");
        }
    }



}
