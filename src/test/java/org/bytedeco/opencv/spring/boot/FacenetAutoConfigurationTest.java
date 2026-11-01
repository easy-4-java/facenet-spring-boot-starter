package org.bytedeco.opencv.spring.boot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacenetAutoConfigurationTest {

    @TempDir
    Path tempDir;

    @Test
    void discoversFaceRecognitionWhenEnabled() {
        try (ConfigurableApplicationContext context = application()
                .run("--opencv.face.enabled=true", "--opencv.face.temp=" + tempDir)) {
            assertTrue(context.containsBean("faceDetector"));
            assertFalse(context.getBean(CascadeClassifier.class).empty());
            assertTrue(context.containsBean("openCVFaceRecognitionTemplate"));
            assertNotNull(context.getBean(FacenetFaceRecognitionTemplate.class));
        }
    }

    @Test
    void doesNotRegisterFaceRecognitionWhenDisabled() {
        try (ConfigurableApplicationContext context = application()
                .run("--opencv.face.enabled=false")) {
            assertTrue(context.getBeansOfType(CascadeClassifier.class).isEmpty());
            assertTrue(context.getBeansOfType(FacenetFaceRecognitionTemplate.class).isEmpty());
        }
    }

    private SpringApplicationBuilder application() {
        return new SpringApplicationBuilder(TestApplication.class)
                .web(WebApplicationType.NONE)
                .logStartupInfo(false);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }
}
