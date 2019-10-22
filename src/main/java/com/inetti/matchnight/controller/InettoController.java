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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;

/**
 * controller to access users repository
 */
@RestController
@RequestMapping({"/v1/user"})
@Api(tags = "inetto")
public class InettoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InettoController.class);

    private final InettoService<Inetto> inettoService;

    @Autowired
    public InettoController(InettoService<Inetto> service) {
        this.inettoService = service;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateInettoRequest request) {
        //todo get the exception thrown when we have duplicate and catch itproperlyh
        Inetto inetto = new Inetto.InettoBuilder()
                .withUsername(request.getUsername())
                .withRole(request.getRole())
                .withContacts(request.getContacts())
                .build();
        inettoService.createInetto(inetto);
        LOGGER.debug("user created with data {}", request);
    }

    @GetMapping
    //todo test with min at 2
    public ResponseEntity<BaseResponse<Set<Inetto>>> search(@Min (3) @RequestParam("query") String query) {
        final Set<Inetto> result = inettoService.search(query);
        return ResponseEntity.ok(BaseResponse.with(result));
    }
}
