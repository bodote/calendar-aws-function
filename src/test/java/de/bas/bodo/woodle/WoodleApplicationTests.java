package de.bas.bodo.woodle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest
@Testcontainers
class WoodleApplicationTests {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
            .withServices(S3);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.s3.endpoint", localstack::getEndpoint);
        registry.add("aws.s3.region", localstack::getRegion);
        registry.add("aws.s3.access-key", localstack::getAccessKey);
        registry.add("aws.s3.secret-key", localstack::getSecretKey);
        registry.add("aws.s3.force-path-style", () -> "true");
        registry.add("aws.s3.bucket-name", () -> "test-bucket");
    }

    @Test
    void contextLoads() {
    }

}