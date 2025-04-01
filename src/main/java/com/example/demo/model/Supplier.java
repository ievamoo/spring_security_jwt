package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@ToString(exclude = "carParts")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarPart> carParts;

}
