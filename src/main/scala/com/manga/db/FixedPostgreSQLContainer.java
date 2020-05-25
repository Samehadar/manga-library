package com.manga.db;

import org.testcontainers.containers.PostgreSQLContainer;

public class FixedPostgreSQLContainer extends PostgreSQLContainer {
    public FixedPostgreSQLContainer() {
        super();
    }
    public FixedPostgreSQLContainer configurePort(Integer port) {
        super.addFixedExposedPort(port, port);
        return this;
    }
}