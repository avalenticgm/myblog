package it.course.myblog.payload.response;

import it.course.myblog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor

public class EditorAverageRateResponse {
	private String editor;
	private Double average;
	
	public static EditorAverageRateResponse createFromEntity(User e,Double avg) {
		return new EditorAverageRateResponse(
				e.getUsername(),
				avg);
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}
}