package com.nisum.myteam.model.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
@Data

@Document(collection = "mystatus")
public class MyStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Transient
  public static final String SEQUENCE_NAME = "users_sequence";

  @Id
  private Integer id;
  private Date taskDate;
  private String ticketNumber;
  private int storyPoints;
  private Date planedStartDate;
  private Date planedEndDate;
  private Date actualStartDate;
  private Date actualEndDate;
  private String hoursSpent;
  private String comments;
  private String taskDetails;
  private String priority;
  private String taskType;
  private String empId;
  private String status;
  private LocalDateTime taskAddedTime = LocalDateTime.now();
}
