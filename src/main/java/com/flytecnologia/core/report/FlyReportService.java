package com.flytecnologia.core.report;

import java.util.List;
import java.util.Map;

public interface FlyReportService {
    String getResourcePath(String path);
    byte[] generatePDF(String filePath,
                       String fileName,
                       Map<String, Object> parameters);
    byte[] generatePDF(String filePath,
                       String fileName,
                       Map<String, Object> parameters,
                       List<?> data);
    byte[] generatePDF(String filePath,
                       String fileName,
                       Map<String, Object> parameters,
                       List<?> data,
                       boolean throwsExceptions);
}
