package edu.teikav.robot.parser;

import java.io.File;

public class ParserStaticConstants {

    // Non-instantiable class
    private ParserStaticConstants() {}

    public static final String MAIN_RESOURCES_PATH = new File("src/main/resources").getAbsolutePath();
    public static final String TEST_RESOURCES_PATH = new File("src/test/resources").getAbsolutePath();
    public static final String IMAGES_PATH = TEST_RESOURCES_PATH + "/textImages/";
}
