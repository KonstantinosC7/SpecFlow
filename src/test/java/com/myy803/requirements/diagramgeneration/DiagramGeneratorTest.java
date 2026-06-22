package com.myy803.requirements.diagramgeneration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.UseCase;
import com.myy803.requirements.diagramgeneration.*;

/**
 * Unit tests for all four diagram generators and both factories (NF1.2).
 *
 * These tests verify that:
 *   1. PlantUML and Nomnoml generators produce non-empty output.
 *   2. Output contains expected keywords (actors, use cases, class names).
 *   3. The factories return the correct implementation.
 *   4. Empty input produces a valid (though minimal) script.
 *
 * No Spring context is needed — these are plain Java objects.
 * No @ExtendWith(MockitoExtension.class) either — we just instantiate
 * the generators directly via the factories.
 */
public class DiagramGeneratorTest {

    private List<UseCase> useCases;
    private List<CRCCard> crcCards;

    /**
     * Build a small but realistic dataset used by most tests.
     */
    @BeforeEach
    void setUp() {
        // --- Actors ---
        Actor customer = new Actor();
        customer.setId(1);
        customer.setName("Customer");

        Actor admin = new Actor();
        admin.setId(2);
        admin.setName("Admin");

        // --- Use Cases ---
        UseCase login = new UseCase();
        login.setId(1);
        login.setName("User Login");
        login.setActors(Arrays.asList(customer, admin));

        UseCase transfer = new UseCase();
        transfer.setId(2);
        transfer.setName("Transfer Funds");
        transfer.setActors(Arrays.asList(customer));

        useCases = Arrays.asList(login, transfer);

        // --- CRC Cards ---
        CRCCard userAccount = new CRCCard();
        userAccount.setId(1);
        userAccount.setClassName("UserAccount");
        userAccount.setResponsibilities("Stores credentials\nValidates login");
        userAccount.setCollaborations("AuthService");

        CRCCard authService = new CRCCard();
        authService.setId(2);
        authService.setClassName("AuthService");
        authService.setResponsibilities("Handles login and logout");
        authService.setCollaborations("UserAccount");

        crcCards = Arrays.asList(userAccount, authService);
    }

    // =========================================================================
    // PlantUML Use Case Diagram (US15)
    // =========================================================================

    @Test
    void plantUML_useCaseDiagram_shouldContainStartAndEndTags() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("plantuml");
        String script = strategy.generateScript(useCases);

