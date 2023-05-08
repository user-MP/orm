package com.bobocode.dao;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.ExerciseNotCompletedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {

        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {

//        throw new ExerciseNotCompletedException(); // todo
     EntityManager manager= entityManagerFactory.createEntityManager();

     EntityTransaction transaction=  manager.getTransaction();


        transaction.begin();

        manager.persist(photo);


        transaction.commit();
    }

    @Override
    public Photo findById(long id) {

        EntityManager manager= entityManagerFactory.createEntityManager();



        EntityTransaction transaction=  manager.getTransaction();


        transaction.begin();

        Photo result=manager.find(Photo.class,id);


        transaction.commit();

        return result;

//        throw new ExerciseNotCompletedException(); // todo


    }

    @Override
    public List<Photo> findAll() {
        EntityManager manager= entityManagerFactory.createEntityManager();

        Query query = manager.createQuery("from Photo");






        EntityTransaction transaction=  manager.getTransaction();


        transaction.begin();

       List<Photo> result = query.getResultList();


        transaction.commit();

        return result;
    }

    @Override
    public void remove(Photo photo) {

        EntityManager manager= entityManagerFactory.createEntityManager();



        EntityTransaction transaction=  manager.getTransaction();


        transaction.begin();


        manager.remove(manager.contains(photo) ? photo : manager.merge(photo));
        transaction.commit();



//        throw new ExerciseNotCompletedException(); // todo
    }

    @Override
    public void addComment(long photoId, String comment) {

        PhotoComment photoComment=new PhotoComment();



        EntityManager manager= entityManagerFactory.createEntityManager();



        EntityTransaction transaction=  manager.getTransaction();


        transaction.begin();

        Photo managedPhoto = manager.find(Photo.class, photoId);
        photoComment.setText(comment);
        photoComment.setPhoto(managedPhoto);
        managedPhoto.addComment(photoComment);
        manager.persist(managedPhoto);

        transaction.commit();



    }
}
