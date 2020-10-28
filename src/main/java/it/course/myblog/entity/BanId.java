package it.course.myblog.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor

public class BanId implements Serializable {	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userId;
}
