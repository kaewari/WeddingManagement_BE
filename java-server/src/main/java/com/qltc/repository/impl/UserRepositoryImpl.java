package com.qltc.repository.impl;

import com.qltc.pojo.User;
import com.qltc.repository.UserRepository;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passEncoder;

    @Override
    public User getUserByName(String name) {
        try {
            Session s = this.factory.getObject().getCurrentSession();
            Query q = s.createQuery("FROM User WHERE name=:n");
            q.setParameter("n", name);
            return (User) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            Session s = this.factory.getObject().getCurrentSession();
            Query q = s.createQuery("FROM User WHERE id=:id");
            q.setParameter("id", id);
            return (User) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public boolean deleteUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            User p = this.getUserById(id);
            s.delete(p);
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    @Override
    public List<User> getUsers() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("From User");
        return q.getResultList();
    }

    @Override
    public boolean updateUser(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            s.update(u);
            return true;
        } catch (HibernateException ex) {
            return false;
        }
    }

    @Override
    public boolean authUser(String name, String password) {
        try {
            User u = this.getUserByName(name);
            return this.passEncoder.matches(password, u.getPassword());
        } catch (NoResultException nre) {
            return false;
        }
    }

    @Override
    public User addUser(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.save(u);

        return u;
    }

    @Override
    public boolean findUserInfo(Object key, Object value) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("SELECT 1 FROM User WHERE " + key + "=:value");
        q.setParameter("value", value);
        try {
            q.getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    @Override
    public List<String> getPermissionsById(int userId) {
        Session session = factory.getObject().getCurrentSession();
        String queryString = "SELECT distinct(p.value) FROM permissions p WHERE p.id IN"
        + "(SELECT ugp.permissionId FROM user_in_group uig JOIN user_group_permission ugp ON uig.groupId = ugp.groupId WHERE uig.userId = :userId AND ugp.allows = 1)"
        + "OR p.id IN"
        + "(SELECT up.permissionId FROM user_permission up WHERE up.userId = :userId AND up.allows = 1)";
        Query query = session.createNativeQuery(queryString);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    
}
