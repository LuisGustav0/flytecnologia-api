package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactiveImpl;
import com.flytecnologia.core.search.FlyFilter;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.notNull;

public interface FlyAutocompleteRepository<T extends FlyEntity, F extends FlyFilter> extends
        FlySearchRepository<T, F>,
        FlyResultListRepository<T> {

    default Optional<Map> getItemAutocomplete(F filter) {
        if (isEmpty(filter.getId())) {
            return Optional.empty();
        }

        FlyACHidden.validateFiltersRequiredToAutocomplete(filter);

        final String entityName = getEntityName();
        final String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        final StringBuilder hql = new StringBuilder()
                .append("select distinct new Map(\n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" as ").append(filter.getAcFieldValue());

        FlyACHidden.addFieldDescriptionToListAutocomplete(filter, alias, hql);

        FlyACHidden.addFieldIdToAutocomplete(filter, alias, hql);

        FlyACHidden.addExtraFieldsToAutocomplete(filter, alias, hql);

        final Map<String, Object> parameters = new HashMap<>();
        final StringBuilder hqlJoin = new StringBuilder();

        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ").append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where \n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" = :id\n ");

        filter.setAutoComplete(true);

        parameters.put("id", filter.getId());

        //If it is necessary to load the record, it does not matter whether it is inactive or not
        filter.setIgnoreInactiveFilter(true);

        changeSearchWhere(hql, parameters, filter);

        final TypedQuery<Map> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(1);

        parameters.forEach(query::setParameter);

        final Map<String, Object> map = query.getResultList().stream().filter(Objects::nonNull).findFirst().orElse(null);

        if (map == null) {
            return Optional.empty();
        }

        FlyACHidden.formatMapItemAutocomplete(alias, map);

        return Optional.of(map);
    }

    default Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter) {
        if (isEmpty(filter.getAcValue()))
            return Optional.empty();

        FlyACHidden.validateFiltersRequiredToAutocomplete(filter);

        final String entityName = getEntityName();
        final String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        final StringBuilder hql = new StringBuilder()
                .append("select distinct new Map(\n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" as ").append(filter.getAcFieldValue()).append("\n ");

        FlyACHidden.addFieldDescriptionToListAutocomplete(filter, alias, hql);

        FlyACHidden.addFieldIdToAutocomplete(filter, alias, hql);

        FlyACHidden.addExtraFieldsToAutocomplete(filter, alias, hql);

        final StringBuilder hqlJoin = new StringBuilder();
        final Map<String, Object> parameters = new HashMap<>();
        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ")
                .append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where (\n ");

        FlyACHidden.addFieldDescriptionToWhereAutocomplete(filter, alias, hql);

        hql.append(" OR CONCAT(").append(alias).append(".").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        final String fieldInactive = alias + ".inactive";

        if (getEntityClass().getGenericSuperclass().equals(FlyEntityWithInactiveImpl.class)) {
            hql.append(" and ").append(fieldInactive).append(" is false \n");
        }

        filter.setAutoComplete(true);

        parameters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        parameters.put("valueId", filter.getAcValue());

        changeSearchWhere(hql, parameters, filter);

        return getResultListMap(hql, parameters, filter.getAcLimit());
    }

    class FlyACHidden {
        private static <F extends FlyFilter> void addExtraFieldsToAutocomplete(F filter,
                                                                               String alias,
                                                                               StringBuilder hql) {
            if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
                String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

                for (String field : extraField) {
                    hql.append(",");

                    addExtraFieldsToAutocomplete(field, alias, hql);
                }
            }
        }

        private static void addExtraFieldsToAutocomplete(String field, String alias, StringBuilder hql) {
            if (!field.contains(".")) {
                hql.append(alias)
                        .append(".")
                        .append(field.trim())
                        .append(" as ").append(field.trim()).append(" \n ");
            } else {
                hql.append(field.trim()).append(" as ").append(field.trim().replace(".", "$")).append(" \n ");
            }
        }

        private static <F extends FlyFilter> void addFieldDescriptionToListAutocomplete(F filter,
                                                                                        String alias,
                                                                                        StringBuilder hql) {
            if (isEmpty(filter.getAcFieldsListAutocomplete())) {
                if (!filter.getAcFieldValue().equals(filter.getAcFieldDescription())) {
                    hql.append(",")
                            .append(alias)
                            .append(".")
                            .append(FlyACHidden.getFormatedField(filter.getAcFieldDescription()))
                            .append(" as ")
                            .append(filter.getAcFieldDescription())
                            .append(" \n ");
                }
            } else {
                String[] extraField = filter.getAcFieldsListAutocomplete().split(",");

                hql.append(",CONCAT(");

                int count = 0;

                for (String field : extraField) {
                    if (!field.contains("."))
                        hql.append(alias).append(".");

                    hql.append(field.trim());

                    if (count < extraField.length - 1) {
                        hql.append(", ' - ', ");
                    }

                    count++;
                }

                hql.append(") as ").append(filter.getAcFieldDescription()).append(" \n ");
            }
        }

        private static void formatMapItemAutocomplete(String alias, Map<String, Object> map) {
            final Set<String> keys = map.keySet();

            final Iterator<String> it = keys.iterator();

            final Map<String, Object> mapAux = new HashMap<>();

            while (it.hasNext()) {
                String key = it.next();

                if (key.contains("$")) {
                    Object value = map.get(key);
                    //map.remove(key);

                    String[] children = key.split("\\$");

                    if (children[0].equals(alias)) {
                        children = ArrayUtils.removeElement(children, children[0]);
                    }

                    for (int x = 0; x < children.length; x++) {
                        String child = children[x];

                        if (x < children.length - 1) {
                            if (x == 0) {
                                mapAux.put(child, new HashMap<>());
                            } else {
                                ((Map) mapAux.get(children[x - 1])).put(child, new HashMap<>());
                            }
                        } else {
                            ((Map) mapAux.get(children[x - 1])).put(child, value);
                        }
                    }

                }
            }

            map.putAll(mapAux);
        }

        private static <F extends FlyFilter> void validateFiltersRequiredToAutocomplete(F filter) {
            notNull(filter.getAcFieldValue(), "fieldValue is required");
            notNull(filter.getAcFieldDescription(), "fieldDescription is required");
        }


        private static <F extends FlyFilter> void addFieldIdToAutocomplete(F filter,
                                                                           String alias,
                                                                           StringBuilder hql) {
            if (!"id".equals(filter.getAcFieldValue())) {
                hql.append(",").append(alias).append(".id \n ");
            }
        }

        private static String getFormatedField(@NonNull String field) {
            return field.replace("__", ".");
        }

        private static void addLikeToFieldDescription(StringBuilder hql, String alias, String field) {
            field = getFormatedField(field);

            hql.append("   fly_to_ascii(lower(cast(");

            if (!field.contains("."))
                hql.append(alias).append(".");

            hql.append(field.trim())
                    .append(" as string))) like fly_to_ascii(cast(:value as string)) \n ");
        }

        private static void addFieldDescriptionToWhereAutocomplete(FlyFilter filter,
                                                                   String alias,
                                                                   StringBuilder hql) {
            if (filter.getAcFieldsListAutocomplete() == null ||
                    filter.getAcFieldsListAutocomplete().trim().length() == 0) {
                FlyACHidden.addLikeToFieldDescription(hql, alias, filter.getAcFieldDescription());
            } else {
                String[] extraField = filter.getAcFieldsListAutocomplete().split(",");

                int count = 0;

                hql.append("(");

                for (String field : extraField) {
                    if (count > 0) {
                        hql.append(" OR ");
                    }

                    FlyACHidden.addLikeToFieldDescription(hql, alias, field);

                    count++;
                }

                hql.append(")");
            }
        }
    }
}
