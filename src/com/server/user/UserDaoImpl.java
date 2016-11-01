package com.server.user;


import com.server.base.BaseDao;
import com.server.util.DataWrapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
@Repository
public class UserDaoImpl extends BaseDao<UserEntity> implements UserDao {

    @Override
    public boolean register(UserEntity user) {
        return save(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return delete(get(id));
    }

    @Override
    public boolean updateUser(UserEntity user) {
        return update(user);
    }

    @Override
    public UserEntity getUserByUsername(String username){
        Session session = getSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.add(Restrictions.eq("username", username));
        List<UserEntity> result = criteria.list();
        if (result != null && result.size() > 0) {
            return (UserEntity) result.get(0);
        }
        return null;
    }

    @Override
    public boolean updateUserPassword(Long userId, String password) {

        String sql = "update c_user u set u.password="+password+" where u.id="+userId;
        Session session = getSession();
        Query query = session.createSQLQuery(sql);
        try {
            query.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public DataWrapper<List<UserEntity>> getUserList() {
        DataWrapper<List<UserEntity>> retDataWrapper = new DataWrapper<List<UserEntity>>();
        List<UserEntity> ret = new ArrayList<UserEntity>();
        Session session = getSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
//        criteria.addOrder(Order.desc("publishDate"));
        try {
            ret = criteria.list();
        }catch (Exception e){
            e.printStackTrace();
        }
        retDataWrapper.setData(ret);
        return retDataWrapper;
    }
}
