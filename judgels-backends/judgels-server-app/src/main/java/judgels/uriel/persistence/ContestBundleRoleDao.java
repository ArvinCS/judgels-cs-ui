package judgels.uriel.persistence;

public interface ContestBundleRoleDao {
    boolean isManager(String userJid, String contestBundleJid);
}
