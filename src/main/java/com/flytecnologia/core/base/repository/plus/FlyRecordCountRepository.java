package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;

import javax.persistence.TypedQuery;
import java.util.Objects;
import java.util.Optional;

public interface FlyRecordCountRepository<T extends FlyEntity> extends
        FlyEntityManagerRepository,
        FlyEntityClassService<T> {

    default Optional<Long> getRecordListCount(Long id, String innerEntityName) {
        final String entityName = getEntityName();

        StringBuilder hql = new StringBuilder()
                .append("select count(entities.id)  \n")
                .append("from\n ")
                .append(entityName)
                .append(" super  \n")
                .append("inner join super.").append(innerEntityName).append(" as entities \n")
                .append("where super.id = :id\n");

        final TypedQuery<Long> query = getEntityManager().createQuery(hql.toString(), Long.class);
        query.setParameter("id", id);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }
}
