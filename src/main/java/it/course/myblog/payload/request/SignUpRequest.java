package it.course.myblog.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SignUpRequest {
	
	@NotNull
    @Size(min = 4, max = 50)
	private String username;
	
	@NotNull
	@Email
	@Size(max = 120)
	private String email;
	
	@NotNull
    @Size(min = 4, max = 100)
	private String password;
	
	//@NotNull
	//private boolean enabled = true;
	
}
