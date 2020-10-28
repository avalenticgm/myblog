package it.course.myblog.payload.response;

import java.util.Date;
import java.util.List;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

public class PostResponse {
	private Long id;
	private String title;
	private String overview;
	private String content;
	private Date updatedAt;
	private String author;
	private String language;
	private String dbFileName;
	private List<String> comments;
	
	public static PostResponse createFromEntity(Post p,List<String> comments) {
		return new PostResponse(
				p.getId(),
				p.getTitle(),
				p.getOverview(),
				p.getContent(),
				p.getUpdatedAt(),
				p.getAuthor().getUsername(),
				p.getLanguage().getDescription(),
				p.getDbfile().getFileName(),
				comments);
	}
	
	public static PostResponse mergePostComments(PostResponseWithoutComments prwc,List<String> comments) {
		return new PostResponse(
				prwc.getId(),
				prwc.getTitle(),
				prwc.getOverview(),
				prwc.getContent(),
				prwc.getUpdatedAt(),
				prwc.getAuthor(),
				prwc.getLanguage(),
				prwc.getDbFileName(),
				comments);
	}
}
