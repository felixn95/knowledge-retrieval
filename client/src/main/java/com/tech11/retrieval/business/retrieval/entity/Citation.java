package com.tech11.retrieval.business.retrieval.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Citation {
	// An identifier for the citation, if required
	private String id;

	// The citation text or title
	private String relatedHeading;

	private int pageNumber;

	// Optionally, a URL or link associated with the citation
	private String url;
}
