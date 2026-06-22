package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.myy803.requirements.model.CRCCard;

public class NomnomlClassDiagramGenerator extends AbstractClassDiagramGenerator {

    @Override
    protected String generateHeader() {
        return "#direction: down\n\n";
    }

    /**
     * Produces one Nomnoml class node per CRC card.
     *
     * If responsibilities exist they are joined with ";" as required
     * by Nomnoml syntax for member separation.
     * If no responsibilities, we produce a simple [ClassName] node.
     */
    @Override
    protected String generateClasses(List<CRCCard> crcCards) {
        if (crcCards.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (CRCCard card : crcCards) {
            List<String> lines = splitResponsibilities(card.getResponsibilities());
            String inner = lines.isEmpty() ? "" : "|" + String.join(";", lines);
            parts.add("[" + card.getClassName() + inner + "]");
        }
        return String.join("\n", parts) + "\n\n";
    }

    /**
     * Produces Nomnoml association lines.
     * Format: [SourceClass] -> [TargetClass]
     */
    @Override
    protected String generateAssociations(List<CRCCard> crcCards, Set<String> knownClassNames) {
        List<String> lines = new ArrayList<>();
        for (CRCCard card : crcCards) {
            for (String target : buildAssociations(card.getCollaborations(), knownClassNames)) {
                if (!target.equals(card.getClassName())) {
                    lines.add("[" + card.getClassName() + "] -> [" + target + "]");
                }
            }
        }
        return lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
    }

    @Override
    protected String generateFooter() {
        return ""; // Nomnoml has no closing tag
    }
}
