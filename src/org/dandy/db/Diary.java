package org.dandy.db;

public class Diary {

	private String title, content, category;
	private int created_at;
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public String getCategory() {
		return category;
	}
	public int getCreated_at() {
		return created_at;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public void setCreated_at(int created_at) {
		this.created_at = created_at;
	}
	
	
}
