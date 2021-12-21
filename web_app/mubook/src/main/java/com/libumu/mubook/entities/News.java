package com.libumu.mubook.entities;


import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "news_id")
    private Long newsid;

    @Column(name = "description")
    private String description;
    @Column(name = "initDate")
    private Date initDate;
    @Column(name = "endDate")
    private Date endDate;
    @Column(name = "num_clicks")
    private Integer numClicks;
    @Column(name = "title")
    private String title;

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

    public Integer getNumClicks() {
        return numClicks;
    }

    public void setNumClicks(Integer numClicks) {
        this.numClicks = numClicks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
