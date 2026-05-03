package com.example.usergenerator.vo.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class HealthIndicatorVO {

    private String indicatorType;

    private String unit;

    private StatisticsVO statistics;

    private List<RecordVO> records;

    @Data
    public static class StatisticsVO {
        private LatestVO latest;
        private AverageVO average;
        private MaxVO max;
        private MinVO min;
    }

    @Data
    public static class LatestVO {
        private Integer systolic;
        private Integer diastolic;
        private String recordDate;
        private String recordTime;
    }

    @Data
    public static class AverageVO {
        private Integer systolic;
        private Integer diastolic;
    }

    @Data
    public static class MaxVO {
        private Integer systolic;
        private Integer diastolic;
    }

    @Data
    public static class MinVO {
        private Integer systolic;
        private Integer diastolic;
    }

    @Data
    public static class RecordVO {
        private Long recordId;
        private Integer systolic;
        private Integer diastolic;
        private Integer value;
        private Integer heartRate;
        private String recordDate;
        private String recordTime;
        private String remark;
        private String source;
    }
}