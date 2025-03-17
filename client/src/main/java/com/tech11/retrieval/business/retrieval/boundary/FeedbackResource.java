package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.tech11.retrieval.business.retrieval.entity.FeedbackDTO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FeedbackResource {

	/**
	 * Submits feedback for a conversation session.
	 *
	 * @param sessionId
	 *     the session identifier
	 * @param feedbackDTO
	 *     the feedback data
	 * @return Response indicating success or failure
	 */
	@POST
	@Path("/feedback")
	Response submitFeedback(@QueryParam("sessionId") String sessionId, @Valid FeedbackDTO feedbackDTO);
}