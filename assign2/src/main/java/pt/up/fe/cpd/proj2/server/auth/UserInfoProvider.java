package pt.up.fe.cpd.proj2.server.auth;

public interface UserInfoProvider {
    UserInfo login(String username, String password);
    UserInfo register(String username, String password);
}
