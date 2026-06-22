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

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.Comment;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;
import com.myy803.requirements.model.User;
import com.myy803.requirements.service.CRCCardService;
import com.myy803.requirements.service.CommentService;
import com.myy803.requirements.service.ProjectService;
import com.myy803.requirements.service.ProjectShareService;
import com.myy803.requirements.service.UseCaseService;
import com.myy803.requirements.service.UserService;

/**
 * Handles comment requests (US19).
 *
 * === URL STRUCTURE ===
 *
 * Use case comments:
 *   GET  /user/projects/{projectId}/usecases/{ucId}/comments
 *   POST /user/projects/{projectId}/usecases/{ucId}/comments/add
 *
 * CRC card comments:
 *   GET  /user/projects/{projectId}/crccards/{cardId}/comments
 *   POST /user/projects/{projectId}/crccards/{cardId}/comments/add
 *
 * === ACCESS CONTROL ===
 * Both the project owner and collaborators (US18) can post and view comments.
 * projectShareService.isUserAuthorizedForProject() is the single authorization
 * check — it returns true for owners AND collaborators.
 *
 * === loadProject() HELPER ===
 * Collaborators are not the project owner, so projectService.getProjectByIdAndUser()
 * would throw for them. Instead, for display purposes (breadcrumbs, page title),
 * we load the project directly via the ProjectMapper DAO using only the projectId.
 * Authorization was already verified by isUserAuthorizedForProject() before this call.
 */
@Controller
@RequestMapping("/user/projects/{projectId}")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectShareService projectShareService;

    @Autowired
    private UseCaseService useCaseService;

    @Autowired
    private CRCCardService crcCardService;

    @Autowired
    private UserService userService;

    /**
     * Injecting ProjectMapper directly here so we can load the project
     * for display even when the current user is a collaborator (not the owner).
     * Named projectDao to avoid any naming conflict with ProjectService.
     */
    @Autowired
    private ProjectMapper projectDao;

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    /**
     * Checks authorization and returns the project for display (breadcrumb/title).
     * Works for both owners and collaborators.
     * Returns null if the user is not authorized.
     */
    private Project loadAuthorizedProject(int projectId, int userId) {
        if (!projectShareService.isUserAuthorizedForProject(projectId, userId)) {
            return null; // caller will redirect to /user/projects
        }
        return projectDao.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
    }

    // =========================================================================
    // USE CASE COMMENTS — US19
    // =========================================================================

    /**
     * GET — shows the comment thread for a use case.
     * Loads the use case (verifies it belongs to projectId) and all its comments.
     */
    @GetMapping("/usecases/{ucId}/comments")
    public String showUseCaseComments(@PathVariable int projectId,
                                       @PathVariable int ucId,
                                       Model model) {

        User currentUser = getCurrentUser();
        Project project = loadAuthorizedProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects"; // not authorized
        }

        // Load the use case — verifies it belongs to this project
        UseCase useCase = useCaseService.getUseCaseByIdAndProject(ucId, projectId);

        // All comments on this use case, oldest first
        List<Comment> comments = commentService.getCommentsForUseCase(ucId);

        model.addAttribute("project", project);
        model.addAttribute("useCase", useCase);
        model.addAttribute("comments", comments);
        model.addAttribute("user", currentUser);

        return "user/usecases/comments"; // -> templates/user/usecases/comments.html
    }

    /**
     * POST — saves a new comment on a use case.
     * Validates that the text is not empty before saving.
     * redirects back to the comments page after saving.
     */
    @PostMapping("/usecases/{ucId}/comments/add")
    public String addUseCaseComment(@PathVariable int projectId,
                                     @PathVariable int ucId,
                                     @RequestParam String text,
                                     RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();

        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        if (text == null || text.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Comment text cannot be empty.");
        } else {
            commentService.addCommentToUseCase(ucId, currentUser.getId(), text.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Comment added.");
        }

        return "redirect:/user/projects/" + projectId + "/usecases/" + ucId + "/comments";
    }

    // =========================================================================
    // CRC CARD COMMENTS — US19
    // =========================================================================

    /**
     * GET — shows the comment thread for a CRC card.
     */
    @GetMapping("/crccards/{cardId}/comments")
    public String showCRCCardComments(@PathVariable int projectId,
                                       @PathVariable int cardId,
                                       Model model) {

        User currentUser = getCurrentUser();
        Project project = loadAuthorizedProject(projectId, currentUser.getId());

        if (project == null) {
            return "redirect:/user/projects";
        }

        // Load the CRC card — verifies it belongs to this project
        CRCCard crcCard = crcCardService.getCRCCardByIdAndProject(cardId, projectId);

        // All comments on this CRC card, oldest first
        List<Comment> comments = commentService.getCommentsForCRCCard(cardId);

        model.addAttribute("project", project);
        model.addAttribute("crcCard", crcCard);
        model.addAttribute("comments", comments);
        model.addAttribute("user", currentUser);

        return "user/crccards/comments"; // -> templates/user/crccards/comments.html
    }

    /**
     * POST — saves a new comment on a CRC card.
     */
    @PostMapping("/crccards/{cardId}/comments/add")
    public String addCRCCardComment(@PathVariable int projectId,
                                     @PathVariable int cardId,
                                     @RequestParam String text,
                                     RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();

        if (!projectShareService.isUserAuthorizedForProject(projectId, currentUser.getId())) {
            return "redirect:/user/projects";
        }

        if (text == null || text.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Comment text cannot be empty.");
        } else {
            commentService.addCommentToCRCCard(cardId, currentUser.getId(), text.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Comment added.");
        }

        return "redirect:/user/projects/" + projectId + "/crccards/" + cardId + "/comments";
    }
}