        assertNotNull(script);
        assertTrue(script.contains("@startuml"),
                "PlantUML script must start with @startuml");
        assertTrue(script.contains("@enduml"),
                "PlantUML script must end with @enduml");
    }

    @Test
    void plantUML_useCaseDiagram_shouldDeclareActors() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("plantuml");
        String script = strategy.generateScript(useCases);

        // Both actors must appear
        assertTrue(script.contains("Customer"),
                "Script must declare the Customer actor");
        assertTrue(script.contains("Admin"),
                "Script must declare the Admin actor");
    }

    @Test
    void plantUML_useCaseDiagram_shouldDeclareUseCases() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("plantuml");
        String script = strategy.generateScript(useCases);

        assertTrue(script.contains("User Login"),
                "Script must contain 'User Login' use case");
        assertTrue(script.contains("Transfer Funds"),
                "Script must contain 'Transfer Funds' use case");
    }

    @Test
    void plantUML_useCaseDiagram_shouldContainAssociations() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("plantuml");
        String script = strategy.generateScript(useCases);

        // Associations use aliases — Customer --> UC_0, UC_1
        assertTrue(script.contains("-->"),
                "Script must contain association arrows");
    }

    // =========================================================================
    // Nomnoml Use Case Diagram (US15)
    // =========================================================================

    @Test
    void nomnoml_useCaseDiagram_shouldContainActorClassifier() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(useCases);

        assertNotNull(script);
        // Nomnoml uses [<actor> Name] syntax
        assertTrue(script.contains("<actor>"),
                "Nomnoml script must use <actor> classifier");
    }

    @Test
    void nomnoml_useCaseDiagram_shouldContainUsecaseClassifier() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(useCases);

        assertTrue(script.contains("<usecase>"),
                "Nomnoml script must use <usecase> classifier");
    }

    @Test
    void nomnoml_useCaseDiagram_shouldContainActorNames() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(useCases);

        assertTrue(script.contains("Customer"));
        assertTrue(script.contains("Admin"));
        assertTrue(script.contains("User Login"));
        assertTrue(script.contains("Transfer Funds"));
    }

    // =========================================================================
    // PlantUML Class Diagram (US16)
    // =========================================================================

    @Test
    void plantUML_classDiagram_shouldContainStartAndEndTags() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("plantuml");
        String script = strategy.generateScript(crcCards);

        assertNotNull(script);
        assertTrue(script.contains("@startuml"));
        assertTrue(script.contains("@enduml"));
    }

    @Test
    void plantUML_classDiagram_shouldDeclareClasses() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("plantuml");
        String script = strategy.generateScript(crcCards);

        assertTrue(script.contains("class UserAccount"),
                "Script must declare UserAccount class");
        assertTrue(script.contains("class AuthService"),
                "Script must declare AuthService class");
    }

    @Test
    void plantUML_classDiagram_shouldContainResponsibilityLines() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("plantuml");
        String script = strategy.generateScript(crcCards);

        // Each responsibility line becomes a "+ ..." member
        assertTrue(script.contains("Stores credentials"),
                "Responsibilities should appear inside the class block");
    }

    @Test
    void plantUML_classDiagram_shouldGenerateAssociationBetweenKnownClasses() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("plantuml");
        String script = strategy.generateScript(crcCards);

        // UserAccount collaborates with AuthService — both are known -> arrow expected
        assertTrue(script.contains("UserAccount --> AuthService"),
                "Known collaboration should produce an association arrow");
    }

    // =========================================================================
    // Nomnoml Class Diagram (US16)
    // =========================================================================

    @Test
    void nomnoml_classDiagram_shouldContainClassNames() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(crcCards);

        assertNotNull(script);
        assertTrue(script.contains("UserAccount"));
        assertTrue(script.contains("AuthService"));
    }

    @Test
    void nomnoml_classDiagram_shouldUseBracketSyntax() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(crcCards);

        // Nomnoml uses [ClassName|members] syntax
        assertTrue(script.contains("[UserAccount|"),
                "Nomnoml class node must use bracket syntax with members");
    }

    @Test
    void nomnoml_classDiagram_shouldContainArrowBetweenClasses() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(crcCards);

        // Both classes are known -> association arrow expected
        assertTrue(script.contains("[UserAccount] -> [AuthService]"),
                "Known collaboration should produce a Nomnoml arrow");
    }

    // =========================================================================
    // Edge cases — empty lists
    // =========================================================================

    /**
     * Generators should handle empty input gracefully
     * (no NullPointerException, no empty string).
     */
    @Test
    void plantUML_useCaseDiagram_shouldHandleEmptyUseCases() {
        UseCaseDiagramStrategy strategy = UseCaseDiagramFactory.create("plantuml");
        String script = strategy.generateScript(Collections.emptyList());

        assertNotNull(script);
        // At minimum the start/end tags should still be present
        assertTrue(script.contains("@startuml"));
        assertTrue(script.contains("@enduml"));
    }

    @Test
    void nomnoml_classDiagram_shouldHandleEmptyCards() {
        ClassDiagramStrategy strategy = ClassDiagramFactory.create("nomnoml");
        String script = strategy.generateScript(Collections.emptyList());

        // Should not throw — empty but valid
        assertNotNull(script);
    }

    // =========================================================================
    // Factory — unknown tool
    // =========================================================================

    /**
     * The factories must throw IllegalArgumentException for unknown tool names.
     * This is the guard that enforces the closed set of supported tools.
     */
    @Test
    void useCaseDiagramFactory_shouldThrow_forUnknownTool() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> UseCaseDiagramFactory.create("mermaid")
        );
    }

    @Test
    void classDiagramFactory_shouldThrow_forUnknownTool() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ClassDiagramFactory.create("unknown_tool")
        );
    }
}
