public class Utilizator {
	private String Username;
	private String Password;

	public Utilizator() {
		Username = null;
		Password = null;
	}

	public Utilizator(String Username, String Password) {
		this.Username = Username;
		this.Password = Password;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	@Override
	public String toString() {
		return "Utilizator [Username=" + Username + ", Password=" + Password + "]";
	}
}
