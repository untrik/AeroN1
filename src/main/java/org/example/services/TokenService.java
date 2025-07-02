package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
public class TokenService {
    private record Token(String token, Instant expiresAt) {}
    private final WebClient authClient;
    private final String clientId, clientSecret;
    private volatile Token current;
    private final ObjectMapper mapper = new ObjectMapper();

    public TokenService(WebClient authClient,
                        @Value("${opensky.client-id}") String clientId,
                        @Value("${opensky.client-secret}") String clientSecret) {
        this.authClient   = authClient;
        this.clientId     = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized String getToken() throws Exception {
        if (current == null || Instant.now().isAfter(current.expiresAt())) {
            var form = new LinkedMultiValueMap<String,String>();
            form.add("grant_type","client_credentials");
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);

            String body = authClient.post()
                    .bodyValue(form)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = mapper.readTree(body);
            String  token     = node.get("access_token").asText();
            long    expiresIn = node.get("expires_in").asLong();
            current = new Token(token, Instant.now().plusSeconds(expiresIn - 30));
        }
        return current.token();
    }
}
