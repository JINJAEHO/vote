package com.wogh.vote.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_member")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "follows")
public class Member extends BaseEntity{
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long mno;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "nickname")
	private String nickname;
	
	@Column(name = "officialmark", columnDefinition = "boolean default false")
	private boolean officialmark;
	
	@OneToMany(mappedBy = "fmember", fetch = FetchType.LAZY)
	private List<Follow> follows;

	public void changeMember(String password, String name, String nickname, boolean officialmark) {
		if(password != null) {
			String pw = BCrypt.hashpw(password, BCrypt.gensalt());
			this.password = pw;
		}
		if(name != null) this.name = name;
		if(nickname != null) this.nickname = nickname;
		if(officialmark != this.officialmark) this.officialmark = officialmark;
	}
}
