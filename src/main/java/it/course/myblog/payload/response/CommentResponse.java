package it.course.myblog.payload.response;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

//nome della response?
public class CommentResponse {
	private Long id;
	private String title;
	private Long commentsnumber;
	
	public static CommentResponse createFromEntity(Post p,Long commentsnumber) {
		return new CommentResponse(
				p.getId(),
				p.getTitle(),
				commentsnumber);
	}
}
