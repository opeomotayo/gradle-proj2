package com.nisum.myteam.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data

public class FromToDates {
  private Date fromDate;
  private Date toDate;
}
