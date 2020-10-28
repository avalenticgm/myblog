package it.course.myblog.payload.response;

import java.util.Date;

import it.course.myblog.entity.Advisory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

public class AdvisoryResponse {
	private Long commentId;
	private String comment;
	private Long reporterId;
	private String reporter;
	private Long authorId;
	private String author;
	private String reason;
	private Boolean status;
	private Date updatedAt;
	private String title;
	
	public static AdvisoryResponse createFromEntity(Advisory a) {
		return new AdvisoryResponse(
				a.getAdvisoryId().getCommentId().getId(),
				a.getAdvisoryId().getCommentId().getComment(),
				a.getAdvisoryId().getUserId().getId(),
				a.getAdvisoryId().getUserId().getUsername(),
				a.getAdvisoryId().getCommentId().getCommentAuthor().getId(),
				a.getAdvisoryId().getCommentId().getCommentAuthor().getUsername(),
				a.getReason().toString(),
				a.getStatus(),
				a.getUpdatedAt(),
				"titolo");
	}
}