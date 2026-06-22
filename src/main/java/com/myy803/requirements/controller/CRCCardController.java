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
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.CRCCardService;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UseCaseService;
import com.myy803.requirements.service.UserService;

/**
 * Handles all CRC card HTTP requests.
 */
@Controller
@RequestMapping("/user/projects/{projectId}/crccards")
public class CRCCardController {

    @Autowired
    private CRCCardService crcCardService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectShareService projectShareService;

    @Autowired
    private ProjectMapper projectDao;

    @Autowired
    private UseCaseService useCaseService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    /**
     * Checks authorization and loads the project for display.
     * Works for both project owners and collaborators.
     * Returns null if the user has no access.
     */
    private Project loadProject(int projectId, int userId) {
        if (!projectShareService.isUserAuthorizedForProject(projectId, userId)) {
            return null;
        }
        return projectDao.findById(projectId).orElse(null);
    }

    // -------------------------------------------------------------------------
    // US14 / US11 view — List all CRC cards
    // -------------------------------------------------------------------------

    @GetMapping
    public String listCRCCards(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        List<CRCCard> cards = crcCardService.getCRCCardsByProject(projectId);

        model.addAttribute("project", project);
        model.addAttribute("cards", cards);
        model.addAttribute("user", currentUser);

        return "user/crccards/list";
    }

    // -------------------------------------------------------------------------
    // US11 — Create: show form
    // -------------------------------------------------------------------------

    @GetMapping("/new")
    public String showNewCRCCardForm(@PathVariable int projectId, Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        List<UseCase> useCases = useCaseService.getUseCasesByProject(projectId);

        model.addAttribute("project", project);
        model.addAttribute("card", new CRCCard());
        model.addAttribute("useCases", useCases);
        model.addAttribute("user", currentUser);

        return "user/crccards/form";
    }

    // -------------------------------------------------------------------------
    // US11 / US13 — Create: handle form submission
    // -------------------------------------------------------------------------

    @PostMapping("/save")
    public String saveCRCCard(@PathVariable int projectId,
                              @ModelAttribute("card") CRCCard card,
                              @RequestParam(value = "linkedUseCaseIds", required = false)
                                  List<Integer> linkedUseCaseIds,
                              RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        crcCardService.saveCRCCard(card, projectId, linkedUseCaseIds);
        redirectAttributes.addFlashAttribute("successMessage",
                "CRC Card \"" + card.getClassName() + "\" created successfully!");

        return "redirect:/user/projects/" + projectId + "/crccards";
    }

    // -------------------------------------------------------------------------
    // US12 — Edit: show pre-populated form
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/edit")
    public String showEditCRCCardForm(@PathVariable int projectId,
                                      @PathVariable int id,
                                      Model model) {
        User currentUser = getCurrentUser();
        Project project = loadProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        CRCCard card = crcCardService.getCRCCardByIdAndProject(id, projectId);
        List<UseCase> useCases = useCaseService.getUseCasesByProject(projectId);

        List<Integer> linkedIds = card.getLinkedUseCases()
                .stream()
                .map(UseCase::getId)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("project", project);
        model.addAttribute("card", card);
        model.addAttribute("useCases", useCases);
        model.addAttribute("linkedIds", linkedIds);
        model.addAttribute("user", currentUser);

        return "user/crccards/form";
    }

    // -------------------------------------------------------------------------
    // US12 / US13 — Edit: handle form submission
    // -------------------------------------------------------------------------

    @PostMapping("/update")
    public String updateCRCCard(@PathVariable int projectId,
                                @ModelAttribute("card") CRCCard card,
                                @RequestParam(value = "linkedUseCaseIds", required = false)
                                    List<Integer> linkedUseCaseIds,
                                RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        crcCardService.saveCRCCard(card, projectId, linkedUseCaseIds);
        redirectAttributes.addFlashAttribute("successMessage",
                "CRC Card \"" + card.getClassName() + "\" updated successfully!");

        return "redirect:/user/projects/" + projectId + "/crccards";
    }

    // -------------------------------------------------------------------------
    // US14 — Delete
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/delete")
    public String deleteCRCCard(@PathVariable int projectId,
                                @PathVariable int id,
                                RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        crcCardService.deleteCRCCard(id, projectId);
        redirectAttributes.addFlashAttribute("successMessage", "CRC Card deleted successfully.");

        return "redirect:/user/projects/" + projectId + "/crccards";
    }
}