package it.course.myblog.payload.response;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

public class AverageRateResponse {
	private Long id;
	private String title;
	private Double average;
	
	public static AverageRateResponse createFromEntity(Post p,Double avg) {
		return new AverageRateResponse(
				p.getId(),
				p.getTitle(),
				avg);
	}
}
