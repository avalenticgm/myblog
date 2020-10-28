package it.course.myblog.payload.request;

import java.util.Set;

import it.course.myblog.entity.AuthorityName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ChangeAuthority {
	
	private String username;
	private Set<AuthorityName> authorityNames;

}
