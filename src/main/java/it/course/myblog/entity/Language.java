package it.course.myblog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="LANGUAGE")
@Data @AllArgsConstructor @NoArgsConstructor

public class Language {
	@Id
	@Column(name="CODE", columnDefinition="VARCHAR(3)")
	private String code;
	
	@Column(nullable=false, unique=true, length=20)
	private String description;
	
	@Column(name="IS_VISIBLE", columnDefinition="TINYINT(1)")
	private Boolean visible = true;
	
	public Language(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	
	
}
