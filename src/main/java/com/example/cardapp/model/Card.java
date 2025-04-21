package com.example.cardapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String answer;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String createdByUsername;

    public Card() {}

    public Card(String question, String answer, String category, String createdByUsername) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.createdByUsername = createdByUsername;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
}
