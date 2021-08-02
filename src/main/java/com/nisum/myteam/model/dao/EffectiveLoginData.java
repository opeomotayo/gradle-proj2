package com.nisum.myteam.model.dao;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
@Document(collection = "effectiveLoginData")
public class EffectiveLoginData implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;

    private String name;
    private String employeeId;
    private Date date;
    private String loginTime;
    private String logoutTime;
    private String durationAtWorkPlace;
    private Date createdDate;
    private List<String> orphanLogin;
    public EffectiveLoginData(){
        orphanLogin = new ArrayList<>();
    }
}
