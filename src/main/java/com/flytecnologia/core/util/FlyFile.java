package com.flytecnologia.core.util;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

@Component
public class FlyFile {

    public void saveImgToFile(String imgImgDir, Long idRecord, String field, String photo) {
        photo = photo.split(",")[1];

        byte[] decodedImg = Base64.getDecoder().decode(photo);
        String dir = imgImgDir + File.separator + idRecord.toString();
        String fileName = field + ".jpg";

        File folder = new File(dir);

        if (!folder.exists()) {
            boolean created = folder.mkdirs();

            if (!created) {
                throw new BusinessException("Error to create folder " + imgImgDir);
            }
        }

        Path destinationFile = Paths.get(dir, fileName);
        try {
            Files.write(destinationFile, decodedImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFile(String imgImgDir, Long id, String filename) {
        String dir = imgImgDir + File.separator + id.toString() + File.separator + filename;

        File folder = new File(dir);

        if (folder.exists()) {
            folder.delete();
        }
    }

    public void saveImgToFile(String imgImgDir, FlyEntity entity, String fieldname) {
        Map<String, Object> parameters = entity.getParameters();

        if (parameters == null)
            return;

        if (!StringUtils.isEmpty(parameters.get(fieldname))) {
            saveImgToFile(imgImgDir, entity.getId(), fieldname, (String) parameters.get(fieldname));
        }
    }

    public void getImageAsByteArray(String imgImgDir, Long id, String filename, HttpServletResponse response) {
        try {
            Path file = Paths.get(imgImgDir + File.separator + id).resolve(filename);
            InputStream in = new FileInputStream(file.toFile());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
