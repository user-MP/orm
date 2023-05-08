package com.bobocode.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/**
 * todo:
 * - configure JPA entity
 * - specify table name: "product"
 * - configure auto generated identifier
 * - configure mandatory column "name" for field {@link Product#name}
 * <p>
 * - configure lazy many-to-one relation between {@link Product} and {@link Company}
 * - configure foreign key column "company_id" references company table
 * - override equals() and hashCode() considering entity id
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;


}
