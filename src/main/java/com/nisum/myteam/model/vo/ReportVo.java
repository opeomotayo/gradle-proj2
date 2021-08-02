package com.nisum.myteam.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter

public class ReportVo {

    List<String> categoriesList = new ArrayList();
    List<Map<String,Object>> seriesDataList = new ArrayList();

//    public ReportVo(){
//        categoriesList.add("ES");
//        categoriesList.add("CI");
//        categoriesList.add("APPS");
//        categoriesList.add("ACI - QE");
//        categoriesList.add("ACI - DevOps");
//        categoriesList.add("ACI - Support");
//        categoriesList.add("I&A");
//    }
}
