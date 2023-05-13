package pt.up.fe.cpd.proj2.server.auth;

public record UserInfo(int id, String username, String password, double elo) implements Comparable<UserInfo> {
    @Override
    public int compareTo(UserInfo o) {
        return Integer.compare(id, o.id);
    }
}
