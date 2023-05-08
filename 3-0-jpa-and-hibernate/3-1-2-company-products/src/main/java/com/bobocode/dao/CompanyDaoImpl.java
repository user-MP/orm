package com.bobocode.dao;

import com.bobocode.model.Company;
import com.bobocode.model.Product;
import com.bobocode.util.ExerciseNotCompletedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.Transactional;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {


        EntityManager em= entityManagerFactory.createEntityManager();

        EntityTransaction transactional = em.getTransaction();

        transactional.begin();


      Company res=  em.createQuery("select c from Company c join fetch c.products where c.id="+id, Company.class)
              .getSingleResult();


        transactional.commit();




        return res;
    }
}
