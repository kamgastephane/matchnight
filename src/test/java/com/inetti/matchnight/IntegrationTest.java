package com.inetti.matchnight;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {Profiles.TEST})
public abstract class IntegrationTest {

    @LocalServerPort
    protected int port;

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
