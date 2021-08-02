package com.nisum.myteam.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignationCountVO {
    private String designationId;
    private String designationName;
    private int designationCount;
    private boolean active;
}
