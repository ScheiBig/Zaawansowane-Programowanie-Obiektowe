package lab_5_1.net;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public sealed abstract class Msg
		implements Serializable
		permits Msg.Register,
		Msg.ListUsers,
		Msg.All,
		Msg.SendTo,
		Msg.Exit,
		Msg.Help
{
	public static final String UsernameTaken = "That username is already taken";
	public static final String UsernameInvalid = "That username is invalid or reserved";
	public static final String NotRegistered = "Not registered: use `REGISTER {username}` to " +
			"register" +
			" " +
			"first";
	public static final String AlreadyRegistered = "Already registered";
	public static final String NoSuchUser = "User with this name does not exist";
	public static final String NoUsers = "No other users available";

	public static final String Success = "Success";
	public static final String UnknownCommand = "Unknown Command";

	public static final List<String> Commands = Arrays.asList(Register.Cmd,
			ListUsers.Cmd,
			All.Cmd,
			SendTo.Cmd,
			Exit.Cmd,
			Help.Cmd
	);

	public final String cmd;

	private Msg(String cmd) {
		this.cmd = cmd;
	}

	public abstract String toString();

	public static final class Register
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "REGISTER";
		public final String username;

		public Register(String username) {
			super(Register.Cmd);
			this.username = username;
		}

		@Override
		public String toString() {
			return Register.Cmd + " " + this.username;
		}

		public static String help() {
			return """
					REGISTER <username>
					--------------------------------------------------------------------------------
					Registers you with specified username. Username can only contain alphanumeric
					ASCII characters.
					
					Command cannot be used as username - for list of commands type `HELP`.
					
					Usernames are session-based - after you  log out, your username becomes free
					to take again.
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Register register)) {
				return false;
			}
			return Objects.equals(username, register.username);
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd, username);
		}
	}

	public static final class ListUsers
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "LIST_USERS";

		public ListUsers() {
			super(ListUsers.Cmd);
		}

		@Override
		public String toString() {
			return ListUsers.Cmd;
		}

		public static String help() {
			return """
					LIST_USERS
					--------------------------------------------------------------------------------
					Prints list of usernames of users in chat.
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Register)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd);
		}
	}

	public static final class SendTo
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "SEND_TO";
		public final String username;
		public final String message;

		public SendTo(
				String username,
				String message
		) {
			super(SendTo.Cmd);
			this.username = username;
			this.message = message;
		}

		@Override
		public String toString() {
			return SendTo.Cmd + " " + this.username + " > " + this.message;
		}

		public static String help() {
			return """
					<username> <message>
					--------------------------------------------------------------------------------
					Sends direct message to specified user.
					
					Any word that is not command will be treated as username and will trigger
					this command.
					
					User must be currently registered in chat - for list of available users to chat
					with type `LIST_USERS`
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof SendTo sendTo)) {
				return false;
			}
			return Objects.equals(username, sendTo.username) &&
					Objects.equals(message, sendTo.message);
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd, username, message);
		}
	}

	public static final class All
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "ALL";
		public final String message;

		public All(String message) {
			super(All.Cmd);
			this.message = message;
		}

		@Override
		public String toString() {
			return All.Cmd + " > " + this.message;
		}

		public static String help() {
			return """
					ALL <message>
					--------------------------------------------------------------------------------
					Sends message to all users in chat.
					
					There must be at least one user currently registered in chat - for list of
					available users to chat with type `LIST_USERS`
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof All all)) {
				return false;
			}
			return Objects.equals(message, all.message);
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd, message);
		}
	}

	public static final class Exit
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "EXIT";
		public final String username;

		public Exit(@Nullable String username) {
			super(Exit.Cmd);
			this.username = username;
		}

		@Override
		public String toString() {
			return Exit.Cmd + " " + this.username;
		}

		public static String help() {
			return """
					EXIT
					--------------------------------------------------------------------------------
					Exits from chat in safe manner.
					
					Exiting via `EXIT` command ensures, that you receive all pending messages sent
					to you before quiting.
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Exit exit)) {
				return false;
			}
			return Objects.equals(username, exit.username);
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd, username);
		}
	}

	public static final class Help
			extends Msg
	{
		@Serial private static final long serialVersionUID = 1;
		public static final String Cmd = "HELP";

		public Help() {
			super(Help.Cmd);
		}

		@Override
		public String toString() {
			return Help.Cmd;
		}

		public static String help() {
			return """
					HELP <command>
					--------------------------------------------------------------------------------
					Prints help for given command. Available commands are:
					
					REGISTER <username> - registers you with specified username.
					LIST_USERS - prints list of usernames of users in chat.
					<username> <message> - (SEND_TO command) sends direct message to specified user.
					ALL <message> - Sends message to all users in chat.
					EXIT - Exits from chat in safe manner.
					""";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Help)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(Cmd);
		}
	}
}
