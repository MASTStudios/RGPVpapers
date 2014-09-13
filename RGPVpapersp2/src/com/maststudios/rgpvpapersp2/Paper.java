package com.maststudios.rgpvpapersp2;

public class Paper {
	
	String id,paperYear,uri, localUri, subjectName, subjectCode, branch,paperSource,link;

	/**
	 * @param id
	 * @param paperYear
	 * @param uri
	 * @param localUri
	 * @param subjectName
	 * @param subjectCode
	 * @param branch
	 * @param paperSource
	 * @param link
	 */
	public Paper(String id, String paperYear, String uri, String localUri,
			String subjectName, String subjectCode, String branch,
			String paperSource, String link) {
		super();
		this.id = id;
		this.paperYear = paperYear;
		this.uri = uri;
		this.localUri = localUri;
		this.subjectName = subjectName;
		this.subjectCode = subjectCode;
		this.branch = branch;
		this.paperSource = paperSource;
		this.link = link;
	}
	
		
}
