package com.nisum.myteam.model.vo;

import lombok.*;

import java.util.HashMap;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DomainVO {

    private String Id;

    private String domainId;

    private String domainName;

    private String accountId;

    private String accountName;

    private String status;

    private List<HashMap<String,String>> deliveryManagers;

}
