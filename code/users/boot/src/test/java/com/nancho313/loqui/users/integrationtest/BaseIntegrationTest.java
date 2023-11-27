package com.nancho313.loqui.users.integrationtest;

import com.nancho313.loqui.users.UsersApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ImportTestcontainers(TestContainerConfiguration.class)
@ContextConfiguration(
        initializers = {TestContainerConfiguration.KafkaServerInitializer.class},
        classes = UsersApplication.class)
public abstract class BaseIntegrationTest {
}
