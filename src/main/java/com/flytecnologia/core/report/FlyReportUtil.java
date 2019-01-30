package com.flytecnologia.core.report;

import com.flytecnologia.core.exception.BusinessException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class FlyReportUtil {
    ResourceLoader resourceLoader;

    public FlyReportUtil(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public String getResourcePath(String path) {
        try {
            return getResourceLoader().getResource("classpath:" + path).getURI().getPath();
        } catch (Exception ex) {
            return null;
        }
    }


    public byte[] generatePDF(String filePath,
                              String fileName,
                              Map<String, Object> parameters) {
        return generatePDF(filePath, fileName, parameters, null, true);

    }

    public byte[] generatePDF(String filePath,
                              String fileName,
                              Map<String, Object> parameters,
                              List<?> data) {
        return generatePDF(filePath, fileName, parameters, data, true);

    }

    public byte[] generatePDF(String filePath,
                              String fileName,
                              Map<String, Object> parameters,
                              List<?> data,
                              boolean throwsExceptions) {

        try {
            final Resource resource = new ClassPathResource(filePath + File.separator + fileName);

            final InputStream input = resource.getInputStream();

            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(input);

            byte[] bt;

            if (data != null && data.size() > 0) {
                bt = JasperRunManager.runReportToPdf(jasperReport, parameters,
                        new JRBeanCollectionDataSource(data)
                );
            } else {
                bt = JasperRunManager.runReportToPdf(jasperReport, parameters);
            }

            input.close();

            return bt;
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
            throw new BusinessException("flyserivice.reportNotFound");
        } catch (Exception ex) {
            if (throwsExceptions) {
                ex.printStackTrace();
                throw new BusinessException("flyserivice.generateReportError");
            } else {
                return null;
            }
        }
    }
}
