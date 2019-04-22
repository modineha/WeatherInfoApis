package com.anaek.assignment.dto;

public class FileInformation {
	private String fileName;
	private String lastUpdatedDate;
	private String size;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public FileInformation(String fileName, String lastUpdatedDate, String size) {
		super();
		this.fileName = fileName;
		this.lastUpdatedDate = lastUpdatedDate;
		this.size = size;
	}
}
