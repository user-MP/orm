package com.bobocode.model;

import com.bobocode.util.ExerciseNotCompletedException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * todo:
 * - make a setter for field {@link Photo#comments} {@code private}
 * - implement equals() and hashCode() based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "photo"
 * - configure auto generated identifier
 * - configure not nullable and unique column: url
 *
 * - initialize field comments
 * - map relation between Photo and PhotoComment on the child side
 * - implement helper methods {@link Photo#addComment(PhotoComment)} and {@link Photo#removeComment(PhotoComment)}
 * - enable cascade type {@link javax.persistence.CascadeType#ALL} for field {@link Photo#comments}
 * - enable orphan removal
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String url;
    private String description;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoComment> comments=new ArrayList<>();

    public void addComment(PhotoComment comment) {

        comment.setPhoto(this);

        comments.add(comment);

//        throw new ExerciseNotCompletedException();


    }

    public void removeComment(PhotoComment comment) {
        comment.setPhoto(null);

        comments.remove(comment);
//        throw new ExerciseNotCompletedException();
    }


    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Photo && Objects.equals(id, ((Photo) o).id));
    }
    @Override
    public int hashCode() {
        return 31;
    }
}
