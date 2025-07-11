package org.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.net.InetSocketAddress;
import java.net.Proxy;

@RestController
@RequestMapping("/api/aviationstack")
@CrossOrigin(origins = "http://localhost:3000")
public class AviationStackProxyController {
    private static final String API_KEY = "e4a0ef61b8e93d598668e0b0129d683d";
    private static final String BASE_URL = "http://api.aviationstack.com/v1/";
    private static final String[] ALLOWED_TYPES = {
            "flights", "routes", "airports", "airlines", "airplanes", "aircraft_types", "taxes", "cities", "countries"
    };

    @GetMapping("/{type}")
    public ResponseEntity<String> proxyAviationStack(@PathVariable String type) {
        boolean allowed = false;
        for (String t : ALLOWED_TYPES) {
            if (t.equals(type)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return ResponseEntity.badRequest().body("Invalid API type");
        }
        String url = BASE_URL + type + "?access_key=" + API_KEY + "&limit=30";

        // SOCKS5 proxy 127.0.0.1:2080 (nekoray)
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 2080));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(proxy);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // Логируем ответ для отладки
        System.out.println("Aviationstack status: " + response.getStatusCode());
        System.out.println("Aviationstack body: " + response.getBody());
        if (response.getBody() != null && response.getBody().trim().startsWith("{")) {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } else {
            return ResponseEntity.status(502).body(
                    "Ошибка: aviationstack вернул не JSON. Возможно, превышен лимит, неверный ключ или Cloudflare block.");
        }
    }
}