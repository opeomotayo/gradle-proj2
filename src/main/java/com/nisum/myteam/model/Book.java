package com.nisum.myteam.model;


import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {
    private int bookId;
    private String bookName;
    private Date date;
}
