package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GenericGenerator(name="news" , strategy="increment")
    @GeneratedValue(generator="news")
    @Column(name = "news_id")
    private Long newsid;

    @Column(name = "description")
    private String description;
    @Column(name = "initDate")
    private Date initDate;
    @Column(name = "endDate")
    private Date endDate;
    @Column(name = "title")
    private String title;
    @Column(name = "image")
    private String image;

    public News() {
    }

    public Long getNewsid() {
        return newsid;
    }

    public void setNewsid(Long newsid) {
        this.newsid = newsid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
