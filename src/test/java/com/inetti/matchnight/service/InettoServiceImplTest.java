package com.inetti.matchnight.service;

import com.inetti.matchnight.data.dto.MatchnightRole;
import com.inetti.matchnight.data.model.Inetto;
import com.inetti.matchnight.data.repository.InettoRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.passay.PasswordGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InettoServiceImplTest {

    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final MatchnightRole ROLE = MatchnightRole.ADMIN;

    private InettoRepository repository;
    private PasswordEncoder passwordEncoder;

    private InettoServiceImpl service;

    private Inetto inetto;

    @Before
    public void setUp() throws Exception {
        repository = mock(InettoRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new InettoServiceImpl(repository, passwordEncoder, new PasswordGenerator());
        inetto = new Inetto.InettoBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .withRole(ROLE)
                .withContacts("email", EMAIL)
                .build();
    }

    @Test
    public void testFindById() {
        ObjectId id = ObjectId.get();
        when(repository.findById(eq(id))).thenReturn(Optional.of(inetto.withId(id)));
        final Inetto result = service.getInetto(id);
        Assert.assertEquals(inetto.withId(id), result);
        verify(repository, times(1)).findById(eq(id));

    }

    @Test
    public void testFindByUsername() {
        ObjectId id = ObjectId.get();
        when(repository.findByUsernameCached(eq(USERNAME))).thenReturn(Optional.of(inetto.withId(id)));
        final Inetto result = service.getInetto(USERNAME);
        Assert.assertEquals(inetto.withId(id), result);
        verify(repository, times(1)).findByUsernameCached(eq(USERNAME));
    }

    @Test
    public void testCreate() {
        ArgumentCaptor<Inetto> captor = ArgumentCaptor.forClass(Inetto.class);
        ArgumentCaptor<CharSequence> charSequenceArgumentCaptor = ArgumentCaptor.forClass(CharSequence.class);

        when(repository.saveAndInvalidate(captor.capture())).thenReturn(inetto.withId(ObjectId.get()));
        when(passwordEncoder.encode(charSequenceArgumentCaptor.capture())).thenReturn(USERNAME);

        service.createInetto(inetto);
        verify(passwordEncoder, times(1)).encode(any());
        verify(repository, times(1)).saveAndInvalidate(any());
        Assert.assertNotNull(captor.getValue().getPassword());
        Assert.assertFalse(captor.getValue().getPassword().isEmpty());

        Assert.assertNotNull(charSequenceArgumentCaptor.getValue());
        Assert.assertEquals(8, charSequenceArgumentCaptor.getValue().length());

    }

    @Test
    public void testSearch() {
        when(repository.findInettiCached(any(Pageable.class))).thenReturn(Collections.singletonList(inetto.withId(ObjectId.get())));
        when(repository.getInettiCount()).thenReturn(1L);
        Set<Inetto> result = service.search("user");
        verify(repository, times(1)).findInettiCached(any(Pageable.class));
        Assert.assertEquals(1, result.size());
    }


}