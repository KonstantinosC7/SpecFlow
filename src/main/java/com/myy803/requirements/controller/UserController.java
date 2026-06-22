package com.myy803.requirements.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UserService;

/* Handles the main user dashboard.*/
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    /**
     * US18: injected to retrieve projects shared with the current user.
     */
    @Autowired
    private ProjectShareService projectShareService;

    @GetMapping("/user/dashboard")
    public String showDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Own projects count (existing)
        int projectCount = projectService.getProjectsByUser(currentUser.getId()).size();

        // US18: projects shared with this user
        List<Project> sharedProjects =
                projectShareService.getProjectsSharedWithUser(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("projectCount", projectCount);
        model.addAttribute("sharedProjects", sharedProjects);

        return "user/dashboard";
    }
}
