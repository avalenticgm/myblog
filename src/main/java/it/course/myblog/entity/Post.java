package it.course.myblog.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="POST")
@Data @AllArgsConstructor @NoArgsConstructor
public class Post {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, unique=true, length=80)
	private String title;
	
	@Column(nullable=true)
	private String overview;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String content;
	
	@Column(name="CREATED_AT",
			updatable=false, insertable=false,
			columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;
	
	@Column(name="UPDATED_AT",
			updatable=true, insertable=false,
			columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedAt;
	
	@Column(name="IS_APPROVED", columnDefinition="TINYINT(1)")
	private Boolean approved = false;
	
	@Column(name="IS_VISIBLE", columnDefinition="TINYINT(1)")
	private Boolean visible = false;
	
	@ManyToOne
	@JoinColumn(name="USER_ID", nullable=false)
	private User author;
	
	@ManyToOne
	@JoinColumn(name="LANGUAGE_CODE", nullable=false)
	private Language language;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="POST_TAGS",
				joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
				inverseJoinColumns = {@JoinColumn(name="TAG_ID", referencedColumnName="ID")})
	private Set<Tag> tags;
	
	@ManyToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinTable(name="POST_COMMENTS",
		joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="COMMENT_ID", referencedColumnName="ID")})
	private List<Comment> comments; //trasformare in lista

	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="POST_IMAGE", nullable=false)
	private DBFile dbfile;
	
	public Post(String title, String overview, String content, User author, Language language) {
		super();
		this.title = title;
		this.overview = overview;
		this.content = content;
		this.author = author;
		this.language = language;
	}
	
	public Post(String title, String overview, String content, User author, Language language, DBFile dbfile) {
		super();
		this.title = title;
		this.overview = overview;
		this.content = content;
		this.author = author;
		this.language = language;
		this.dbfile = dbfile;
	}
	
	
	
}
