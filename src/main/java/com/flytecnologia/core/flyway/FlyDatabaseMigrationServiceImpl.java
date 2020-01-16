package com.flytecnologia.core.flyway;

import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.user.FlyUserService;
import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isNotEmpty;

@Service
@AllArgsConstructor
public class FlyDatabaseMigrationServiceImpl implements FlyDatabaseMigrationService {
    private FlyUserService userService;
    private DataSource dataSource;
    private FlyAppProperty appProperty;

    @PostConstruct
    private void migrate() {
        migrateAllSpecificSchemas();
    }

    /**
     * Updates all schemas when application is starting
     */
    private void migrateAllSpecificSchemas() {
        List<String> schemas;

        if (!isEmpty(appProperty.getApp().getStartSchemas())) {
            schemas = Arrays.asList(appProperty.getApp().getStartSchemas().split(","));
        } else {
            schemas = userService.listAllSchemas();
        }

        if (isNotEmpty(schemas)) {
            if (appProperty.getApp().isDebug()) {
                schemas.parallelStream().forEach(this::migrateSpecificSchema);
            } else {
                schemas.forEach(this::migrateSpecificSchema);
            }
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
