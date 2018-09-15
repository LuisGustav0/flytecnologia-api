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
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];

        return emptyNames.toArray(result);
    }

    public static <T extends FlyEntity> void removeEmpityEntityFromEntitiy(T source, int level, int maxLevel) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            Object obj = src.getPropertyValue(pd.getName());

            if (level <= maxLevel && obj instanceof List && isListOfFlyEntity((List) obj)) {
                level++;

                for (FlyEntity entity : (List<FlyEntity>) obj) {
                    removeEmpityEntityFromEntitiy(entity, level, maxLevel);
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
        setParentInTheChildrenList(source, true, source);
    }

    private static <T extends FlyEntity> void setParentInTheChildrenList(T source, boolean isFirstLevel, T parent) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        String sourceName = parent.getClass().getSimpleName();

        for (PropertyDescriptor pd : pds) {
            Object obj = src.getPropertyValue(pd.getName());

            if (obj instanceof List && isListOfFlyEntity((List) obj)) {
                for (FlyEntity entity : (List<FlyEntity>) obj) {
                    setParentInTheChildrenList(entity, false, parent);
                }
            }

            if(obj != null) {
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
        Method[] methods = clazz.getDeclaredMethods();

        List<String> methodNames = new ArrayList<>();
        for (Method method : methods) {
            methodNames.add(method.getName());
        }
        return methodNames;
    }
}
