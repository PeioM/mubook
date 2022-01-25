package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "incidence")
public class Incidence {

    @Id
    @GenericGenerator(name="incidence" , strategy="increment")
    @GeneratedValue(generator="incidence")
    @Column(name = "incidence_id")
    private Long incidenceId;

    @ManyToOne
    @JoinColumn(name = "incidence_severity_id")
    private IncidenceSeverity incidenceSeverity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "description")
    private String description;
    @Column(name = "initDate")
    private Date initDate;
    @Column(name = "endDate")
    private Date endDate;

    public Incidence(){
        //This is empty
    }

    public Long getIncidenceId() {
        return incidenceId;
    }

    public void setIncidenceId(Long incidenceId) {
        this.incidenceId = incidenceId;
    }

    public IncidenceSeverity getIncidenceSeverity() {
        return incidenceSeverity;
    }

    public void setIncidenceSeverity(IncidenceSeverity incidenceSeverity) {
        this.incidenceSeverity = incidenceSeverity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
