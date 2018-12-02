package edu.teikav.robot.parser.listeners;

import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.utils.RtfDumpListener;
import edu.teikav.robot.parser.domain.FontColor;
import edu.teikav.robot.parser.domain.Languages;
import edu.teikav.robot.parser.domain.RTFParserCallbackProcessor;
import edu.teikav.robot.parser.domain.VocabularyToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRTFCommandsCallbackProcessor implements RTFParserCallbackProcessor {

    private Logger logger = LoggerFactory.getLogger(AbstractRTFCommandsCallbackProcessor.class);

    // Use delegation to preserve the XML output
    RtfDumpListener rtfDumpListener;

    // One instance of this class will be holding information for each consecutive token
    // extracted from RFT document
    VocabularyToken currentToken;

    private int nextColorIndex = 1;
    private Map<Integer, FontColor> colorMap;

    // Temporary FontColor to hold the values so that we can have them ready when
    // we encounter blue. At that point, we can save the FontColor along with the index
    // This index will allows to us to relate tokens with a specific color
    private FontColor colorHolder;

    AbstractRTFCommandsCallbackProcessor(OutputStream outputStream) throws XMLStreamException {
        this.rtfDumpListener = new RtfDumpListener(outputStream);
        this.currentToken = new VocabularyToken();
        this.colorMap = new HashMap<>();
        colorHolder = new FontColor();
    }

    // ********************************
    // Simple Delegation Calls
    // Used only to output the XML file
    // ********************************
    @Override
    public void processDocumentStart() {
        rtfDumpListener.processDocumentStart();
    }

    @Override
    public void processDocumentEnd() {
        rtfDumpListener.processDocumentEnd();
    }

    @Override
    public void processCharacterBytes(byte[] data) {
        rtfDumpListener.processCharacterBytes(data);
    }

    @Override
    public void processBinaryBytes(byte[] data) {
        rtfDumpListener.processBinaryBytes(data);
    }

    @Override
    public void processGroupStart() {
        rtfDumpListener.processGroupStart();
    }

    @Override
    public void processGroupEnd() {
        rtfDumpListener.processGroupEnd();
    }
    // ********************************
    // End of simple Delegation Calls
    // Used only to output the XML file
    // ********************************

    @Override
    public void processCommand(Command command, int parameter, boolean hasParameter, boolean optional)
    {
        rtfDumpListener.processCommand(command, parameter, hasParameter, optional);
        // Search for the commands we are currently interested in
        switch (command.getCommandName()) {
            case "red":
                colorHolder.setRed(parameter);
                break;
            case "green":
                colorHolder.setGreen(parameter);
                break;
            case "blue":
                colorHolder.setBlue(parameter);
                colorMap.put(nextColorIndex, new FontColor(colorHolder));
                nextColorIndex++;
                break;
            case "fs":
                if (hasParameter) {
                    currentToken.setTextPoints(parameter/2);
                } else {
                    logger.error("Font size set but without a parameter");
                }
                break;
            case "i":
                if (!hasParameter) {
                    currentToken.setItalicized(true);
                } else {
                    if (parameter == 0) {
                        currentToken.setItalicized(false);
                    } else {
                        logger.error("Italicized unknown parameter");
                    }
                }
                break;
            case "b":
                if (!hasParameter) {
                    currentToken.setBold(true);
                } else {
                    if (parameter == 0) {
                        currentToken.setBold(false);
                    } else {
                        logger.error("Bold unknown parameter");
                    }
                }
                break;
            case "lang":
                if (hasParameter) {
                    currentToken.setLanguage(Languages.languageFromID(parameter));
                } else {
                    logger.error("Language unknown parameter");
                }
                break;
            case "cf":
                if (hasParameter) {
                    if (parameter == 0) {
                        currentToken.setColor(FontColor.BLACK);
                    } else {
                        currentToken.setColor(colorMap.get(parameter));
                    }
                } else {
                    logger.error("Font color unknown parameter");
                }
        }
    }

    @Override
    public void processString(String token) {
        if (token == null) {
            logger.warn("Null token string. Ignoring it");
            return;
        }
        String trimmedToken = token.trim();
        if (trimmedToken.length() == 0) {
            logger.warn("Empty token string. Ignoring it");
            return;
        }
        processToken(token);
    }

    @Override
    public void reset() {
        colorMap.clear();
        nextColorIndex = 1;
    }

    protected abstract void processToken(String token);
}
