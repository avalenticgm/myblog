package it.course.myblog.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ADVISORY")
@Data @AllArgsConstructor @NoArgsConstructor

public class Advisory {
	
	@EmbeddedId
	private AdvisoryId advisoryId;
	
//	@ManyToOne
//	@JoinColumn(name="comment_author")
//	private User commentAuthor;

	@Enumerated(EnumType.STRING)
	private AdvisoryReason reason;
	
	@Column(name="STATUS", columnDefinition="TINYINT(1)")
	private Boolean status = false;

	@Column(name="CREATED_AT",
			updatable=false, insertable=false,
			columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;
	
	@Column(name="UPDATED_AT",
			updatable=true, insertable=false,
			columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedAt;

	public Advisory(AdvisoryId advisoryId, AdvisoryReason reason) {
		super();
		this.advisoryId = advisoryId;
		this.reason = reason;
	}
}
