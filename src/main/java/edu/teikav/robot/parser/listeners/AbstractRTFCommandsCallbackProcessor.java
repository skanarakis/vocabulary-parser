package edu.teikav.robot.parser.listeners;

import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.utils.RtfDumpListener;

import edu.teikav.robot.parser.domain.FontColor;
import edu.teikav.robot.parser.domain.Languages;
import edu.teikav.robot.parser.domain.RTFParserCallbackProcessor;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.util.TokenUtils;

public abstract class AbstractRTFCommandsCallbackProcessor implements RTFParserCallbackProcessor {

    private Logger logger = LoggerFactory.getLogger(AbstractRTFCommandsCallbackProcessor.class);

    private static final String START_OF_BOOKMARK_COMMAND = "bkmkstart";
    private static final String END_OF_BOOKMARK_COMMAND = "bkmkend";
    private static final String COLOR_TABLE_COMMAND = "colortbl";
    private static final String PICTURE_COMMAND = "pict";
    private static final String COLOR_FONT_COMMAND = "cf";
    private static final String LANGUAGE_COMMAND = "lang";
    private static final String BOLD_COMMAND = "b";
    private static final String ITALICS_COMMAND = "i";
    private static final String FONT_SIZE_COMMAND = "fs";
    private static final String RED_COLOR_COMMAND = "red";
    private static final String GREEN_COLOR_COMMAND = "green";
    private static final String BLUE_COLOR_COMMAND = "blue";
    private static final String FONT_TABLE_COMMAND = "fonttbl";
    private static final String STYLESHEET_COMMAND = "stylesheet";
    private static final String LIST_TABLE_COMMAND = "listtable";
    private static final String LIST_OVERRIDE_TABLE_COMMAND = "listoverridetable";
    private static final String INFO_COMMAND = "info";
    private static final String SHAPE_COMMAND = "shp";
    private static final String GENERATOR_COMMAND = "generator";

    private static final String ENTERED_GROUP = "Group Entered";

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

    private enum RtfCommandScope {
        IN_SCOPE,
        OUT_OF_SCOPE
    }
    private static class CommandHelperAttributes {
        RtfCommandScope scope;
        int nestedDepth;
    }

    private Deque<String> nestedGroupCommandsQueue;
    private Map<String, CommandHelperAttributes> rtfCommandsMap;

    // Flags
    private boolean pictureCharactersComingNext;
    private boolean headerComingNext;

    AbstractRTFCommandsCallbackProcessor(OutputStream outputStream) throws XMLStreamException {
        this.rtfDumpListener = new RtfDumpListener(outputStream);
        this.currentToken = new VocabularyToken();
        this.colorMap = new HashMap<>();
        colorHolder = new FontColor();

        nestedGroupCommandsQueue = new ArrayDeque<>();
        rtfCommandsMap = new HashMap<>();
    }

