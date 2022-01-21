package com.libumu.mubook.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "specification")
public class Specification implements Comparable<Specification>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "specification_id")
    private Integer specificationId;

    @Column(name = "desccription")
    private String description;

    @JsonBackReference
    @OneToMany(mappedBy = "specification")
    private List<SpecificationList> specificationLists;

    public Specification() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(Integer specificationId) {
        this.specificationId = specificationId;
    }

    public List<SpecificationList> getSpecificationLists() {
        return specificationLists;
    }

    public void setSpecificationLists(List<SpecificationList> specificationLists) {
        this.specificationLists = specificationLists;
    }

    @Override
    public String toString() {
        return this.description + " " + this.specificationId;
    }

    @Override
    public int compareTo(Specification o) {
        return this.description.compareTo(o.description);
    }
}
