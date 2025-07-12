package de.bas.bodo.woodle.infrastructure;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bas.bodo.woodle.service.PollStorageService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3 implementation of PollStorageService.
 * This is an adapter in the hexagonal architecture.
 */
@Service
@Primary
public class S3PollStorageService implements PollStorageService {

    private static final String BUCKET_NAME = "de.bas.bodo";
    private static final String POLLS_PREFIX = "polls/";
    private static final String JSON_SUFFIX = ".json";

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public S3PollStorageService() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:9000"))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create("minioadmin", "minioadmin")))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String storePollData(Map<String, String> pollData) {
        try {
            String uuid = UUID.randomUUID().toString();
            String key = POLLS_PREFIX + uuid + JSON_SUFFIX;

            // Convert poll data to JSON
            String jsonContent = objectMapper.writeValueAsString(pollData);

            // Store in S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(jsonContent));

            return uuid;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store poll data", e);
        }
    }

    @Override
    public Map<String, String> retrievePollData(String uuid) {
        try {
            String key = POLLS_PREFIX + uuid + JSON_SUFFIX;

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            String jsonContent = s3Client.getObjectAsBytes(getRequest).asUtf8String();

            // Parse JSON back to Map
            @SuppressWarnings("unchecked")
            Map<String, String> pollData = objectMapper.readValue(jsonContent, Map.class);

            return pollData;
        } catch (NoSuchKeyException e) {
            return null; // Poll data not found
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve poll data", e);
        }
    }
}