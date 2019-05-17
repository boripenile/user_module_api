package app.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Token implements Serializable {

    private String username;
    
    private String[] roles;
    
    private String[] selfPermissions;
    
    private String[] basicPermissions;
    
    private long timestamp;

    public Token() {

    }

    public Token(String username, long timestamp) {
        super();
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String[] getSelfPermissions() {
		return selfPermissions;
	}

	public void setSelfPermissions(String[] selfPermissions) {
		this.selfPermissions = selfPermissions;
	}

	public String[] getBasicPermissions() {
		return basicPermissions;
	}

	public void setBasicPermissions(String[] basicPermissions) {
		this.basicPermissions = basicPermissions;
	}

	

}
