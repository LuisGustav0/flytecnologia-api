package com.flytecnologia.core.util;

import com.flytecnologia.core.model.FlyEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FlyReflection {
    public static void copyPropertiesIngoreNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        final PropertyDescriptor[] pds = src.getPropertyDescriptors();

        final Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            final Object srcValue = src.getPropertyValue(pd.getName());

            if (srcValue == null)
                emptyNames.add(pd.getName());
        }

        final String[] result = new String[emptyNames.size()];

        return emptyNames.toArray(result);
    }

    public static <T extends FlyEntity> void removeEmptyEntityFromEntity(T source, int level, int maxLevel) {
        if (level == 0)
            return;

        final BeanWrapper src = new BeanWrapperImpl(source);
        final PropertyDescriptor[] pds = src.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            Object obj = src.getPropertyValue(pd.getName());

            if (level <= maxLevel && obj instanceof List && isListOfFlyEntity((List) obj)) {
                level++;

                for (FlyEntity entity : (List<FlyEntity>) obj) {
                    removeEmptyEntityFromEntity(entity, level, maxLevel);
                }
            }

            if (obj instanceof FlyEntity) {
                if (((FlyEntity) obj).getId() == null) {
                    src.setPropertyValue(pd.getName(), null);
                }
            }
        }
    }

    public static <T extends FlyEntity> void setParentInTheChildrenList(T source) {
        setParentInTheChildrenList(source, source, true);
    }

    private static <T extends FlyEntity> void setParentInTheChildrenList(T source, T parent, boolean isFirstLevel) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        final PropertyDescriptor[] pds = src.getPropertyDescriptors();
        final String sourceName = parent.getClass().getSimpleName();

        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();

            Object obj = src.getPropertyValue(propertyName);

            if (obj instanceof List && isListOfFlyEntity((List) obj)) {
                for (FlyEntity entity : (List<FlyEntity>) obj) {
                    setParentInTheChildrenList(entity, source, false);
                }
            }

            if (isFirstLevel) {
                continue;
            }

            if (pd.getName().toLowerCase().equals(sourceName.toLowerCase())) {
                if (pd.getPropertyType().getSimpleName().equals(sourceName)) {
                    src.setPropertyValue(pd.getName(), parent);
                }
            }
        }
    }


    public static boolean isListOfFlyEntity(List<?> list) {
        if (list == null || list.size() == 0)
            return false;

        return list.get(0) instanceof FlyEntity;
    }

    public static List<String> getMethodNames(Object object) {
        return getMethodNames(object.getClass());
    }

    public static List<String> getMethodNames(Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final List<String> methodNames = new ArrayList<>();

        for (Method method : methods) {
            methodNames.add(method.getName());
        }
        return methodNames;
    }
}
