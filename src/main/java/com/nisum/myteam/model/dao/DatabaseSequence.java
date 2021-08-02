package com.nisum.myteam.model.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Document(collection = "database_sequences")
public class DatabaseSequence implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;
  private long seq;
}
