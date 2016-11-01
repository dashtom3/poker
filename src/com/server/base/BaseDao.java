package com.server.base;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by tia on 16/5/28.
 */
@Transactional
public class BaseDao<T>{

    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> entityClass;
    /**
     * 通过反射获取子类确定的泛型类
     */
    public BaseDao() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    /**
     * 根据ID加载PO实例
     *
     * @param id
     * @return 返回相应的持久化PO实例
     */
    public T load(Serializable id) {
        Session session = getSession();
        session.beginTransaction();
        T entity = (T) session.load(entityClass, id);
        session.getTransaction().commit();
        return entity;
    }

    /**
     * 根据ID获取PO实例
     *
     * @param id
     * @return 返回相应的持久化PO实例
     */
    public T get(Serializable id) {
        //getSession();
        //session.beginTransaction();
        //T entity = (T) session.get(entityClass, id);
        //session.getTransaction().commit();
        //return entity;
        return (T)getSession().get(entityClass,id);
    }

    /**
     * @param
     * @return 返回所有相应的持久化实例
     */
    public List<T> getAll() {

        return find("from " + entityClass.getSimpleName());
    }

    /**
     * 保存PO
     *
     * @param entity
     */
    public boolean save(T entity) {
        Session session = getSession();
        try{
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            session.flush();
        }catch(Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();

            return false;
        }
        return true;
    }

    /**
     * 删除PO
     *
     * @param entity
     */
    public boolean delete(T entity) {
        Session session = getSession();
        try{
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
            session.flush();
        }catch(Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }
        return true;
    }

    /**
     * 删除PO
     *
     * @param hql
     */
    public void delete(String hql) {
        Session session = getSession();
        session.beginTransaction();
        session.delete(hql);
        session.getTransaction().commit();
    }

    /**
     * 更改PO
     *
     * @param entity
     */
    public boolean update(T entity) {
        Session session = getSession();
        try{
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
            session.flush();
        }catch(Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }
        return true;
    }

    /**
     * 执行HQL查询
     *
     * @param hql
     * @return 查询结果
     */
    public List find(String hql) {
        /*
        Session session;
        Transaction transaction = null;
        List resultList;
        session = getSession();
       // transaction = session.beginTransaction();
        resultList = session.createQuery(hql).list();
        session.getTransaction().commit();
        return resultList;
        */
        /*
        List<Object> result = (List<Object>)  sessionFactory.getCurrentSession().createQuery(hql).list();
        Iterator itr = result.iterator();
        while(itr.hasNext()){
            Object[] obj = (Object[]) itr.next();
            //now you have one array of Object for each row
            String client = String.valueOf(obj[0]); // don't know the type of column CLIENT assuming String
            Integer service = Integer.parseInt(String.valueOf(obj[1])); //SERVICE assumed as int
            //same way for all obj[2], obj[3], obj[4]
        }
*/

        return sessionFactory.getCurrentSession().createQuery(hql).list();
    }

    /**
     * 创建Query对象. 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
     * 留意可以连续设置,如下：
     * <pre>
     * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
     * </pre>
     * 调用方式如下：
     * <pre>
     *        dao.createQuery(hql)
     *        dao.createQuery(hql,arg0);
     *        dao.createQuery(hql,arg0,arg1);
     *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
     * </pre>
     *
     * @param values 可变参数.
     */
    public Query createQuery(String hql, Object... values) {
        Assert.hasText(hql);
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        return query;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
//        return sessionFactory.openSession();
    }
}
