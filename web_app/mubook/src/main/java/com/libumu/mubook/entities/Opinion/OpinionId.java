package com.libumu.mubook.entities.Opinion;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OpinionId implements Serializable{
    @Column(name = "user_id")
    private Long user;
    @Column(name = "item_model_id")
    private Long itemModel;

    public OpinionId() {
    }

    public long getItemModel() {
        return itemModel;
    }

    public void setItemModel(long itemModel) {
        this.itemModel = itemModel;
    }

    @Override
    public int hashCode() {
        return user.hashCode() + itemModel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) return false;
    
        if (this.getClass() != obj.getClass()) return false;

        OpinionId sl = (OpinionId) obj;
        return (sl.getUser()==this.user && sl.getItemModel()==this.itemModel);
        
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
