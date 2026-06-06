package kr.kro.personalos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column
    private int age;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private String schoolNm;

    @Column
    private String lctn;

    @Column(length = 100)
    private String password;

    @Column
    private String hash;

}