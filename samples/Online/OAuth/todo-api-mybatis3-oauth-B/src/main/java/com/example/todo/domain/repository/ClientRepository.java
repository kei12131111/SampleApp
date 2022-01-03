package com.example.todo.domain.repository;

import java.util.Set;

import com.example.todo.domain.model.Client;


public interface ClientRepository {

    Client findClientByClientId(String clientId);

    Set<String> findClientScopesByClientId(String clientId);

    Set<String> findClientResourcesByClientId(String clientId);

    Set<String> findClientGrantsByClientId(String clientId);

    Set<String> findClientRedirectUrisByClientId(String clientId);

}
