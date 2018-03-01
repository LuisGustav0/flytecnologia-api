package com.flytecnologia.core.report;

import com.flytecnologia.core.exception.BusinessException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class FlyReportUtil {
    public byte[] generarPdf(String filePath,
                             String fileName,
                             Map<String, Object> parameters,
                             List<?> data) throws Exception {

        Resource resource = new ClassPathResource(filePath + File.separator + fileName);

        InputStream input = resource.getInputStream();

        if(input == null)
            throw new BusinessException(filePath + File.separator + fileName + " not found");

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(input);

        return JasperRunManager.runReportToPdf(jasperReport, parameters,
                new JRBeanCollectionDataSource(data)
        );
    }
}
