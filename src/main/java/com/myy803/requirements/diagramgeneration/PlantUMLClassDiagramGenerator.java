package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.myy803.requirements.model.CRCCard;

public class PlantUMLClassDiagramGenerator extends AbstractClassDiagramGenerator {

    @Override
    protected String generateHeader() {
        return "@startuml\n\n";
    }

    /**
     * Produces one class block per CRC card.
     *
     * If responsibilities text is empty, an empty class block is produced.
     * Each non-empty line of the responsibilities becomes a member entry
     * prefixed with "+ " (public visibility).
     */
    @Override
    protected String generateClasses(List<CRCCard> crcCards) {
        if (crcCards.isEmpty()) {
            return "";
        }
        List<String> blocks = new ArrayList<>();
        for (CRCCard card : crcCards) {
            List<String> members = splitResponsibilities(card.getResponsibilities());
            String memberLines = members.isEmpty()
                    ? ""
                    : "  + " + String.join("\n  + ", members) + "\n";
            blocks.add("class " + card.getClassName() + " {\n" + memberLines + "}");
        }
        return String.join("\n\n", blocks) + "\n\n";
    }

    /**
     * Produces one association line per valid collaboration.
     * Format: SourceClass --> TargetClass
     *
     * buildAssociations() (defined in the abstract base) handles the
     * parsing of the collaborations field and the validation against
     * knownClassNames.
     */
    @Override
    protected String generateAssociations(List<CRCCard> crcCards, Set<String> knownClassNames) {
        List<String> lines = new ArrayList<>();
        for (CRCCard card : crcCards) {
            for (String target : buildAssociations(card.getCollaborations(), knownClassNames)) {
                if (!target.equals(card.getClassName())) {
                    lines.add(card.getClassName() + " --> " + target);
                }
            }
        }
        return lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
    }

    @Override
    protected String generateFooter() {
        return "\n@enduml\n";
    }
}
