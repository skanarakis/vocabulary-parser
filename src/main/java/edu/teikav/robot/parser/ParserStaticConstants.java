package edu.teikav.robot.parser;

import java.io.File;

public class ParserStaticConstants {

    // Non-instantiable class
    private ParserStaticConstants() {}

    public static final String MAIN_RESOURCES_PATH = new File("src/main/resources").getAbsolutePath();
    public static final String INTEGRATION_TEST_RESOURCES_PATH =
            new File("src/integration-test/resources").getAbsolutePath();

    public static final String TEST_IMAGES_PATH = INTEGRATION_TEST_RESOURCES_PATH + "/textImages/";
    public static final String TEST_INPUT_RTF_DOCS_PATH = INTEGRATION_TEST_RESOURCES_PATH + "/inputRTFDocs/";
    public static final String TEST_OUTPUT_XML_DOCS_PATH = INTEGRATION_TEST_RESOURCES_PATH + "/outputXMLDocs/";

    public static final String FIRST_PASS_OUTPUT_XML_FILENAME = "output-first-pass.xml";
    public static final String FIRST_PASS_TEST_OUTPUT_XML_FILENAME = "test-output-first-pass.xml";
    public static final String SECOND_PASS_OUTPUT_XML_FILENAME = "output-second-pass.xml";
    public static final String SECOND_PASS_TEST_OUTPUT_XML_FILENAME = "test-output-second-pass.xml";
}
