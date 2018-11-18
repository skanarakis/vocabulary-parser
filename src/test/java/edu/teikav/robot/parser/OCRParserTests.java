package edu.teikav.robot.parser;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static edu.teikav.robot.parser.ParserStaticConstants.IMAGES_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.MAIN_RESOURCES_PATH;
import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OCRParserTests {

    private static tesseract.TessBaseAPI ocrBaseAPI;
    private Logger logger = LoggerFactory.getLogger(OCRParserTests.class);

    @BeforeClass
    public static void initialize() {
        ocrBaseAPI = new tesseract.TessBaseAPI();
        // Initialize API to use English language
        int initializationResult = ocrBaseAPI.Init(MAIN_RESOURCES_PATH, "ENG");
        assertThat(initializationResult, equalTo(0));
    }

    @Test
    public void whenImageContainsFullEnglishPage_ocrWorks() {

        lept.PIX image = ingestImage("domestic-dog.png");

        // Read OCR from the image, then take its String representation
        BytePointer ocrOutput;
        ocrOutput = ocrBaseAPI.GetUTF8Text();
        String ocrStringRepresentation = ocrOutput.getString();

        assertNotEquals(0, ocrStringRepresentation.length());
        assertThat(ocrStringRepresentation, startsWith("Dog\nFrom Wikipedia,"));
        assertThat(ocrStringRepresentation, endsWith("man's best friend\".\n\n"));

        logger.info("OCR representation:\n{}", ocrStringRepresentation);

        cleanNativeResources(ocrOutput, image);

    }

    private lept.PIX ingestImage(String imageFileName) {
        // leptonica library used to open the input sample image to OCR
        lept.PIX image = pixRead( IMAGES_PATH + imageFileName);
        ocrBaseAPI.SetImage(image);
        return image;
    }

    private void cleanNativeResources(BytePointer ocrOutput, lept.PIX image) {
        // Clean resources (memory, objects)
        ocrBaseAPI.End();
        ocrOutput.deallocate();
        pixDestroy(image);
    }

}
