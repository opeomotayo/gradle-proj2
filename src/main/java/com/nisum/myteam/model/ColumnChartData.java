package com.nisum.myteam.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ColumnChartData implements Serializable {

    private String categories; // GAP
    private String seriesName; // Billable
    private long count; // count
    private List categoriesList;
    private List seriesDataList;
    private String billableStatus;

}