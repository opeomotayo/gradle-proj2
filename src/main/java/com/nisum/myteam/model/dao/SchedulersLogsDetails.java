package com.nisum.myteam.model.dao;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "schedulersLogDetails")
public class SchedulersLogsDetails implements Serializable {
    private static final long serialVersionUID = 6160218148752874740L;
    @Id
    private String id;
    private String schedulerName;
    private String schedulerStatus;
    private Date createdDate;
    private String date;
}
