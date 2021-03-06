package edu.teikav.robot.parser;

import java.io.File;

public class ParserStaticConstants {

    // Non-instantiable class
    private ParserStaticConstants() {}

    private static final String TEST_RESOURCES_PATH =
            new File("src/test/resources").getAbsolutePath();

    public static final String TEST_INPUT_RTF_DOCS_PATH = TEST_RESOURCES_PATH + "/inputRTFDocs/";
    public static final String TEST_OUTPUT_XML_DOCS_PATH = TEST_RESOURCES_PATH + "/outputXMLDocs/";
}
