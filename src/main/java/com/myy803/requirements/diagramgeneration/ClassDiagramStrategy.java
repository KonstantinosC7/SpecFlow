package com.myy803.requirements.diagramgeneration;


import java.util.List;
 
import com.myy803.requirements.model.CRCCard;
 

public interface ClassDiagramStrategy {
 
    /**
     * Generates a complete class diagram script from a list of CRC cards.
     *
     * @param crcCards the CRC cards of a project
     * @return the full script string ready to paste into PlantUML / Nomnoml
     */
    String generateScript(List<CRCCard> crcCards);
}
