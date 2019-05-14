package com.flytecnologia.core.base.repository.plus;

import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;

import java.sql.SQLException;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyHibernateExecuteNativeQueryWithoutTransactionRepository extends FlyHibernateSessionRepository {
    default void executeNativeQueryWithoutTransaction(@NonNull String sql) throws SQLException {
        executeNativeQueryWithoutTransaction(sql, null);
    }

    default void executeNativeQueryWithoutTransaction(@NonNull String sql, String tenant) throws SQLException {
        if (isEmpty(tenant))
            tenant = "public";

        Session session = getNewSession(tenant);
        org.hibernate.internal.SessionImpl sessionImpl = (SessionImpl) session;
        java.sql.Connection connection = sessionImpl.connection();
        connection.prepareStatement(sql).execute();
    }
}
