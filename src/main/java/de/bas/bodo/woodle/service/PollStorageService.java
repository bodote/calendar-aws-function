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

    /**
     * Update existing poll data with the same UUID.
     * 
     * @param uuid     the existing UUID of the poll data to update
     * @param pollData the updated poll data
     */
    void updatePollData(String uuid, Map<String, String> pollData);
}