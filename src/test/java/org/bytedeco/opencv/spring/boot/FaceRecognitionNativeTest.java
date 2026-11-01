package org.bytedeco.opencv.spring.boot;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.junit.jupiter.api.Test;
import org.opencv.objdetect.CascadeClassifier;
import nu.pattern.OpenCV;
import com.alibaba.fastjson.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaceRecognitionNativeTest {

    @Test
    void loadsBothOpenCvJavaApiAndBundledClassifier() throws Exception {
        OpenCV.loadLocally();
        assertNotNull(Loader.load(opencv_imgcodecs.class));
        assertNotNull(Loader.load(opencv_imgproc.class));
        Path classifierFile = Files.createTempFile("facenet-classifier-", ".xml");
        try {
            try (InputStream source = getClass().getResourceAsStream(
                    "/haarcascades/haarcascade_frontalface_alt.xml")) {
                assertNotNull(source);
                Files.copy(source, classifierFile, StandardCopyOption.REPLACE_EXISTING);
            }
            assertFalse(new CascadeClassifier(classifierFile.toString()).empty());
        } finally {
            Files.deleteIfExists(classifierFile);
        }
    }

    @Test
    void smoothAndMatchUseTheBundledJavaCppNativeModules() throws Exception {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int intensity = (x * 11 + y * 7) & 0xff;
                image.setRGB(x, y, (intensity << 16) | (intensity << 8) | intensity);
            }
        }
        Path imageFile = Files.createTempFile("facenet-smooth-match-", ".png");
        try {
            assertTrue(ImageIO.write(image, "png", imageFile.toFile()));
            FacenetFaceRecognitionTemplate recognition = new FacenetFaceRecognitionTemplate(
                    null, new FacenetFaceRecognitionProperties());
            recognition.smooth(imageFile.toString());
            assertTrue(Files.size(imageFile) > 0);
            JSONObject match = recognition.match(imageFile.toFile(), imageFile.toFile());
            assertNotNull(match.getDouble("score"));
            assertEquals(1.0, match.getDouble("score"), 0.000001);
        } finally {
            Files.deleteIfExists(imageFile);
        }
    }
}
