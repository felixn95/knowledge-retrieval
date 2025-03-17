package com.tech11.retrieval.business.retrieval.boundary;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import com.tech11.retrieval.business.retrieval.entity.ChatRequestDTO;
import com.tech11.retrieval.business.retrieval.entity.ChatResponseDTO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QueryResource {

	/**
	 * Process a chat message.
	 *
	 * @param request
	 *     the chat message
	 * @return the response to the chat message
	 */
	@POST
	@Path("chat")
	ChatResponseDTO processChat(ChatRequestDTO request);
}
