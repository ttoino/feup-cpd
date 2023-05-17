package pt.up.fe.cpd.proj2.server.auth;

public record UserInfo(long id, String username, byte[] password, double elo) implements Comparable<UserInfo> {
    @Override
    public int compareTo(UserInfo o) {
        return Long.compare(id, o.id);
    }
}
