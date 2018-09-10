package com.z_s.dao.generic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * @author  zsooruth
 * @created Jun 6, 2014
 * @since   Jun 6, 2014
 *
 * @param <T>
 */
public interface IGenericDAO<T> extends ISimpleDAO{
    
    /**
     * Method that returns the number of entries from a table that meet some
     * criteria (where clause params)
     *
     * @param params sql parameters
     * @return the number of records meeting the criteria
     */
    long countAll(Map params);

    T create(T t);
    
    T createWithNewTransaction(T t);

    /**
     * hibernate query.list() method is returning empty list NOT a null value
     */
    List<T> find(final Map params, int start, int count);

    /**
     * hibernate query.list() method is returning empty list NOT a null value
     */
    List<T> findAll();

    void delete(Serializable id);
    
    void delete(final T t);

    void deleteAll();

    T find(Serializable id);

    T get(final Serializable id);
    T getWithoutFilters(Serializable id);

    T update(T t);

    T updateWithNewTransaction(T t);

    T update(T t, boolean updateTimestamp);

    void refresh(final T t);

    int supprime(Serializable id);

    int supprime(Collection<? extends Serializable> ids);

}
