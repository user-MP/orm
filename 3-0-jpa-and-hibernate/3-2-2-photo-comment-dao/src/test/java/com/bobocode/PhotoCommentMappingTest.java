package com.bobocode;

import static com.bobocode.util.PhotoTestDataGenerator.createListOfRandomComments;
import static com.bobocode.util.PhotoTestDataGenerator.createRandomPhoto;
import static com.bobocode.util.PhotoTestDataGenerator.createRandomPhotoComment;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.EntityManagerUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PhotoCommentMappingTest {

    private static EntityManagerUtil emUtil;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("PhotoComments");
        emUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @AfterAll
    static void destroy() {
        entityManagerFactory.close();
    }

    @Test
    @Order(1)
    @DisplayName("Comments list is initialized")
    void commentsListIsInitialized() {
        Photo photo = new Photo();
        List<PhotoComment> comments = photo.getComments();

        assertThat(comments).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Setter for field \"comments\" is private in Photo entity")
    void commentsSetterIsPrivate() throws NoSuchMethodException {
        Method setComments = Photo.class.getDeclaredMethod("setComments", List.class);

        assertThat(setComments.getModifiers()).isEqualTo(Modifier.PRIVATE);
    }

    @Test
    @Order(3)
    @DisplayName("Photo table name is specified")
    void photoTableNameIsSpecified() {
        Table table = Photo.class.getAnnotation(Table.class);
        String tableName = table.name();

        assertThat(tableName).isEqualTo("photo");
    }

    @Test
    @Order(4)
    @DisplayName("Photo comment table name is specified")
    void photoCommentTableNameIsSpecified() {
        Table table = PhotoComment.class.getAnnotation(Table.class);

        assertThat(table.name()).isEqualTo("photo_comment");
    }

    @Test
    @Order(5)
    @DisplayName("Photo URL is not null and unique")
    void photoUrlIsNotNullAndUnique() throws NoSuchFieldException {
        Field url = Photo.class.getDeclaredField("url");
        Column column = url.getAnnotation(Column.class);

        assertThat(column.nullable()).isFalse();
        assertThat(column.unique()).isTrue();
    }

    @Test
    @Order(6)
    @DisplayName("Photo comment text is mandatory")
    void photoCommentTextIsMandatory() throws NoSuchFieldException {
        Field text = PhotoComment.class.getDeclaredField("text");
        Column column = text.getAnnotation(Column.class);

        assertThat(column.nullable()).isFalse();
    }

    @Test
    @Order(7)
    @DisplayName("Cascade type ALL is enabled for comments")
    void cascadeTypeAllIsEnabledForComments() throws NoSuchFieldException {
        Field comments = Photo.class.getDeclaredField("comments");
        OneToMany oneToMany = comments.getAnnotation(OneToMany.class);
        CascadeType[] expectedCascade = {CascadeType.ALL};

        assertThat(oneToMany.cascade()).isEqualTo(expectedCascade);
    }

    @Test
    @Order(8)
    @DisplayName("Orphan removal is enabled for comments")
    void orphanRemovalIsEnabledForComments() throws NoSuchFieldException {
        Field comments = Photo.class.getDeclaredField("comments");
        OneToMany oneToMany = comments.getAnnotation(OneToMany.class);

        assertThat(oneToMany.orphanRemoval()).isTrue();
    }

    @Test
    @Order(9)
    @DisplayName("Foreign key column is specified")
    void foreignKeyColumnIsSpecified() throws NoSuchFieldException {
        Field photo = PhotoComment.class.getDeclaredField("photo");
        JoinColumn joinColumn = photo.getAnnotation(JoinColumn.class);

        assertThat(joinColumn.name()).isEqualTo("photo_id");
    }

    @Test
    @Order(10)
    @DisplayName("Save a photo only")
    void savePhotoOnly() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        assertThat(photo.getId()).isNotNull();
    }

    @Test
    @Order(11)
    @DisplayName("Save a photo comment only")
    void savePhotoCommentOnly() {
        PhotoComment photoComment = createRandomPhotoComment();

        assertThatExceptionOfType(PersistenceException.class).isThrownBy(() ->
                emUtil.performWithinTx(entityManager -> entityManager.persist(photoComment)));
    }

    @Test
    @Order(12)
    @DisplayName("A comment cannot exist without a photo")
    void commentCannotExistsWithoutPhoto() throws NoSuchFieldException {
        Field photo = PhotoComment.class.getDeclaredField("photo");
        ManyToOne manyToOne = photo.getAnnotation(ManyToOne.class);

        assertThat(manyToOne.optional()).isFalse();
    }

    @Test
    @Order(13)
    @DisplayName("Save a new comment")
    void saveNewComment() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        PhotoComment photoComment = createRandomPhotoComment();
        photoComment.setPhoto(photo);
        emUtil.performWithinTx(entityManager -> entityManager.persist(photoComment));

        assertThat(photoComment.getId()).isNotNull();
        emUtil.performWithinTx(entityManager -> {
            PhotoComment managedPhotoComment = entityManager.find(PhotoComment.class, photoComment.getId());
            assertThat(managedPhotoComment.getPhoto()).isEqualTo(photo);
        });
        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments()).contains(photoComment);
        });
    }

    @Test
    @Order(14)
    @DisplayName("Add a new comment")
    void addNewComment() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        PhotoComment photoComment = createRandomPhotoComment();
        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            managedPhoto.addComment(photoComment);
        });

        assertThat(photoComment.getId()).isNotNull();
        emUtil.performWithinTx(entityManager -> {
            PhotoComment managedPhotoComment = entityManager.find(PhotoComment.class, photoComment.getId());
            assertThat(managedPhotoComment.getPhoto()).isEqualTo(photo);
        });
        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments()).contains(photoComment);
        });
    }

    @Test
    @Order(15)
    @DisplayName("Save new comments")
    void saveNewComments() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        List<PhotoComment> listOfComments = createListOfRandomComments(5);
        listOfComments.forEach(comment -> comment.setPhoto(photo));

        emUtil.performWithinTx(entityManager -> listOfComments.forEach(entityManager::persist));

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments()).containsExactlyInAnyOrderElementsOf(listOfComments);
        });
    }

    @Test
    @Order(16)
    @DisplayName("Add a new comment")
    void addNewComments() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));
        List<PhotoComment> listOfComments = createListOfRandomComments(5);

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            listOfComments.forEach(managedPhoto::addComment);
        });

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments()).containsExactlyInAnyOrderElementsOf(listOfComments);
        });
    }

    @Test
    @Order(17)
    @DisplayName("Remove a comment")
    void removeComment() {
        Photo photo = createRandomPhoto();
        PhotoComment photoComment = createRandomPhotoComment();
        List<PhotoComment> commentList = createListOfRandomComments(5);
        photo.addComment(photoComment);
        commentList.forEach(photo::addComment);
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            PhotoComment managedComment = entityManager.find(PhotoComment.class, photoComment.getId());
            managedPhoto.removeComment(managedComment);
        });

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            PhotoComment managedPhotoComment = entityManager.find(PhotoComment.class, photoComment.getId());

            assertThat(managedPhoto.getComments()).doesNotContain(photoComment);
            assertThat(managedPhoto.getComments()).containsExactlyInAnyOrderElementsOf(commentList);
            assertThat(managedPhotoComment).isNull();
        });
    }
}
