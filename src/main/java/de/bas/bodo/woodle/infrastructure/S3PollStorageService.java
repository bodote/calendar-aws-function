package de.bas.bodo.woodle.infrastructure;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bas.bodo.woodle.service.PollStorageService;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
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
@Profile("!e2e")
@Primary
@Slf4j
public class S3PollStorageService implements PollStorageService {

    private static final String POLLS_PREFIX = "polls/";
    private static final String JSON_SUFFIX = ".json";

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;
    private final String bucketName;
    private final String s3Region;

    public S3PollStorageService(
            @Value("${aws.s3.endpoint}") String endpoint,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.force-path-style}") boolean forcePathStyle) {

        // DEBUG: Override with environment variables directly if available
        String envEndpoint = System.getenv("AWS_S3_ENDPOINT");
        String envRegion = System.getenv("AWS_S3_REGION");
        String envAccessKey = System.getenv("AWS_S3_ACCESS_KEY");
        String envSecretKey = System.getenv("AWS_S3_SECRET_KEY");
        String envBucketName = System.getenv("AWS_S3_BUCKET_NAME");
        String envForcePathStyle = System.getenv("AWS_S3_FORCE_PATH_STYLE");

        if (envEndpoint != null)
            endpoint = envEndpoint;
        if (envRegion != null)
            region = envRegion;
        if (envAccessKey != null)
            accessKey = envAccessKey;
        if (envSecretKey != null)
            secretKey = envSecretKey;
        if (envBucketName != null)
            bucketName = envBucketName;
        if (envForcePathStyle != null)
            forcePathStyle = Boolean.parseBoolean(envForcePathStyle);

        // Force no endpoint override in Lambda to avoid invalid empty override
        String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        if ("lambda".equalsIgnoreCase(activeProfile)) {
            endpoint = null;
        }

        log.info("=== S3 Config DEBUG ===");
        log.info("@Value endpoint: {}", endpoint);
        log.info("@Value region: {}", region);
        log.info("Environment AWS_S3_ENDPOINT: {}", System.getenv("AWS_S3_ENDPOINT"));
        log.info("Environment AWS_S3_REGION: {}", System.getenv("AWS_S3_REGION"));
        log.info("Final endpoint used: {}", endpoint);
        log.info("Final region used: {}", region);
        log.info("AWS access key provided: {}", accessKey != null && !accessKey.isBlank());
        log.info("AWS secret key provided: {}", secretKey != null && !secretKey.isBlank());
        log.info("Bucket name: {}", bucketName);
        log.info("Force path style: {}", forcePathStyle);
        log.info("=======================");

        this.bucketName = bucketName;
        this.s3Region = region;
        // Build S3 client with sensible defaults: only set endpoint override when
        // non-empty
        S3ClientBuilder s3Builder = S3Client.builder()
                .region(Region.of(region))
                .forcePathStyle(forcePathStyle);

        // Always use AWS default endpoint; do not call endpointOverride to avoid
        // invalid values in Lambda
        if (endpoint != null && !endpoint.isBlank()) {
            log.info("Ignoring custom S3 endpoint '{}' in Lambda deployment; using AWS default for {}", endpoint,
                    region);
        }
        log.info("Using AWS default S3 endpoint for region {}", region);

        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            s3Builder = s3Builder.credentialsProvider(StaticCredentialsProvider
                    .create(AwsBasicCredentials.create(accessKey, secretKey)));
            log.info("Using static credentials provider for S3 client (access key provided)");
        } else {
            // Use default provider (IAM role) when explicit keys are not provided
            s3Builder = s3Builder.credentialsProvider(DefaultCredentialsProvider.create());
            log.info("Using DefaultCredentialsProvider for S3 client (IAM role or environment credentials)");
        }

        try {
            this.s3Client = s3Builder.build();
            log.info("S3 client built successfully");
        } catch (Exception e) {
            log.error("Failed to build S3 client (endpoint='{}', region='{}'): {}", endpoint, region, e.toString(), e);
            throw new RuntimeException("S3 client initialization failed", e);
        }
        this.objectMapper = new ObjectMapper();

        // Skip bucket existence check in Lambda (IAM may not allow create/list)
        if ("lambda".equalsIgnoreCase(activeProfile)) {
            log.info("Skipping S3 bucket existence check in 'lambda' profile");
        } else if (bucketName == null || bucketName.isBlank()) {
            log.warn("No S3 bucket name configured; skipping bucket existence check");
        } else {
            try {
                ensureBucketExists();
            } catch (Exception e) {
                log.warn("Bucket existence check failed (bucket='{}'): {}", bucketName, e.getMessage());
            }
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            CreateBucketRequest.Builder create = CreateBucketRequest.builder().bucket(bucketName);
            // In regions other than us-east-1, S3 requires a LocationConstraint
            if (!"us-east-1".equalsIgnoreCase(s3Region)) {
                create = create.createBucketConfiguration(
                        CreateBucketConfiguration.builder().locationConstraint(s3Region).build());
            }
            s3Client.createBucket(create.build());
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