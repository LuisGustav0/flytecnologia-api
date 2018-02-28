package com.flytecnologia.core.report;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FlyReportUtil {
    public static byte[] generarPdf(String filePath,
                                    Map<String, Object> parameters,
                                    List<?> data) throws Exception {

        InputStream input = new FileInputStream(filePath);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(input);

        return JasperRunManager.runReportToPdf(jasperReport, parameters,
                new JRBeanCollectionDataSource(data)
        );
    }
}
