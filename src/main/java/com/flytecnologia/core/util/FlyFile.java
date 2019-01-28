package com.flytecnologia.core.util;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntityImpl;
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

        final byte[] decodedImg = Base64.getDecoder().decode(photo);
        final String dir = imgImgDir + File.separator + idRecord.toString();
        final String fileName = field + ".jpg";
        final File folder = new File(dir);

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

    public void deleteFile(String imgImgDir, Long id, String filename) {
        final String dir = getPathFoto(imgImgDir, id, filename);
        final File folder = new File(dir);

        if (folder.exists()) {
            folder.delete();
        }
    }

    public String getPathFoto(String imgImgDir, Long id, String filename) {
        return  imgImgDir + File.separator + id.toString() + File.separator + filename;
    }

    public void saveImgToFile(String imgImgDir, FlyEntityImpl entity, String fieldname) {
        final Map<String, Object> parameters = entity.getParameters();

        if (parameters == null)
            return;

        if (!StringUtils.isEmpty(parameters.get(fieldname))) {
            saveImgToFile(imgImgDir, entity.getId(), fieldname, (String) parameters.get(fieldname));
        }
    }

    public void getImageAsByteArray(String imgImgDir, Long id, String filename, HttpServletResponse response) {
        try {
            final Path file = Paths.get(imgImgDir + File.separator + id).resolve(filename);
            final InputStream in = new FileInputStream(file.toFile());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
