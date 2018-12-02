package edu.teikav.robot.parser.config;

import edu.teikav.robot.parser.services.InstructorParser;
import edu.teikav.robot.parser.services.TesseractBasedParser;
import org.bytedeco.javacpp.tesseract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static edu.teikav.robot.parser.ParserStaticConstants.MAIN_RESOURCES_PATH;

@Configuration
public class OCRConfig {

    @Profile("tesseract")
    @Bean
    InstructorParser getInstructorParser() {
        return new TesseractBasedParser(ocrLibraryAPIObject());
    }

    @Profile("tesseract")
    @Qualifier("OCR_API_OBJECT")
    @Bean Object ocrLibraryAPIObject() {
        tesseract.TessBaseAPI libraryAPI = new tesseract.TessBaseAPI();
        int initializationResult = libraryAPI.Init(MAIN_RESOURCES_PATH, "ENG");
        if (initializationResult != 0) {
            throw new RuntimeException("OCR library could not be properly initialized");
        }
        return libraryAPI;
    }
}
