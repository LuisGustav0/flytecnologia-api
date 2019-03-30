package com.flytecnologia.core.flyway;

public interface FlyDatabaseMigrationService {
    void migrateSpecificSchema(String schema);
}
