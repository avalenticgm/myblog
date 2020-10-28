package it.course.myblog.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="COMMENT")
@Data @AllArgsConstructor @NoArgsConstructor

public class Comment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String comment;
	
	@Column(name="CREATED_AT",
			updatable=false, insertable=false,
			columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;
	
	@Column(name="IS_VISIBLE", columnDefinition="TINYINT(1)")
	private Boolean visible = true;
	
	@ManyToOne
	@JoinColumn(name="USER_ID", nullable=false)
	private User commentAuthor;
	
	

	public Comment(String comment, User commentAuthor) {
		super();
		this.comment = comment;
		this.commentAuthor = commentAuthor;
	}

	
}
