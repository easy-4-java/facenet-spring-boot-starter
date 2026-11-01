package org.bytedeco.opencv.spring.boot;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.junit.jupiter.api.Test;
import org.opencv.objdetect.CascadeClassifier;
import nu.pattern.OpenCV;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FaceRecognitionNativeTest {

    @Test
    void loadsBothOpenCvJavaApiAndBundledClassifier() throws Exception {
        OpenCV.loadShared();
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
}
