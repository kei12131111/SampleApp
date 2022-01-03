package com.example.todo.domain.service.todo;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.domain.model.Client;
import com.example.todo.domain.repository.ClientRepository;


/**
 * 、認可処理で必要となるクライアント詳細情報をデータストアから取得するためのインタフェースであるClientDetailsServiceの実装クラス
 * 
 */
@Service("clientDetailsService")
@Transactional
public class OAuthClientDetailsService implements ClientDetailsService {

    @Inject
    ClientRepository clientRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId)
            throws ClientRegistrationException {

        Client client = clientRepository.findClientByClientId(clientId);

        if (client == null) {
              throw new NoSuchClientException("No client with requested id: " + clientId);
        }

        Set<String> clientScopes = clientRepository.findClientScopesByClientId(clientId);
        Set<String> clientResources = clientRepository.findClientResourcesByClientId(clientId);
        Set<String> clientGrants = clientRepository.findClientGrantsByClientId(clientId);
        Set<String> clientRedirectUris = clientRepository.findClientRedirectUrisByClientId(clientId);

        
        OAuthClientDetails clientDetails = new OAuthClientDetails();
        clientDetails.setClientId(client.getClientId());
        clientDetails.setClientSecret(client.getClientSecret());
        clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
        clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
        clientDetails.setResourceIds(clientResources);
        clientDetails.setScope(clientScopes);
        clientDetails.setAuthorizedGrantTypes(clientGrants);
        clientDetails.setRegisteredRedirectUri(clientRedirectUris);
        clientDetails.setClient(client);

        return clientDetails;
    }

}