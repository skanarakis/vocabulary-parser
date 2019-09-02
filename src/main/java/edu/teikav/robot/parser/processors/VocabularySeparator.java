package edu.teikav.robot.parser.processors;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.utils.RtfDumpListener;
import edu.teikav.robot.parser.domain.FontColor;
import edu.teikav.robot.parser.domain.Language;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.exceptions.InfrastructureSetupException;
import edu.teikav.robot.parser.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Stream;

public class VocabularySeparator implements IRtfListener {

    private Logger logger = LoggerFactory.getLogger(VocabularySeparator.class);

    private static final String ENTERED_GROUP = "Group Entered";

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
    private static final String SP_COMMAND = "sp";
    private static final String GENERATOR_COMMAND = "generator";

    // One instance of this class will be holding information for each consecutive token
    // extracted from RTF document
    private VocabularyToken currentToken;

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

    // Use delegation to preserve the XML output
    private RtfDumpListener rtfDumpListener;

    private List<VocabularyToken> vocPartsStream;

    public VocabularySeparator() {
        this(null);
    }

    public VocabularySeparator(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                this.rtfDumpListener = new RtfDumpListener(outputStream);
            } catch (XMLStreamException e) {
                throw new InfrastructureSetupException(e);
            }
        }
        this.currentToken = new VocabularyToken();
        this.colorMap = new HashMap<>();
        colorHolder = new FontColor();

        nestedGroupCommandsQueue = new ArrayDeque<>();
        rtfCommandsMap = new HashMap<>();
        vocPartsStream = new LinkedList<>();
    }

    // ********************************
    // Simple Delegation Calls
    // Used only to output the XML file
    // ********************************
    @Override
    public void processDocumentStart() {
        if (rtfDumpListener != null) {
            rtfDumpListener.processDocumentStart();
        }
    }

    @Override
    public void processDocumentEnd() {
        if (rtfDumpListener != null) {
            rtfDumpListener.processDocumentEnd();
        }
    }

    @Override
    public void processCharacterBytes(byte[] data) {
        if (rtfDumpListener != null) {
            rtfDumpListener.processCharacterBytes(data);
        }
    }

    @Override
    public void processBinaryBytes(byte[] data) {
        if (rtfDumpListener != null) {
            rtfDumpListener.processBinaryBytes(data);
        }
    }

    // ********************************
    // End of simple Delegation Calls
    // ********************************

    @Override
    public void processGroupStart() {
        if (rtfDumpListener != null) {
            rtfDumpListener.processGroupStart();
        }
        nestedGroupCommandsQueue.addLast(ENTERED_GROUP);
    }

    @Override
    public void processGroupEnd() {
        if (rtfDumpListener != null) {
            rtfDumpListener.processGroupEnd();
        }
        nestedGroupCommandsQueue.removeLast();
        if (0 < rtfCommandsMap.size()) {

            long inScopeCommands = rtfCommandsMap.entrySet().stream()
                    .filter(e -> e.getValue().scope == RtfCommandScope.IN_SCOPE).count();

            if (inScopeCommands > 1) {
                throw new RuntimeException(String.format("Too many RTF commands in scope. \n%s", printCommands()));
            }

            Optional<Map.Entry<String, CommandHelperAttributes>> inScopeCommand = rtfCommandsMap.entrySet().stream()
                    .filter(e -> e.getValue().scope == RtfCommandScope.IN_SCOPE)
                    .findFirst();
            inScopeCommand.ifPresent(e -> recalculateScope(e.getKey(), nestedGroupCommandsQueue.size()));
        }
    }

    private String printCommands() {
        StringBuilder sb = new StringBuilder();
        rtfCommandsMap.forEach((key, value) -> sb.append(key)
                .append("/")
                .append(value.scope)
                .append("/")
                .append(value.nestedDepth)
                .append(" "));
        return sb.toString();
    }

    @Override
    public void processCommand(Command command, int parameter, boolean hasParameter, boolean optional)
    {
        if (rtfDumpListener != null) {
            rtfDumpListener.processCommand(command, parameter, hasParameter, optional);
        }

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
                    currentToken.setLanguage(Language.languageFromID(parameter));
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
            case SP_COMMAND:
            case GENERATOR_COMMAND:
                CommandHelperAttributes existingAttributes = rtfCommandsMap.get(commandName);
                if (existingAttributes == null) {
                    CommandHelperAttributes attributes = new CommandHelperAttributes();
                    attributes.nestedDepth = nestedGroupCommandsQueue.size();
                    attributes.scope = RtfCommandScope.IN_SCOPE;
                    logger.trace("Command: {}, Scope: IN_SCOPE, Nested-Depth: {}", command, attributes.nestedDepth);
                    rtfCommandsMap.put(commandName, attributes);
                } else {
                    existingAttributes.nestedDepth = nestedGroupCommandsQueue.size();
                    if (existingAttributes.scope == RtfCommandScope.OUT_OF_SCOPE) {
                        logger.trace("Scope changed from OUT_OF_SCOPE to IN_SCOPE for command {}", command);
                    }
                    existingAttributes.scope = RtfCommandScope.IN_SCOPE;
                    logger.trace("Command: {}, Scope: IN_SCOPE, Nested-Depth: {}", command, existingAttributes.nestedDepth);
                }
                break;
        }
    }

    @Override
    public void processString(String token) {

        if (rtfDumpListener != null) {
            rtfDumpListener.processString(token);
        }

        if (token == null) {
            logger.warn("Ignoring null token");
            return;
        }
        String trimmedToken = token.trim();

        if (TokenUtils.isDigitsOnly(trimmedToken) || TokenUtils.isDegenerate(trimmedToken))
            return;

        if (isImportantToken(trimmedToken)) {
            String cleanToken = TokenUtils.removeNonLetterCharsInPrefix(trimmedToken);
            logger.debug("Processing >{}<", cleanToken);
            currentToken.setValue(cleanToken);
            vocPartsStream.add(currentToken.clone());
        } else {
            logger.debug("Ignoring unimportant token {}", trimmedToken);
        }
    }

    public Stream<VocabularyToken> streamOfVocParts() {
        return vocPartsStream.stream();
    }

    public Stream<String> streamOfVocPartsValues() {
        return vocPartsStream.stream().map(VocabularyToken::getValue);
    }

    private void recalculateScope(String command, int currentNestingDepth) {

        CommandHelperAttributes attributes = rtfCommandsMap.get(command);

        // If current nesting depth is less than the stored command's depth, that means we exited the command.
        if (attributes.nestedDepth > currentNestingDepth) {
            attributes.scope = RtfCommandScope.OUT_OF_SCOPE;
        }
    }

    private boolean isImportantToken(String trimmedToken) {
        if (pictureCharactersComingNext) {
            pictureCharactersComingNext = false;
            logger.trace("Unimportant - Picture");
            return false;
        }
        if (headerComingNext) {
            logger.trace("Unimportant - Header coming");
            return false;
        }
        if (trimmedToken.startsWith("bookmark")) {
            logger.trace("Unimportant - Bookmark");
            return false;
        }

        if (rtfCommandsMap.entrySet().stream()
                .anyMatch(e -> e.getValue().scope == RtfCommandScope.IN_SCOPE)) {
            logger.trace("Unimportant - In scope of certain command\n{}", printCommands());
            return false;
        }

        return true;
    }
}
