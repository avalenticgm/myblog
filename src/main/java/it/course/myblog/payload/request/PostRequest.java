package it.course.myblog.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostRequest {
	
	@NotBlank
	@Size(max=80, min=1)
	private String title;
	
	@Size(max=255)
	private String overview;
	
	@NotBlank
	private String content;
	
	@NotBlank
	@Size(min=3, max=3)
	private String language;
	
	

}
