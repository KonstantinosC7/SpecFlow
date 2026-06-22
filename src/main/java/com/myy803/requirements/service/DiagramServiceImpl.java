package com.myy803.requirements.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myy803.requirements.dao.CRCCardMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.UseCase;
import com.myy803.requirements.diagramgeneration.*;

/**
 * Implementation of DiagramService.
 *
 * === HOW THE THREE PATTERNS COME TOGETHER HERE ===
 *
 * 1. FACTORY (Parameterized):
 *    UseCaseDiagramFactory.create(tool) and ClassDiagramFactory.create(tool)
 *    return the right Strategy object based on the tool string.
 *
 * 2. STRATEGY:
 *    The returned object is typed as UseCaseDiagramStrategy /
 *    ClassDiagramStrategy — we call generateScript() on the interface.
 *    The service does not know (or care) whether it got the PlantUML
 *    or Nomnoml implementation.
 *
 * 3. TEMPLATE METHOD:
 *    Inside generateScript(), the concrete class calls the steps defined
 *    in the abstract base (header -> actors/classes -> associations -> footer).
 *    This all happens transparently — the service just calls generateScript().
 *
 * === @Transactional ===
 * Both methods are annotated @Transactional so that the JPA session
 * stays open while we access lazily-loaded collections (useCase.getActors(),
 * crcCard.getLinkedUseCases()). Without @Transactional, accessing a lazy
 * collection outside the original load call would throw
 * LazyInitializationException.
 */
@Service
public class DiagramServiceImpl implements DiagramService {

    @Autowired
    private UseCaseMapper useCaseMapper;

    @Autowired
    private CRCCardMapper crcCardMapper;

    /**
     * US15 — Generates a use case diagram.
     *
     * Step 1: Load all use cases for the project (with their actors).
     * Step 2: Ask the factory for the right strategy (plantuml / nomnoml).
     * Step 3: Call generateScript() — the strategy + template method does the rest.
     */
    @Override
    @Transactional
    public String generateUseCaseDiagram(int projectId, String tool) {
        List<UseCase> useCases = useCaseMapper.findByProjectId(projectId);

        if (useCases.isEmpty()) {
            return "// No use cases found for this project.";
        }

        // Factory creates the correct Strategy based on the tool name
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create(tool);

        // Strategy.generateScript() internally calls the Template Method steps
        return strategy.generateScript(useCases);
    }

    /**
     * US16 — Generates a class diagram from CRC cards.
     *
     * Step 1: Load all CRC cards for the project.
     * Step 2: Ask the factory for the right strategy.
     * Step 3: Call generateScript().
     */
    @Override
    @Transactional
    public String generateClassDiagram(int projectId, String tool) {
        List<CRCCard> crcCards = crcCardMapper.findByProjectId(projectId);

        if (crcCards.isEmpty()) {
            return "// No CRC cards found for this project.";
        }

        ClassDiagramStrategy strategy = ClassDiagramFactory.create(tool);

        return strategy.generateScript(crcCards);
    }
}