package com.example.barmanager.backend.repositories;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Update Utility Class which helps to update a target object with
 * new fields from a source object only if they are not null.
 * Credit to https://developpaper.com/solution-to-update-field-to-null-by-jpa-save-method/
 */
public class UpdateUtil {
    public static void copyNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullField(source));
    }

    private static String[] getNullField(Object target) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(target);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> notNullFieldSet = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                Object value = beanWrapper.getPropertyValue(name);
                if (Objects.isNull(value)) {
                    notNullFieldSet.add(name);
                }
            }
        }
        String[] notNullField = new String[notNullFieldSet.size()];
        return notNullFieldSet.toArray(notNullField);
    }
}
