package it.course.myblog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="TAG")
@Data @AllArgsConstructor @NoArgsConstructor

public class Tag {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable=false, unique=true, length=15)
	private String tagName;
	
	@Column(name="IS_VISIBLE", columnDefinition="TINYINT(1)")
	private Boolean visible = true;

	public Tag(String tagName) {
		super();
		this.tagName = tagName;
	}

	
	
}