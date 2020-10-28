package it.course.myblog.payload.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class  SignInRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull
    @Size(min = 4, max = 50)
	private String username;
	
	@NotNull
    @Size(min = 4, max = 100)
    private String password;

}