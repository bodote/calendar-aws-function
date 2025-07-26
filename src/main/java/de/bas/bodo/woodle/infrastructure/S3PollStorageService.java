package de.bas.bodo.woodle.infrastructure;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bas.bodo.woodle.service.PollStorageService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3 implementation of PollStorageService.
 * This is an adapter in the hexagonal architecture.
 */
@Service
@Primary
public class S3PollStorageService implements PollStorageService {

    private static final String POLLS_PREFIX = "polls/";
    private static final String JSON_SUFFIX = ".json";

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;
    private final String bucketName;

    public S3PollStorageService(
            @Value("${aws.s3.endpoint}") String endpoint,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.force-path-style}") boolean forcePathStyle) {
        
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .forcePathStyle(forcePathStyle)
                .build();
        this.objectMapper = new ObjectMapper();
        
        // Ensure bucket exists
        ensureBucketExists();
    }
    
    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
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
                    .bucket(bucketName)
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
                    .bucket(bucketName)
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

    @Override
    public void updatePollData(String uuid, Map<String, String> pollData) {
        try {
            String key = POLLS_PREFIX + uuid + JSON_SUFFIX;

            // Convert poll data to JSON
            String jsonContent = objectMapper.writeValueAsString(pollData);

            // Update in S3 (overwrites existing object with same key)
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(jsonContent));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update poll data", e);
        }
    }
}