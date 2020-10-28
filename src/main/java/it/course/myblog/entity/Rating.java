package it.course.myblog.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="RATING")
@Data @AllArgsConstructor @NoArgsConstructor

public class Rating {
	@EmbeddedId
	private RatingUserPostCompositeKey ratingUserPostCompositeKey;
	
	@Column(columnDefinition="TINYINT(1)", nullable=false)
	private int rating;
}