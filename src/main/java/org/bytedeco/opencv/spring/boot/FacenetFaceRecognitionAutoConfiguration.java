package org.bytedeco.opencv.spring.boot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import nu.pattern.OpenCV;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConditionalOnProperty(prefix = FacenetFaceRecognitionProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ FacenetFaceRecognitionProperties.class })
public class FacenetFaceRecognitionAutoConfiguration {
	
	@Value("classpath:haarcascades/haarcascade_frontalface_alt.xml")
	private Resource classifier;
	
	static {
		OpenCV.loadShared();
		//new opencv_java();
	}
	
    @Bean
    public CascadeClassifier faceDetector(FacenetFaceRecognitionProperties properties) throws IOException {
    	// 创建临时文件，因为boot打包后无法读取文件内的内容
    	File tempDir = new File(properties.getTemp());
    	if(!tempDir.exists()) {
    		tempDir.setReadable(true);
    		tempDir.setWritable(true);
    		tempDir.mkdir();
    	}
		File targetXmlFile = new File(tempDir, classifier.getFilename());
		FileUtils.copyInputStreamToFile(classifier.getInputStream(), targetXmlFile);
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		return new CascadeClassifier(targetXmlFile.getPath());
	}
    
	@Bean
	public FacenetFaceRecognitionTemplate openCVFaceRecognitionTemplate(CascadeClassifier faceDetector,
			FacenetFaceRecognitionProperties properties) {
		return new FacenetFaceRecognitionTemplate(faceDetector, properties);
	}
	
}
