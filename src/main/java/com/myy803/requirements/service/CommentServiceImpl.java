package com.myy803.requirements.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.requirements.dao.CRCCardMapper;
import com.myy803.requirements.dao.CommentMapper;
import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.Comment;
import com.myy803.requirements.model.User;

/**
 * Implementation of CommentService (US19).
 *
 * Both addComment methods follow the same pattern:
 *   1. Load the author (User) by userId.
 *   2. Load the target (UseCase or CRCCard) by its id.
 *   3. Build a Comment entity — one FK is set, the other stays null.
 *   4. Save the comment. Hibernate sets createdAt via @CreationTimestamp.
 *
 * The two FK fields (useCase, crcCard) are mutually exclusive:
 *   - Use case comments have useCase set, crcCard is null.
 *   - CRC card comments have crcCard set, useCase is null.
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UseCaseMapper useCaseMapper;

    @Autowired
    private CRCCardMapper crcCardMapper;

    /**
     * US19 — Adds a comment to a use case.
     */
    @Override
    public void addCommentToUseCase(int useCaseId, int authorId, String text) {

        User author = userMapper.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));

        var useCase = useCaseMapper.findById(useCaseId)
                .orElseThrow(() -> new RuntimeException("Use case not found: " + useCaseId));

        // Build the comment — crcCard is intentionally left null
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setUseCase(useCase);
        comment.setCrcCard(null);

        commentMapper.save(comment);
    }

    /**
     * US19 — Adds a comment to a CRC card.
     */
    @Override
    public void addCommentToCRCCard(int crcCardId, int authorId, String text) {

        User author = userMapper.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));

        var crcCard = crcCardMapper.findById(crcCardId)
                .orElseThrow(() -> new RuntimeException("CRC card not found: " + crcCardId));

        // Build the comment — useCase is intentionally left null
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setUseCase(null);
        comment.setCrcCard(crcCard);

        commentMapper.save(comment);
    }

    /**
     * Returns all comments for a use case, oldest first.
     */
    @Override
    public List<Comment> getCommentsForUseCase(int useCaseId) {
        return commentMapper.findByUseCaseIdOrderByCreatedAtAsc(useCaseId);
    }

    /**
     * Returns all comments for a CRC card, oldest first.
     */
    @Override
    public List<Comment> getCommentsForCRCCard(int crcCardId) {
        return commentMapper.findByCrcCardIdOrderByCreatedAtAsc(crcCardId);
    }
}
