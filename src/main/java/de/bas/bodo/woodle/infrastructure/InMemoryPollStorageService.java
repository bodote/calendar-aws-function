package de.bas.bodo.woodle.infrastructure;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import de.bas.bodo.woodle.service.PollStorageService;

/**
 * In-memory implementation of PollStorageService.
 * This serves as a fallback when S3 is not available.
 */
@Service
@ConditionalOnMissingBean(type = "de.bas.bodo.woodle.infrastructure.S3PollStorageService")
public class InMemoryPollStorageService implements PollStorageService {

    private final Map<String, Map<String, String>> pollDataStorage = new ConcurrentHashMap<>();

    @Override
    public String storePollData(Map<String, String> pollData) {
        String uuid = UUID.randomUUID().toString();
        pollDataStorage.put(uuid, Map.copyOf(pollData));
        return uuid;
    }

    @Override
    public Map<String, String> retrievePollData(String uuid) {
        return pollDataStorage.get(uuid);
    }

    @Override
    public void updatePollData(String uuid, Map<String, String> pollData) {
        pollDataStorage.put(uuid, Map.copyOf(pollData));
    }
}