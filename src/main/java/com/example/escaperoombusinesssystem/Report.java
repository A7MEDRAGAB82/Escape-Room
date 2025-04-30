package com.example.escaperoombusinesssystem;

import java.util.*;
import java.time.*;

public class Report {
    private String reportId;
    //keys are unique,put method is to replace the key if repeated , add is just add
    private Map<String, Object> data;
    private LocalDate generatedDate;

    public Report(Map<String, Object> data) {
        this.reportId = "RPT-" + System.currentTimeMillis();
        this.data = data;
        this.generatedDate = LocalDate.now();
    }

    public Map<String, Object> getData() {
        return data;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public String getReportId() {
        return reportId;
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public void removeData(String key) {
        data.remove(key);
    }

}


