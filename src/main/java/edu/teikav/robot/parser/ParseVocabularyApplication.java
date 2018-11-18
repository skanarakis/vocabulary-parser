package edu.teikav.robot.parser;

import edu.teikav.robot.parser.services.InstructorParser;
import edu.teikav.robot.parser.services.TesseractBasedParser;
import org.bytedeco.javacpp.tesseract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.File;

import static edu.teikav.robot.parser.ParserStaticConstants.IMAGES_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.MAIN_RESOURCES_PATH;

@SpringBootApplication
public class ParseVocabularyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParseVocabularyApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(){
        return args -> {
            InstructorParser parser = getInstructorParser();
            File inputFile = new File(IMAGES_PATH + "oscar-wilde.png");
            String output = parser.parseImage(inputFile);
            System.out.println("Output:\n"  + output);
        };
    }

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
