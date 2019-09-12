package com.flytecnologia.core.hibernate.multitenancy;

import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.DEFAULT_TENANT_ID;

@Component
@AllArgsConstructor
public class FlyMultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    public static final String SET_SCHEMA = "SET SEARCH_PATH TO ";

    private DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();

        try {
            String schema  = DEFAULT_TENANT_ID;

            if (tenantIdentifier != null) {
                schema  = tenantIdentifier;
            }

            String sql = SET_SCHEMA + schema ;

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }

        } catch (SQLException e) {
            throw new HibernateException(
                    "Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
                    e
            );
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        String sql = SET_SCHEMA + DEFAULT_TENANT_ID;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new HibernateException(
                    "Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
                    e
            );
        }

        connection.close();
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}