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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UserService;

/**
 * Handles all project management requests.
 *
 * US4 — GET  /user/projects          -> list all projects
 * US5 — GET  /user/projects/new      -> show create form
 *        POST /user/projects/save    -> save new project
 * US5 — GET  /user/projects/{id}/edit -> show edit form
 *        POST /user/projects/update  -> save updated project
 * US6 — GET  /user/projects/{id}/delete -> delete project
 *
 * Updated for US18: listProjects() now also adds "sharedProjects"
 * to the model so the template can show the "Shared with me" section.
 */
@Controller
@RequestMapping("/user/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * US18: injected to load projects shared with the current user.
     */
    @Autowired
    private ProjectShareService projectShareService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    // -------------------------------------------------------------------------
    // US4 — List projects
    // -------------------------------------------------------------------------

    /**
     * Loads the user's own projects AND projects shared with them (US18).
     * Both lists are added to the model and rendered in the template.
     */
    @GetMapping
    public String listProjects(Model model) {
        User currentUser = getCurrentUser();

        // Own projects
        List<Project> projects = projectService.getProjectsByUser(currentUser.getId());

        // US18: projects shared with this user
        List<Project> sharedProjects =
                projectShareService.getProjectsSharedWithUser(currentUser.getId());

        model.addAttribute("projects", projects);
        model.addAttribute("sharedProjects", sharedProjects);
        model.addAttribute("user", currentUser);

        return "user/projects/list";
    }

    // -------------------------------------------------------------------------
    // US5 — Create project
    // -------------------------------------------------------------------------

    @GetMapping("/new")
    public String showNewProjectForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("user", getCurrentUser());
        return "user/projects/form";
    }

    @PostMapping("/save")
    public String saveProject(@ModelAttribute("project") Project project,
                              RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        projectService.saveProject(project, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage",
                "Project \"" + project.getName() + "\" created successfully!");
        return "redirect:/user/projects";
    }

    // -------------------------------------------------------------------------
    // US5 — Edit project
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/edit")
    public String showEditProjectForm(@PathVariable int id, Model model) {
        User currentUser = getCurrentUser();
        Project project = projectService.getProjectByIdAndUser(id, currentUser.getId());
        model.addAttribute("project", project);
        model.addAttribute("user", currentUser);
        return "user/projects/form";
    }

    @PostMapping("/update")
    public String updateProject(@ModelAttribute("project") Project project,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        projectService.saveProject(project, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage",
                "Project \"" + project.getName() + "\" updated successfully!");
        return "redirect:/user/projects";
    }

    // -------------------------------------------------------------------------
    // US6 — Delete project
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        projectService.deleteProject(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage",
                "Project deleted successfully.");
        return "redirect:/user/projects";
    }
}
