package it.course.myblog.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor

public class RatePostRequest {
	@NotNull
	private Long postId;
	@NotNull
	@Max(5)
	@Min(1)
	private int rating;
}
