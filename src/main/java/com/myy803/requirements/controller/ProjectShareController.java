package com.myy803.requirements.controller;

import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.ProjectShare;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UserService;

/**
 * Handles project sharing requests (US18).
 *
 * === URL STRUCTURE ===
 * GET  /user/projects/{projectId}/share        -> show share management page
 * POST /user/projects/{projectId}/share/add    -> add a new collaborator
 * GET  /user/projects/{projectId}/share/{shareId}/remove -> remove a collaborator
 */
@Controller
@RequestMapping("/user/projects/{projectId}/share")
public class ProjectShareController {

    @Autowired
    private ProjectShareService projectShareService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    // -------------------------------------------------------------------------
    // GET — show share management page
    // -------------------------------------------------------------------------

    /**
     * Loads the project (ownership check) and lists all current collaborators.
     * Only the owner can see and manage this page.
     */
    @GetMapping
    public String showSharePage(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();

        // getProjectByIdAndUser throws if currentUser is not the owner
        Project project = projectService.getProjectByIdAndUser(projectId, currentUser.getId());

        // Load all current shares for this project
        List<ProjectShare> shares = projectShareService.getSharesByProject(projectId);

        model.addAttribute("project", project);
        model.addAttribute("shares", shares);
        model.addAttribute("user", currentUser);

        return "user/projects/share"; // → templates/user/projects/share.html
    }

    // -------------------------------------------------------------------------
    // POST — add a new collaborator
    // -------------------------------------------------------------------------

    /**
     * Adds the user identified by targetUsername as a collaborator.
     *
     * Errors (user not found, already shared) are caught and shown
     * as flash error messages — the user stays on the share page.
     */
    @PostMapping("/add")
    public String addShare(@PathVariable int projectId,
                           @RequestParam String targetUsername,
                           RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();

        try {
            projectShareService.shareProject(projectId, currentUser.getId(), targetUsername);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Project shared with " + targetUsername + " successfully!");
        } catch (RuntimeException e) {
            // Show the error message from the service
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/projects/" + projectId + "/share";
    }

    // -------------------------------------------------------------------------
    // GET — remove a collaborator
    // -------------------------------------------------------------------------

    /**
     * Removes the share record with the given shareId.
     * The service verifies the current user is the project owner.
     */
    @GetMapping("/{shareId}/remove")
    public String removeShare(@PathVariable int projectId,
                              @PathVariable int shareId,
                              RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();

        try {
            projectShareService.removeShare(shareId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Collaborator removed successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/projects/" + projectId + "/share";
    }
}
