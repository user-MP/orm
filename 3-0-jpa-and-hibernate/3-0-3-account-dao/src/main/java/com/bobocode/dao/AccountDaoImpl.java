package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;
import com.bobocode.util.ExerciseNotCompletedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import java.util.List;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        try{
            EntityManager em= emf.createEntityManager();
            EntityTransaction transactional= em.getTransaction();
            transactional.begin();

            em.persist(account);

            transactional.commit();
        }
        catch(Exception e){
            throw new AccountDaoException("Err",e);
        }



    }

    @Override
    public Account findById(Long id) {
        EntityManager em= emf.createEntityManager();
        EntityTransaction transactional= em.getTransaction();
        transactional.begin();

        Account  foundedAccount= em.createQuery("select a FROM Account a where a.id="+id,Account.class).getSingleResult();

        transactional.commit();

        return foundedAccount;
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager em= emf.createEntityManager();
        EntityTransaction transactional= em.getTransaction();
        transactional.begin();

        Account  foundedAccount= em.createQuery("select a from Account a where a.email= :emailParam",Account.class)
                .setParameter("emailParam",email) .getSingleResult();

        transactional.commit();

        return foundedAccount;
    }

    @Override
    public List<Account> findAll() {
        EntityManager em= emf.createEntityManager();
        EntityTransaction transactional= em.getTransaction();
        transactional.begin();

        List<Account>  result= em.createQuery("select a from Account a "  ).getResultList();

        transactional.commit();

        return result;
    }

    @Override
    public void update(Account account) {

     try{
         EntityManager em = emf.createEntityManager();
         EntityTransaction transactional = em.getTransaction();
         transactional.begin();

         em.merge(account);

         transactional.commit();
     }
     catch(Exception e){
         throw new AccountDaoException("Error",e);
     }

    }

    @Override
    public void remove(Account account) {

        EntityManager em= emf.createEntityManager();
        EntityTransaction transactional= em.getTransaction();
        transactional.begin();

        Account accountAfterMerge =   em.merge(account);
       em.remove(accountAfterMerge);
        transactional.commit();



    }
}

