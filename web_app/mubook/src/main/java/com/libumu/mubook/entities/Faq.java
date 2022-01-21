package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "faq")
public class Faq {

    @Id
    @GenericGenerator(name="faq" , strategy="increment")
    @GeneratedValue(generator="faq")
    @Column(name = "faq_id")
    private Integer faqid;

    @Column(name = "question")
    private String question;
    @Column(name = "answer")
    private String answer;

    public Faq() {
    }

    public Integer getFaqid() {
        return faqid;
    }

    public void setFaqid(Integer faqid) {
        this.faqid = faqid;
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

}