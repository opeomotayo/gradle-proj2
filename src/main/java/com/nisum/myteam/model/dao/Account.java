package com.nisum.myteam.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "accounts")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
   

    private String accountId;
    @NotNull
    @Size(min=2,max=50,message="Name should have atlast 2 and less than 50 characters")
    private String accountName;
    private Integer accountProjectSequence;
    private String status;
    //private String domain;
    //List<String> subDomains;
    
    @NotBlank
    private String clientAddress;
    @NotBlank
    private String industryType;
    @NotNull
    private List<String> deliveryManagers;

}
