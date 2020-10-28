package it.course.myblog.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="BAN")
@Data @AllArgsConstructor @NoArgsConstructor

public class Ban {
	@EmbeddedId
	private BanId id;

	@Column(name="ADVISORIES",columnDefinition="TINYINT(1)")
	private int advisories;
	
	@Column(name="BANNED_UNTIL",columnDefinition="DATETIME")
	private LocalDateTime bannedUntil;

	@Column(name="BANNED_DIRECTLY",columnDefinition="TINYINT(1)")
	private boolean bannedDirectly = false;

	public Ban(BanId id, int advisories, LocalDateTime bannedUntil) {
		super();
		this.id = id;
		this.advisories = advisories;
		this.bannedUntil = bannedUntil;
	}
}
