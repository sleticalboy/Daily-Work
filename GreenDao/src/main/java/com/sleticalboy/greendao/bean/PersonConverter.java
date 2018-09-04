package com.sleticalboy.greendao.bean;

import com.alibaba.fastjson.JSON;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created on 18-8-29.
 *
 * @author sleticalboy
 * @description
 */
public class PersonConverter implements PropertyConverter<Person, String> {

    @Override
    public Person convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue, Person.class);
    }

    @Override
    public String convertToDatabaseValue(Person entityProperty) {
        return JSON.toJSONString(entityProperty);
    }
}
