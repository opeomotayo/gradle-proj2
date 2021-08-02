package com.nisum.myteam.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "functionalGroups")
public class FunctionalGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String name;
    private String groupHeadName;
    private String groupHeadEmpId;
    private String groupHeadEmailId;

}
