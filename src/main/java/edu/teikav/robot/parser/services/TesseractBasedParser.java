package edu.teikav.robot.parser.services;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PreDestroy;
import java.io.File;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

public class TesseractBasedParser implements InstructorParser {

    private tesseract.TessBaseAPI ocrBaseAPI;
    private Logger logger = LoggerFactory.getLogger(TesseractBasedParser.class);

    @Autowired
    public TesseractBasedParser(@Qualifier("OCR_API_OBJECT") Object ocrBaseAPI) {
        this.ocrBaseAPI = (tesseract.TessBaseAPI) ocrBaseAPI;
    }

    @Override
    public String parseImage(File imageFile) {
        logger.info("Attempt to parse image {}", imageFile.getName());
        lept.PIX image = pixRead(imageFile.getAbsolutePath());
        ocrBaseAPI.SetImage(image);

        // Read OCR from the image, then take its String representation
        BytePointer ocrOutput;
        ocrOutput = ocrBaseAPI.GetUTF8Text();
        String ocrStringRepresentation = ocrOutput.getString();

        ocrOutput.deallocate();
        pixDestroy(image);
        return ocrStringRepresentation;
    }

    // Spring will make sure we gracefully terminate the API object upon bean deletion
    @PreDestroy
    public void destroy() {
        ocrBaseAPI.End();
    }
}
