package com.example.usergenerator.vo.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class VisitRecordVO {

    private Long visitId;

    private String visitType;

    private String visitDate;

    private String department;

    private DoctorVO doctor;

    private String chiefComplaint;

    private String diagnosis;

    private String treatment;

    private Long prescriptionId;

    private List<AttachmentVO> attachments;

    @Data
    public static class DoctorVO {
        private Long doctorId;
        private String name;
        private String title;
    }

    @Data
    public static class AttachmentVO {
        private String fileId;
        private String fileName;
        private String fileType;
        private String fileUrl;
    }
}