package com.nisum.myteam.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Document(collection = "domains")
public class Domain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;

	private String domainId;
	@NotNull
	@Size(min = 2, max = 80, message = "Domain Name should have atlast 2 and less than 80 characters")
	private String domainName;
	@NotBlank
	private String accountId;
	@NotBlank
	private String status;
	@NotNull
	List<?> deliveryManagers;
}
