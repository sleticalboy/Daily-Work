package com.sleticalboy.greendao.bean;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description Type Converter
 */
public class StudentTypeConverter implements PropertyConverter<StudentType, String> {

    @Override
    public StudentType convertToEntityProperty(String databaseValue) {
        return StudentType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(StudentType entityProperty) {
        return entityProperty.name();
    }
}
