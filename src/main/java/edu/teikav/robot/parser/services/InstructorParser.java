package edu.teikav.robot.parser.services;

import java.io.File;

public interface InstructorParser {

    /**
     * Method to parse the image using a 3rd party OCR library
     * @param imageFile Represents the full path name of the image to be parsed
     * @return String representation of the OCR-parsed image
     */
    String parseImage(File imageFile);
}
