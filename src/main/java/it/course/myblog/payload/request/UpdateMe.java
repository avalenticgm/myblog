package it.course.myblog.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UpdateMe {
	
	@NotBlank
	@Size(min=5, max=100)
	private String password;
	
	@NotBlank
	@Size(min=4, max=15)
	private String username;

}
