package com.flytecnologia.core.base.plusService;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface FlyPrintService<F extends FlyFilter> {

    default byte[] getReport(F filter) {
        return null;
    }

    default ResponseEntity<ByteArrayResource> print(F filter) {
        final byte[] data = getReport(filter);

        if (data == null)
            throw new BusinessException("flyserivice.generateReportError");

        final String fileName = filter.getPdfName() != null ? filter.getPdfName() : "report.pdf";

        final ByteArrayResource resource = new ByteArrayResource(data);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        headers.setCacheControl("no-cache, no-store, must-revalidate, post-check=0, pre-check=0");
        headers.setPragma("no-cache");
        headers.setExpires(0);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(resource);
    }
}
