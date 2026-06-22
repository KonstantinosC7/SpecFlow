package com.myy803.requirements.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.DiagramService;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UserService;

/**
 * Handles diagram generation requests (US15, US16).
 */
@Controller
@RequestMapping("/user/projects/{projectId}/diagrams")
public class DiagramController {

    @Autowired
    private DiagramService diagramService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectShareService projectShareService;

    @Autowired
    private ProjectMapper projectDao;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    /**
     * Checks authorization and loads the project.
     * Works for both owners and collaborators.
     * Returns null if the user has no access.
     */
    private Project loadProject(int projectId, int userId) {
        if (!projectShareService.isUserAuthorizedForProject(projectId, userId)) {
            return null;
        }
        return projectDao.findById(projectId).orElse(null);
    }

    // -------------------------------------------------------------------------
    // GET — show the diagram selection form
    // -------------------------------------------------------------------------

    @GetMapping
    public String showDiagramPage(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        model.addAttribute("project", project);
        model.addAttribute("user", currentUser);

        return "user/diagrams/generate";
    }

    // -------------------------------------------------------------------------
    // POST — generate and display the script
    // -------------------------------------------------------------------------

    @PostMapping("/generate")
    public String generateDiagram(@PathVariable int projectId,
                                   @RequestParam String diagramType,
                                   @RequestParam String tool,
                                   Model model) {

        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        model.addAttribute("project", project);
        model.addAttribute("user", currentUser);
        model.addAttribute("selectedType", diagramType);
        model.addAttribute("selectedTool", tool);

        try {
            String script;
            if ("usecase".equals(diagramType)) {
                script = diagramService.generateUseCaseDiagram(projectId, tool);
            } else {
                script = diagramService.generateClassDiagram(projectId, tool);
            }
            model.addAttribute("generatedScript", script);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Generation failed: " + e.getMessage());
        }

        return "user/diagrams/generate";
    }
}