package edu.teikav.robot.parser.services;

import java.io.File;

public interface VocabularyParsingService {

    /**
     * Parses input document representing encoded (to whatever encoding scheme) vocabulary. This method assumes that
     * the Vocabulary Publisher is unknown so the system must identify Publisher's format first
     * @param vocabularyDocument input encoded document (eg. RTF-encoding)
     */
    void parseVocabulary(String vocabularyDocument);

    /**
     * Parses input document representing encoded (to whatever encoding scheme) vocabulary
     * @param vocabularyDocument input encoded document (eg. RTF-encoding)
     */
    void parseVocabulary(String vocabularyDocument, String publisher);

    /**
     * Parses input document representing encoded (to whatever encoding scheme) vocabulary. This method assumes that
     *      * the Vocabulary Publisher is unknown so the system must identify Publisher's format first
     * @param vocabularyDocument input encoded document (eg. RTF-encoding)
     */
    void parseVocabulary(File vocabularyDocument);
}
