package com.libumu.mubook.entities;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_model_id")
    private ItemModel itemModel;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Date date;

    public Comment(){}

    public Long getCommentId(){
        return this.commentId;
    }

    public void setCommentId(Long commentId){
        this.commentId = commentId;
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public ItemModel getItemModel(){
        return this.itemModel;
    }

    public void setItemModel(ItemModel itemModel){
        this.itemModel = itemModel;
    }

    public String getContent(){
        return this.content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(Date date){
        this.date = date;
    }
}
