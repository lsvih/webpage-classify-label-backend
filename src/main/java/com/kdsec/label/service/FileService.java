package com.kdsec.label.service;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static java.lang.String.*;

public class FileService {
    private String path;

    public FileService() {
        path = format("%s/uploads", System.getProperty("user.dir"));
    }

    public void save(Long id, String html, String image) throws IOException {
        writeHtml(format("%s.html", Long.toString(id)), html);
        writeImage(format("%s.png", Long.toString(id)), image);
    }

    private void writeHtml(String fileName, String html) throws IOException {
        PrintWriter out = new PrintWriter(format("%s/html/%s", path, fileName));
        out.write(html);
        out.close();
    }

    private void writeImage(String fileName, String image) throws IOException {
        byte[] imgBytes = Base64.decodeBase64(image.replaceFirst("data:image/\\w+;base64,", ""));
        File imgFile = new File(format("%s/image/%s", path, fileName));
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        ImageIO.write(img, "png", imgFile);
    }
}
