package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data_base.AirDao;
import org.example.data_base.ConditionDao;
import org.example.models.VrsAir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OpenSkyFetchService {
    private static final Logger log = LoggerFactory.getLogger(OpenSkyFetchService.class);

    private final WebClient apiClient;
    private final TokenService tokenService;
    private final ObjectMapper mapper = new ObjectMapper();

    // Кэш текущих самолётов
    private final ConcurrentHashMap<Integer, VrsAir> cache = new ConcurrentHashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(1);

    public OpenSkyFetchService(WebClient apiClient,
                               TokenService tokenService) {
        this.apiClient    = apiClient;
        this.tokenService = tokenService;
    }

    /**
     * Каждые vrs.fetch-interval-ms миллисекунд обновляем кэш:
     * 1) Запрашиваем JSON у OpenSky
     * 2) Парсим и фильтруем по стране
     * 3) Перезаполняем cache
     */
    @Scheduled(fixedRateString = "${vrs.fetch-interval-ms}")
    public void refresh() {
        String json;
        try {
            String bearer = "Bearer " + tokenService.getToken();
            json = apiClient.get()
                    .uri("/states/all")
                    .header("Authorization", bearer)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Raw JSON length = {} bytes", json != null ? json.length() : 0);
        } catch (Exception e) {
            log.error("ERROR fetching raw JSON from OpenSky", e);
            return;
        }

        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (Exception e) {
            log.error("ERROR parsing JSON response", e);
            return;
        }

        cache.clear();
        idGen.set(1);

        long time = root.get("time").asLong();
        for (JsonNode s : root.path("states")) {
            String country = s.get(2).asText("");
            if (!"Russian Federation".equals(country)) {
                continue;
            }
            double lon = s.get(5).asDouble(Double.NaN);
            double lat = s.get(6).asDouble(Double.NaN);
            if (Double.isNaN(lon) || Double.isNaN(lat)) {
                log.warn("Skipping invalid coords: lon={}, lat={}", lon, lat);
                continue;
            }
            VrsAir ac = new VrsAir();
            ac.setId(idGen.getAndIncrement());
            ac.setIcao(s.get(0).asText(""));
            ac.setCallsign(s.get(1).asText(""));
            ac.setLatitude(lat);
            ac.setLongitude(lon);
            ac.setAltitude(s.get(7).asDouble(0.0));
            ac.setSpeed(s.get(9).asDouble(0.0));
            ac.setTrack(s.get(10).asDouble(0.0));

            cache.put(ac.getId(), ac);

            boolean isAirInDataBase = false;
            if (!AirDao.isThereAir(ac.getIcao())) {
                System.out.printf("Новый самолет! icao24 = %s, country = %s\n", ac.getIcao(), country);
                if (AirDao.addAir(ac.getIcao(), country)) {
                    System.out.printf("Добавлен самолет icao24 = %s, country = %s\n", ac.getIcao(), country);
                    isAirInDataBase = true;
                } else {
                    System.out.printf("Не удалось добавить самолет icao24 = %s, country = %s\n", ac.getIcao(), country);
                }
            } else {
                isAirInDataBase = true;
            }
            if (isAirInDataBase) {
                int timePosition = s.get(3).asInt();
                int lastContact = s.get(4).asInt();
                boolean onGround = s.get(8).asBoolean();
                double verticalRate = s.get(11).asDouble();
                double geoAltitude = s.get(13).asDouble();
                String squawk = s.get(14).asText();
                boolean spi = s.get(15).asBoolean();
                int positionSource = s.get(16).asInt();
                ConditionDao.addCondition(ac, time, timePosition, lastContact, onGround, verticalRate, geoAltitude,
                        squawk, spi, positionSource);
            }
        }

        log.info("Refresh complete, cache size={}", cache.size());
    }

    /**
     * Отдаём текущее содержимое кэша для контроллера
     */
    public Collection<VrsAir> getAllAircraft() {
        return cache.values();
    }
}