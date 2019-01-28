package com.flytecnologia.core.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FlyEnumToMap {

    public static List<Map<String, Object>> parse(Class<? extends Enum> enumClass) {
        final EnumSet enumMap = EnumSet.allOf(enumClass);
        final List<Map<String, Object>> data = new ArrayList<>();
        final Object[] enums = enumMap.toArray();
        final List<String> props = getPropertiesDescriptor(PropertyUtils.getPropertyDescriptors(enumClass));

        for(Object item : enums) {
            final Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("property", item.toString());

            for(String prop : props) {
                if ("declaringClass".equals(prop))
                    continue;

                try {
                    itemMap.put(prop, BeanUtils.getProperty(item, prop));
                }catch (Exception ex){
                    //continue;
                }
            }

            data.add(itemMap);
        }

        return data;
    }

    private static List<String> getPropertiesDescriptor(PropertyDescriptor[] descriptors) {
        final List<String> listaDeNomes = new ArrayList<>();

        for (PropertyDescriptor descriptor : descriptors) {
            if (!descriptor.getName().equals("class")) {
                listaDeNomes.add(descriptor.getName());
            }
        }
        return listaDeNomes;
    }

    public static void main(String[] args) {
       /* Map<String, EnumSet<?>> enumMap = new HashMap<String, EnumSet<?>>();
        enumMap.put("estado",EnumSet.allOf(EnumEstado.class) );*/

        //parse(EnumTipoUsuario.class);
    }

}