    // ********************************
    // Simple Delegation Calls
    // Used only to output the XML file
    // ********************************
    @Override
    public void processDocumentStart() { rtfDumpListener.processDocumentStart(); }

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
        nestedGroupCommandsQueue.addLast(ENTERED_GROUP);
    }

    @Override
    public void processGroupEnd() {
        rtfDumpListener.processGroupEnd();
        nestedGroupCommandsQueue.removeLast();
        if (0 < rtfCommandsMap.size()) {
            checkIfStillInScope(nestedGroupCommandsQueue.size());
        }
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
        String commandName = command.getCommandName();
        switch (commandName) {
            case RED_COLOR_COMMAND:
                colorHolder.setRed(parameter);
                break;
            case GREEN_COLOR_COMMAND:
                colorHolder.setGreen(parameter);
                break;
            case BLUE_COLOR_COMMAND:
                colorHolder.setBlue(parameter);
                colorMap.put(nextColorIndex, new FontColor(colorHolder));
                nextColorIndex++;
                break;
            case FONT_SIZE_COMMAND:
                if (hasParameter) {
                    currentToken.setTextPoints(parameter/2);
                } else {
                    logger.error("Font size set but without a parameter");
                }
                break;
            case ITALICS_COMMAND:
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
            case BOLD_COMMAND:
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
            case LANGUAGE_COMMAND:
                if (hasParameter) {
                    currentToken.setLanguage(Languages.languageFromID(parameter));
                } else {
                    logger.error("Language unknown parameter");
                }
                break;
            case COLOR_FONT_COMMAND:
                if (hasParameter) {
                    if (parameter == 0) {
                        currentToken.setColor(FontColor.BLACK);
                    } else {
                        currentToken.setColor(colorMap.get(parameter));
                    }
                } else {
                    logger.error("Font color unknown parameter");
                }
                break;
            case PICTURE_COMMAND:
                pictureCharactersComingNext = true;
                break;
            case START_OF_BOOKMARK_COMMAND:
                headerComingNext = true;
                break;
            case END_OF_BOOKMARK_COMMAND:
                headerComingNext = false;
                break;
            case COLOR_TABLE_COMMAND:
            case FONT_TABLE_COMMAND:
            case STYLESHEET_COMMAND:
            case LIST_TABLE_COMMAND:
            case LIST_OVERRIDE_TABLE_COMMAND:
            case INFO_COMMAND:
            case SHAPE_COMMAND:
            case GENERATOR_COMMAND:
                CommandHelperAttributes attributes = new CommandHelperAttributes();
                attributes.nestedDepth = nestedGroupCommandsQueue.size();
                attributes.scope = RtfCommandScope.IN_SCOPE;
                rtfCommandsMap.put(commandName, attributes);
                break;
        }
    }

    @Override
    public void processString(String token) {

        if (token == null) {
            logger.warn("Null token string. Ignoring it");
            return;
        }

        String trimmedToken = token.trim();

        if (TokenUtils.isDegenerate(trimmedToken) || TokenUtils.isDigitsOnly(trimmedToken))
            return;

        if (isImportantToken(trimmedToken)) {
            String cleanToken = TokenUtils.removeNumericDigitsBeforeToken(trimmedToken);
            logger.debug("Processing token {}", cleanToken);
            processToken(cleanToken);
        } else {
            logger.debug("Ignoring unimportant token {}", trimmedToken);
        }
    }

    @Override
    public void reset() {
        colorMap.clear();
        nextColorIndex = 1;
    }

    protected abstract void processToken(String token);

    private void checkIfStillInScope(int currentNestingDepth) {
        List<Map.Entry<String, CommandHelperAttributes>> commandsInScope = rtfCommandsMap.entrySet().stream()
                .filter(e -> e.getValue().scope == RtfCommandScope.IN_SCOPE).collect(Collectors.toList());
        if (commandsInScope.size() > 1) {
            logger.debug("All Commands are {}", rtfCommandsMap.keySet());
            logger.debug("Commands in scope are {}", commandsInScope);
            throw new RuntimeException("Only one command should be in scope.Debug!!");
        }
        if (commandsInScope.size() == 1) {
            String commandInScope = commandsInScope.get(0).getKey();
            CommandHelperAttributes attributes = rtfCommandsMap.get(commandInScope);
            // If current nesting depth is less than the stored command's depth, then we exited the command.
            // We need to invalidate its scope
            if (attributes.nestedDepth > currentNestingDepth) {
                attributes.scope = RtfCommandScope.OUT_OF_SCOPE;
            }
        }
    }

    private boolean isImportantToken(String trimmedToken) {
        if (pictureCharactersComingNext) {
            pictureCharactersComingNext = false;
            return false;
        }

        return !headerComingNext &&
                !trimmedToken.startsWith("bookmark") &&
                rtfCommandsMap.entrySet().stream()
                        .noneMatch(e -> e.getValue().scope == RtfCommandScope.IN_SCOPE);
    }
}
