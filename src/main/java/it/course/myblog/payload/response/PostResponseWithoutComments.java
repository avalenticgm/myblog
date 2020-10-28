package it.course.myblog.payload.response;

import java.util.Date;
import java.util.List;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

public class PostResponseWithoutComments {
	private Long id;
	private String title;
	private String overview;
	private String content;
	private Date updatedAt;
	private String author;
	private String language;
	private String dbFileName;
	
	public static PostResponseWithoutComments createFromEntity(Post p) {
		return new PostResponseWithoutComments(
				p.getId(),
				p.getTitle(),
				p.getOverview(),
				p.getContent(),
				p.getUpdatedAt(),
				p.getAuthor().getUsername(),
				p.getLanguage().getDescription(),
				p.getDbfile().getFileName());
	}
}
