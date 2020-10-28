package it.course.myblog.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryRequest {
	private Long commentId;
	private String reason;
}
