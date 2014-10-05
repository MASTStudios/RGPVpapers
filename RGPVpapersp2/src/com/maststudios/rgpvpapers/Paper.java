package com.maststudios.rgpvpapers;

public class Paper {
	
	private String heading,details,year;
	private boolean isDownloaded;
	private int id;
	
	public int getId(){
		return id;
	}
	
	public String getHeading() {
		return heading;
	}

	public String getDetails() {
		return details;
	}

	public String getYear() {
		return year;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public Paper(int i,String h,String d,String y,boolean dow){
		heading=h;
		details=d;
		year=y;
		isDownloaded=dow;
		id=i;
	}
		
//	String id,paperYear,uri, localUri, subjectName, subjectCode, branch,paperSource,link;
//
//	/**
//	 * @param id
//	 * @param paperYear
//	 * @param uri
//	 * @param localUri
//	 * @param subjectName
//	 * @param subjectCode
//	 * @param branch
//	 * @param paperSource
//	 * @param link
//	 */
//	public Paper(String id, String paperYear, String uri, String localUri,
//			String subjectName, String subjectCode, String branch,
//			String paperSource, String link) {
//		super();
//		this.id = id;
//		this.paperYear = paperYear;
//		this.uri = uri;
//		this.localUri = localUri;
//		this.subjectName = subjectName;
//		this.subjectCode = subjectCode;
//		this.branch = branch;
//		this.paperSource = paperSource;
//		this.link = link;
//	}
	
		
}
