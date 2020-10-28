package it.course.myblog.payload.request;

import java.util.Set;

import it.course.myblog.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @AllArgsConstructor @NoArgsConstructor
public class PostTagRequest {
	
	private Long postId;
	
	private Set<String> tagNames;
}
