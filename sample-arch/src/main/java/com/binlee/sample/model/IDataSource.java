package com.binlee.sample.model;

import java.util.List;

/**
 * Created on 21-2-26.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IDataSource {

    <T> T query(String key, Class<T> clazz);

    <T> List<T> queryAll(Class<T> clazz);

    <T> void update(T obj);

    <T> void delete(T obj);
}
