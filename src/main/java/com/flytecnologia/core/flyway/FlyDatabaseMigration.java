package com.flytecnologia.core.flyway;

import com.flytecnologia.core.user.FlyUserService;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

@Service
public class FlyDatabaseMigration {
    private Flyway flyway;
    private DataSource dataSource;
    private EntityManager entityManager;
    private FlyUserService flyUserService;

    public FlyDatabaseMigration(Flyway flyway,
                                DataSource dataSource,
                                EntityManager entityManager,
                                FlyUserService flyUserService) {
        this.entityManager = entityManager;
        this.flyway = flyway;
        this.dataSource = dataSource;
        this.flyUserService = flyUserService;
        this.flyway.setDataSource(dataSource);
        this.flyway.setLocations("db/migration/common", "db/migration/specific");
    }

    @PostConstruct
    private void migrate() {
        migrateAllSpecificSchemas();
    }

    /**
     * Updates all schemas when application is starting
     */
    private void migrateAllSpecificSchemas() {
        List<String> schemas = flyUserService.listAllSchemas();

        for (String schema : schemas) {
            migrateSpecificSchema(schema);
        }
    }

    /**
     * Used when creating a new schema for a new client
     */
    public void migrateSpecificSchema(String schema) {
        flyway.setSchemas(schema);
        flyway.migrate();
    }
}
