package com.nisum.myteam.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class StatusResponse {
  private int statusCode;
  private String title;
  private String message;
}
