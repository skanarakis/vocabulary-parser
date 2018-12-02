package edu.teikav.robot.parser.domain;

import com.rtfparserkit.parser.IRtfListener;

public interface RTFParserCallbackProcessor extends IRtfListener {

    // Used only for testing purposes, we may need to refactor at some point
    void reset();
}
