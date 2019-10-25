package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.dto.MatchnightRole;
import com.inetti.matchnight.data.model.Inetto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;

public class InettoRepositoryImplTest extends MockMVCTest {

    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final MatchnightRole ROLE = MatchnightRole.ADMIN;

    @Autowired
    private InettoRepository repository;

    @Autowired
    private RedisTemplate<String, Object> template;

    private Inetto inetto;

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
        template.getConnectionFactory().getConnection().flushAll();

    }

    @Before
    public void setUp() throws Exception {
        inetto = new Inetto.InettoBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .withRole(ROLE)
                .withContacts("email", EMAIL)
                .build();

        repository.deleteAll();
        template.getConnectionFactory().getConnection().flushAll();

    }

    @Test
    public void testSave() {
        final Inetto save = repository.save(inetto);
        verify(save);
    }

    @Test
    public void testWithVersion() {
        final Inetto save = repository.save(inetto);
        Assert.assertEquals(inetto.withId(save.getRepositoryId()).withVersion(save.getVersion()), save);
    }

    @Test
    public void testFindIsCached() {
        final Inetto save = repository.save(inetto);
        final Optional<Inetto> usernameCached = repository.findByUsernameCached(USERNAME);
        Assert.assertTrue(usernameCached.isPresent());
        verify(save);
        Assert.assertEquals(true, template.hasKey(getCacheKey(save.getUsername())));
    }

    @Test
    public void testPurge() {
        final Inetto save = repository.save(inetto);
        final Optional<Inetto> usernameCached = repository.findByUsernameCached(USERNAME);
        Assert.assertEquals(true, template.hasKey(getCacheKey(save.getUsername())));
        repository.purgeCache();
        Assert.assertEquals(false, template.hasKey(getCacheKey(save.getUsername())));
    }

    @Test
    public void testFindAll() {
        final Inetto inetto2 = new Inetto.InettoBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withUsername(USERNAME+2)
                .withPassword(PASSWORD)
                .withRole(ROLE)
                .withContacts("email", EMAIL)
                .build();
        repository.save(inetto);
        repository.save(inetto2);

        final Pageable pageable = OffsetLimitRequest.of(0L, 10, Sort.by(Sort.Direction.DESC, "username"));
        List<Inetto> inettiCached = repository.findInettiCached(pageable);
        Assert.assertEquals(2, inettiCached.size());
        template.hasKey(getCacheKey(pageable.toString()));
    }

    @Test
    public void testUpdate() {
        final Inetto save = repository.save(inetto);
        Update update = new Update().set("username", EMAIL).set("contact.email", USERNAME)
                .set("role", MatchnightRole.PUBLISHER);
        repository.update(save.getId(), update);
        final Optional<Inetto> usernameCached = repository.findByUsernameCached(EMAIL);
        Assert.assertTrue(usernameCached.isPresent());
        Assert.assertEquals(USERNAME, usernameCached.get().getContacts().get("email") );
        Assert.assertEquals(EMAIL, usernameCached.get().getUsername() );
        Assert.assertEquals(MatchnightRole.PUBLISHER, usernameCached.get().getRole() );

    }


    @Test
    public void testDuplicate() {
        final Inetto save = repository.save(inetto);
        try {
            final Inetto duplicate = repository.save(inetto);
            Assert.fail();
        }catch (Exception e) {
            Assert.assertEquals(DuplicateKeyException.class, e.getClass());
        }
    }



    public void verify(Inetto inetto) {
        Assert.assertNotNull(inetto.getId());
        Assert.assertEquals(FIRST_NAME, inetto.getFirstName());
        Assert.assertEquals(LAST_NAME, inetto.getLastName());
        Assert.assertEquals(PASSWORD, inetto.getPassword());
        Assert.assertEquals(ROLE, inetto.getRole());
        Assert.assertNotNull(inetto.getVersion());
    }

    private String getCacheKey(String id) {
        return RepositoryConstants.INETTO_CACHE_NAME + "::" + id;
    }

}