package com.inetti.matchnight.service;

import com.inetti.matchnight.controller.exception.UserAlreadyExistException;
import com.inetti.matchnight.data.dto.InettoContract;
import com.inetti.matchnight.data.model.Inetto;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface InettoService<T extends InettoContract> extends UserDetailsService {

    /**
     * retrieve details about a user
     * @param inettoId the id of the user
     * @return a user
     */
    public T getInetto(ObjectId inettoId);

    /**
     * retrieve details about a user
     * @param username the username
     * @return a username if found or emtpy
     */
    public T getInetto(String username);

    /**
     * create an inetto, in case of violation
     * @param inettoContract the inetto to save
     * @throws UserAlreadyExistException if there is already a user with the same {@link Inetto#getUsername()}
     */
    public void createInetto(T inettoContract) throws UserAlreadyExistException;

    /**
     * search user having the username which contains the param
     * @param query the query to search for
     * @return a list of user
     */
    public Set<T> search(String query);
}
