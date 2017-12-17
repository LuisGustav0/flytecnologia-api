package com.flytecnologia.core.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

public class FlyPostgreSqlDialect extends PostgreSQL94Dialect {
    public FlyPostgreSqlDialect() {
        super();
        registerFunction( "fly_to_ascii", new SQLFunctionTemplate(StringType.INSTANCE, "fly_to_ascii(?1)"));
    }
}
