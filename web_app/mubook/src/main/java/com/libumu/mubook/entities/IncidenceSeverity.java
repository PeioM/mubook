package com.libumu.mubook.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "incidence_severity")
public class IncidenceSeverity {

    @Id
    @GenericGenerator(name="incidenceSeverity" , strategy="increment")
    @GeneratedValue(generator="incidenceSeverity")
    @Column(name = "incidence_severity_id")
    private Integer incidenceSeverityId;

    @Column(name = "description")
    private String description;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "importance")
    private Integer importance;


    public IncidenceSeverity(){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIncidenceSeverityId() {
        return incidenceSeverityId;
    }

    public void setIncidenceSeverityId(Integer incidenceSeverityId) {
        this.incidenceSeverityId = incidenceSeverityId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }
}
