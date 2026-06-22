package com.myy803.requirements.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UseCaseService;
import com.myy803.requirements.service.UserService;

/**
 * Handles all use case related HTTP requests.
 */
@Controller
@RequestMapping("/user/projects/{projectId}/usecases")
public class UseCaseController {

    @Autowired
    private UseCaseService useCaseService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectShareService projectShareService;

    /**
     * Injected directly to load a project by ID alone (no ownership filter).
     * Used when the current user is a collaborator, not the owner.
     */
    @Autowired
    private ProjectMapper projectDao;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    /**
     * Loads the project for display after verifying the current user is
     * authorized (owner OR collaborator).
     *
     * Returns null if the user is not authorized — callers redirect to
     * /user/projects in that case.
     */
    private Project loadProject(int projectId, int userId) {
        if (!projectShareService.isUserAuthorizedForProject(projectId, userId)) {
            return null;
        }
        return projectDao.findById(projectId)
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // US9 — List all use cases for a project
    // -------------------------------------------------------------------------

    @GetMapping
    public String listUseCases(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        List<UseCase> useCases = useCaseService.getUseCasesByProject(projectId);

        model.addAttribute("project", project);
        model.addAttribute("useCases", useCases);
        model.addAttribute("user", currentUser);

        return "user/usecases/list";
    }

    // -------------------------------------------------------------------------
    // US7 — Create new use case: show form
    // -------------------------------------------------------------------------

    @GetMapping("/new")
    public String showNewUseCaseForm(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        model.addAttribute("project", project);
        model.addAttribute("useCase", new UseCase());
        model.addAttribute("actorsInput", "");
        model.addAttribute("user", currentUser);

        return "user/usecases/form";
    }

    // -------------------------------------------------------------------------
    // US7 — Create new use case: handle form submission
    // -------------------------------------------------------------------------

    @PostMapping("/save")
    public String saveUseCase(@PathVariable int projectId,
                              @ModelAttribute("useCase") UseCase useCase,
                              @RequestParam(value = "actorsInput", defaultValue = "") String actorsInput,
                              RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        useCaseService.saveUseCase(useCase, projectId, actorsInput);
        redirectAttributes.addFlashAttribute("successMessage",
                "Use case \"" + useCase.getName() + "\" created successfully!");

        return "redirect:/user/projects/" + projectId + "/usecases";
    }

    // -------------------------------------------------------------------------
    // US8 — Edit use case: show pre-populated form
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/edit")
    public String showEditUseCaseForm(@PathVariable int projectId,
                                      @PathVariable int id,
                                      Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        UseCase useCase = useCaseService.getUseCaseByIdAndProject(id, projectId);
        String actorsInput = useCaseService.getActorsAsString(useCase);

        model.addAttribute("project", project);
        model.addAttribute("useCase", useCase);
        model.addAttribute("actorsInput", actorsInput);
        model.addAttribute("user", currentUser);

        return "user/usecases/form";
    }

    // -------------------------------------------------------------------------
    // US8 — Edit use case: handle form submission
    // -------------------------------------------------------------------------

    @PostMapping("/update")
    public String updateUseCase(@PathVariable int projectId,
                                @ModelAttribute("useCase") UseCase useCase,
                                @RequestParam(value = "actorsInput", defaultValue = "") String actorsInput,
                                RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        useCaseService.saveUseCase(useCase, projectId, actorsInput);
        redirectAttributes.addFlashAttribute("successMessage",
                "Use case \"" + useCase.getName() + "\" updated successfully!");

        return "redirect:/user/projects/" + projectId + "/usecases";
    }

    // -------------------------------------------------------------------------
    // US10 — Delete use case
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/delete")
    public String deleteUseCase(@PathVariable int projectId,
                                @PathVariable int id,
                                RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        useCaseService.deleteUseCase(id, projectId);
        redirectAttributes.addFlashAttribute("successMessage", "Use case deleted successfully.");

        return "redirect:/user/projects/" + projectId + "/usecases";
    }
}