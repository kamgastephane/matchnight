package com.inetti.matchnight.controller;

import com.inetti.matchnight.data.model.Inetto;
import com.inetti.matchnight.data.request.CreateInettoRequest;
import com.inetti.matchnight.data.response.BaseResponse;
import com.inetti.matchnight.service.InettoService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * controller to access users repository
 */
@RestController
@RequestMapping({InettoController.INETTO_CRTL_URL})
@Api(tags = "inetto")
@Validated
public class InettoController {
    public static final String INETTO_CRTL_URL = "/v1/user";

    private static final Logger LOGGER = LoggerFactory.getLogger(InettoController.class);

    private final InettoService<Inetto> inettoService;

    @Autowired
    public InettoController(InettoService<Inetto> service) {
        this.inettoService = service;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody CreateInettoRequest request) {
        Inetto inetto = new Inetto.InettoBuilder()
                .withUsername(request.getUsername())
                .withRole(request.getRole())
                .withContacts(request.getContacts())
                .build();
        inettoService.createInetto(inetto);
        LOGGER.debug("user created with data {}", request);
        return ResponseEntity.ok(BaseResponse.success());
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Set<Inetto>>> search(@RequestParam("query") @Size(min = 3, max = 20) String query) {
        final Set<Inetto> result = inettoService.search(query);
        return ResponseEntity.ok(BaseResponse.with(result));
    }
}
