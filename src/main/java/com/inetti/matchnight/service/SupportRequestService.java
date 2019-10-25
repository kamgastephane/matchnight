package com.inetti.matchnight.service;

import com.inetti.matchnight.data.model.Event;
import com.inetti.matchnight.data.model.SupportRequest;
import com.inetti.matchnight.data.repository.RequestRepository;
import com.inetti.matchnight.data.request.UpdateSupportRequest;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SupportRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportRequestService.class);


    private final RequestRepository requestRepository;

    @Autowired
    public SupportRequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Load the support request with id {@code requestId} using cache
     * @param requestId the id of the support request
     * @return an optional
     */
    public Optional<SupportRequest> getRequest(String requestId) {

        return requestRepository.findByIdCached(new ObjectId(requestId));
    }


    /**
     * @param projectId the id of the project
     * @param pageable the pagination parameters
     * @return a single plage containing some support requests
     */
    public List<SupportRequest> getRequestList(String projectId, Pageable pageable) {
        return requestRepository.findByProjectId(projectId, pageable);
    }


    /**
     * Update the support request with id {@code requestId}
     * @param requestId the id of the support request
     * @param request the request
     * @return true if the update was successful false otherwise
     */
    public boolean updateRequest(@NotNull String requestId, UpdateSupportRequest request ) {
        Objects.requireNonNull(requestId);

        if (Stream.of(request.getLocation(), request.getResponseTime(), request.getDuration(), request.getSource(),
                request.getEventId(), request.getInetti()).anyMatch(Objects::nonNull)){
            Update update = new Update();
            ObjectId objectId = new ObjectId(requestId);
            build(update, SupportRequest.LOCATION, request.getLocation());
            build(update, SupportRequest.RESPONSE_TIME, request.getResponseTime());
            build(update, SupportRequest.DURATION, request.getDuration());
            build(update, SupportRequest.REQUEST_SOURCE, request.getSource());
            build(update, SupportRequest.EVENT_ID, request.getEventId());
            build(update, SupportRequest.INETTO_ID, request.getInetti());

            boolean success = requestRepository.updateRequest(Collections.singletonList(objectId), update);
            if (success) {
                requestRepository.purgeById(objectId);
                LOGGER.debug("support request updated: value: {}", request);
            }

        }
        return false;
    }

    private void build(Update update, String fieldName, Object value) {
        if (value != null) update.set(fieldName, value);
    }

    /** Create a support request
     * @param projectId the projectId
     * @param location the {@link SupportRequest.Location}
     * @param responseTime the {@link SupportRequest.ResponseTime}
     * @param duration the {@link SupportRequest.Duration}
     * @param source the source of the support request
     * @param eventId the Id of the corresponding {@link Event}
     * @return the created resource
     */
    public SupportRequest createSupportRequest(@NotNull String projectId,
                                        @NotNull SupportRequest.Location location,
                                        @NotNull SupportRequest.ResponseTime responseTime,
                                        @NotNull SupportRequest.Duration duration,
                                        String source,
                                        String eventId) {
        Objects.requireNonNull(projectId, "cannot create a supportRequest without a projectId");
        Objects.requireNonNull(location, "cannot create a supportRequest without a location");
        Objects.requireNonNull(responseTime, "cannot create a supportRequest without a responseTime");
        Objects.requireNonNull(duration, "cannot create a supportRequest without a duration");

        SupportRequest request = SupportRequest.of(projectId, source, location, responseTime, duration, eventId);

        SupportRequest result = requestRepository.save(request);
        LOGGER.debug("support request created: id {}, data: {}", result.getId(), result);
        return result;
    }


    /**
     * @param requestIds a collection containing the ids of request to archive
     * @return true if the request was successful false otherwise
     */
    public boolean archiveRequests(@NotNull List<String> requestIds) {

        Objects.requireNonNull(requestIds, "cannot delete a supportRequest without a requestId");
        List<ObjectId> objectIds = requestIds.stream().filter(Objects::nonNull)
                .map(ObjectId::new).collect(Collectors.toList());
        Update deleteUpdate = new Update();
        deleteUpdate.set(SupportRequest.ARCHIVED, true);
        boolean success = requestRepository.updateRequest(objectIds, deleteUpdate);
        LOGGER.debug("support request archived: id {}, success: {}", requestIds, success);
        return success;
    }

    /**
     * Archive all request corresponding to that projectId
     * @param projectId the request projectId
     * @return true if at least one request was updated successfully false otherwise
     */
    public boolean archiveRequestByProjectId(@NotNull String projectId) {
        boolean result = requestRepository.archiveByProjectId(projectId);
        LOGGER.debug("archive requests: projectId: {} result: {}", projectId, result);
        return true;
    }
}
