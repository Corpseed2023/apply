package com.apply.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "platforms")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCredential> credentials;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    // Required by JPA
    public Platform() {}

    // Convenience constructor
    public Platform(String name) {
        this.name = name;
    }

    // Getters and Setters (if not using Lombok)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<UserCredential> credentials) {
        this.credentials = credentials;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
