package com.nisum.myteam.model.dao;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "orgLocations")
public class OrgLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String locationCode;
    private String location;
    private String country;
    private String state;
    private String city;
    private boolean activeStatus;
    private String comments;
}
