package it.course.myblog.payload.response;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import it.course.myblog.entity.Authority;
import it.course.myblog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserResponse {
	
	@Email
	@NotBlank
	@Size(min=6, max=120)
	private String email;
	
	@NotBlank
	@Size(min=4, max=15)
	private String username;
	
	private String avatar;
	
	private Boolean enabled;
	
	private Set<Authority> autorithies;
	
	
	public static UserResponse createFromEntity(User user) {
		
		return new UserResponse(
			user.getEmail(),
			user.getUsername(),
			user.getDbFile().getFileName(),
			user.getEnabled(),
			user.getAuthorities()
			);		
	}

}
