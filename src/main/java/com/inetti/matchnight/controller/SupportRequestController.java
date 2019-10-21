package com.inetti.matchnight.controller;


import com.inetti.matchnight.controller.validators.SupportRequestValidator;
import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.model.SupportRequest;
import com.inetti.matchnight.data.request.CreateSupportRequest;
import com.inetti.matchnight.data.request.UpdateSupportRequest;
import com.inetti.matchnight.data.response.BaseResponse;
import com.inetti.matchnight.data.response.CreateSupportResponse;
import com.inetti.matchnight.service.SupportRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.inetti.matchnight.data.response.BaseResponse.success;
import static com.inetti.matchnight.data.response.BaseResponse.with;

@RestController
@RequestMapping({"/v1/requests"})
public class SupportRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportRequestController.class);

    private final SupportRequestService supportRequestService;
    private final SupportRequestValidator requestValidator;

    @Autowired
    public SupportRequestController(SupportRequestService supportRequestService, SupportRequestValidator requestValidator) {

        this.supportRequestService = supportRequestService;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Set<SupportRequest>>> getRequest(@PathVariable(value = "id") String id) {
        requestValidator.validate(id);
        Optional<SupportRequest> request = supportRequestService.getRequest(id);
        Set<SupportRequest> set = request.map(Collections::singleton).orElse(new HashSet<>());
        return new ResponseEntity<>(with(set), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<List<SupportRequest>>> getRequestList(@RequestParam(value = "projectId", required = true) String projectId,
                                                       @RequestParam(value = "offset", required = false) Long offset,
                                                       @RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "sort", required = false) Sort sort) {

        Pageable pageable = OffsetLimitRequest.of(offset, limit, Optional.ofNullable(sort).orElse(Sort.by(Sort.Direction.DESC,"projectId")));
        List<SupportRequest> requests = supportRequestService.getRequestList(projectId, pageable);
        return new ResponseEntity<>(with("requests", requests), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CreateSupportResponse>>  createSupportRequest(@Valid @RequestBody CreateSupportRequest request) {
        SupportRequest response = supportRequestService.createSupportRequest(request.getProjectId(), request.getLocation(), request.getResponseTime(),
                request.getDuration(), request.getSource(), request.getEventId());
        return new ResponseEntity<>(with(new CreateSupportResponse(response.getId())), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateSupportRequest(@NotNull  @PathVariable(value = "id") String id,
                                                               @Valid @RequestBody UpdateSupportRequest request) {
        requestValidator.validate(id);
        supportRequestService.updateRequest(id, request);
        return new ResponseEntity<>(success(), HttpStatus.OK);
    }

    /**
     * we handle the {@param projectId} as the default parameter and if it is empty we check the second
     * query parameter {@param requestIds}
     */
    @PostMapping("/archiveRequest")
    public ResponseEntity<BaseResponse> archiveSupportRequest(@RequestParam(value = "projectId") String projectId,
                                      @RequestParam(value = "requestIds") List<String> requestIds) {

        requestValidator.validateArchiveRequest(projectId, requestIds);
        if (!StringUtils.isEmpty(projectId)) {
            supportRequestService.archiveRequestByProjectId(projectId);

        } else if (requestIds != null && !requestIds.isEmpty()) {

            requestIds.forEach(requestValidator::validate);
            supportRequestService.archiveRequests(requestIds);
        }
        return new ResponseEntity<>(success(), HttpStatus.OK);


    }



}
