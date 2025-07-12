package de.bas.bodo.woodle.service;

import java.util.Map;

/**
 * Service interface for poll data storage operations.
 * This is a port in the hexagonal architecture.
 */
public interface PollStorageService {

    /**
     * Store poll data with a generated UUID.
     * 
     * @param pollData the poll data to store
     * @return the UUID used as the storage key
     */
    String storePollData(Map<String, String> pollData);

    /**
     * Retrieve poll data by UUID.
     * 
     * @param uuid the UUID of the poll data to retrieve
     * @return the poll data if found, null otherwise
     */
    Map<String, String> retrievePollData(String uuid);
}