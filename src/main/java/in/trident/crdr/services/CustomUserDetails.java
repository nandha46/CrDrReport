package in.trident.crdr.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;

/**
 * @author Nandhakumar Subramanian
 */

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	public String getFullName() {
		return user.getFirstName() + " " + user.getLastName();
	}

	public String getName() {
		return user.getFirstName();
	}

	public Long getId() {
		return user.getId();
	}

	public User getUser() {
		return user;
	}

	public String getAvatarType() {
		return user.getAvatarType() != null && !user.getAvatarType().trim().isEmpty()
				? user.getAvatarType()
				: "initials";
	}

	public String getAvatarValue() {
		return user.getAvatarValue();
	}

	public String getInitials() {
		String f = user.getFirstName() != null && !user.getFirstName().isEmpty()
				? user.getFirstName().substring(0, 1).toUpperCase(Locale.ROOT)
				: "";
		String l = user.getLastName() != null && !user.getLastName().isEmpty()
				? user.getLastName().substring(0, 1).toUpperCase(Locale.ROOT)
				: "";
		String combined = f + l;
		return combined.isEmpty() ? "U" : combined;
	}

	public String getGravatarUrl() {
		return gravatarUrlFor(user.getEmail());
	}

	public static String gravatarUrlFor(String email) {
		String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(normalized.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder(digest.length * 2);
			for (byte b : digest) {
				hex.append(String.format("%02x", b & 0xff));
			}
			return "https://www.gravatar.com/avatar/" + hex + "?s=160&d=identicon";
		} catch (NoSuchAlgorithmException e) {
			return "https://www.gravatar.com/avatar/?s=160&d=identicon";
		}
	}

}
