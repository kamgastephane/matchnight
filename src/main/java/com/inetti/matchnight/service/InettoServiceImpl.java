package com.inetti.matchnight.service;

import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.model.Inetto;
import com.inetti.matchnight.data.repository.InettoRepository;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This service describe the communication with the user registry
 */
@Service
public class InettoServiceImpl implements InettoService<Inetto> {

    private final InettoRepository inettoRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
//    private final Notifier notificationEngine;

    @Autowired
    public InettoServiceImpl(InettoRepository inettoRepository, PasswordEncoder encoder,
                             PasswordGenerator passwordGenerator) {
        this.inettoRepository = inettoRepository;
        this.passwordEncoder = encoder;
        this.passwordGenerator = passwordGenerator;

    }

    public Inetto getInetto(@NotNull ObjectId inettoId) {
        Objects.requireNonNull(inettoId);
        return inettoRepository.findById(inettoId).orElse(null);
    }

    public Inetto getInetto(@NotNull String username) {
        Objects.requireNonNull(username);
        return inettoRepository.findByUsernameCached(username).orElse(null);
    }


    public void createInetto(@NotNull Inetto inetto) {
        Objects.requireNonNull(inetto);
        String password = inetto.getPassword();
        if (password == null) {
            password = passwordGenerator.generatePassword(8, new CharacterRule(EnglishCharacterData.LowerCase, 4),
                     new CharacterRule(EnglishCharacterData.Digit, 4));
        }
        inettoRepository.saveAndInvalidate(inetto.withPassword(passwordEncoder.encode(password)));

    }

    @Override
    public Set<Inetto> search(String query) {
        //we start searching with pages of 1000 elements
        //we know the user base will hardly be as large as 1000 and all further request will go through the cache layer
        //this could be improved using caffeine in memory cache
        boolean found = false;
        int jump = 0;
        Set<Inetto> inetti = Collections.emptySet();
        while (!found) {
            final List<Inetto> inettiCached = inettoRepository.findInettiCached(OffsetLimitRequest.of((long) jump, 1000 + jump, Sort.unsorted()));
            inetti = inettiCached.parallelStream().filter(inetto -> inetto.getUsername().contains(query)).collect(Collectors.toSet());
            if (inetti.isEmpty()) {
                found = true;

            } else {
                jump += 1000;
            }
        }
        return inetti;

    }

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) {
        return inettoRepository.findByUsernameCached(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}