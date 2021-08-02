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
@Document(collection = "masterData")
public class MasterData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String masterDataType;
    private String masterDataId;
    private String masterDataName;
    private boolean activeStatus;
    private String comments;

    public MasterData(String masterDataType, String masterDataId, String masterDataName, boolean activeStatus) {
        this.masterDataType = masterDataType;
        this.masterDataId = masterDataId;
        this.masterDataName = masterDataName;
        this.activeStatus = activeStatus;
    }
}
