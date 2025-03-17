package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.controller.ConversationSessionService;
import com.tech11.retrieval.business.retrieval.entity.ConversationSession;
import com.tech11.retrieval.business.retrieval.entity.FeedbackDTO;

@RequestScoped
public class FeedbackResourceImpl implements FeedbackResource {

	@Inject
	ConversationSessionService conversationSessionService;

	@Override
	public Response submitFeedback(String sessionId, @Valid FeedbackDTO feedbackDTO) {
		final ConversationSession session = conversationSessionService.getSession(sessionId);
		if (session == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Session not found")
					.build();
		}
		conversationSessionService.updateFeedback(session, feedbackDTO);
		return Response.ok().build();
	}
}
