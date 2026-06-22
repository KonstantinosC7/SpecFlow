package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.myy803.requirements.model.CRCCard;

public abstract class AbstractClassDiagramGenerator implements ClassDiagramStrategy {

    /**
     * The fixed algorithm — final, cannot be overridden by subclasses.
     */
    @Override
    public final String generateScript(List<CRCCard> crcCards) {
        // Build a set of all known class names once — used in step 3
        Set<String> knownClassNames = crcCards.stream()
                .map(CRCCard::getClassName)
                .collect(Collectors.toSet());

        return generateHeader()
             + generateClasses(crcCards)
             + generateAssociations(crcCards, knownClassNames)
             + generateFooter();
    }

    // -------------------------------------------------------------------------
    // Abstract steps
    // -------------------------------------------------------------------------

    protected abstract String generateHeader();

    /** Produces one class block for each CRC card */
    protected abstract String generateClasses(List<CRCCard> crcCards);

    /**
     * Produces association lines.
     * @param crcCards        the full list of CRC cards
     * @param knownClassNames set of className values — used to validate collaborations
     */
    protected abstract String generateAssociations(List<CRCCard> crcCards,
                                                    Set<String> knownClassNames);

    protected abstract String generateFooter();

    // -------------------------------------------------------------------------
    // Shared helper
    // -------------------------------------------------------------------------

    /**
     * Parses the collaborations free-text field and returns only the
     * entries that match a known class name in the project.
     *
     * Split logic: splits on comma OR newline, then trims whitespace.
     * Example input: "AuthService, UserRepository\nSessionManager"
     * Result (assuming all three are known classes): ["AuthService",
     * "UserRepository", "SessionManager"]
     */
    protected List<String> buildAssociations(String collaborations,
                                              Set<String> knownClassNames) {
        if (collaborations == null || collaborations.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Split on comma or newline, trim each token
        return Arrays.stream(collaborations.split("[,\\n]+"))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .filter(knownClassNames::contains)   // only include known classes
                .collect(Collectors.toList());
    }

    /**
     * Splits the responsibilities text into individual lines.
     * Each non-empty line becomes a member entry inside the class block.
     */
    protected List<String> splitResponsibilities(String responsibilities) {
        if (responsibilities == null || responsibilities.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(responsibilities.split("[\\n]+"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }
}
