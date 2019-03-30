package com.flytecnologia.core.flyway;

import com.flytecnologia.core.user.FlyUserService;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Service
public class FlyDatabaseMigrationServiceImpl implements FlyDatabaseMigrationService {
    private FlyUserService flyUserService;
    private DataSource dataSource;

    public FlyDatabaseMigrationServiceImpl(DataSource dataSource,
                                           FlyUserService flyUserService) {
        this.dataSource = dataSource;
        this.flyUserService = flyUserService;
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

        if (schemas != null && schemas.size() > 0) {
            schemas.forEach(this::migrateSpecificSchema);
        }
    }

    /**
     * Used when creating a new schema for a new client
     */
    public void migrateSpecificSchema(String schema) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration/common", "db/migration/specific", "db/migration/" + schema)
                .schemas(schema)
                .load();

        flyway.migrate();
    }
}
