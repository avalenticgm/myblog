package it.course.myblog.payload.response;

import java.util.Date;
import java.util.Set;

import it.course.myblog.entity.Language;
import it.course.myblog.entity.Tag;
import it.course.myblog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserMeResponse {
	
	private Long id;
	private String email;
	private String username;
	private String avatar;
	private Date createdAt;
	private Date updatedAt;
	
	public static UserMeResponse createFromEntity(User user) {
		
		return new UserMeResponse(
			user.getId(),
			user.getEmail(),
			user.getUsername(),
			user.getDbFile().getFileName(),
			user.getCreatedAt(),
			user.getUpdatedAt()
			);		
	}
	
	

}
