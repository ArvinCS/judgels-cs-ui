package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_bundle_supervisor")
@Table(indexes = {@Index(columnList = "bundleJid,userJid", unique = true)})
public class ContestBundleSupervisorModel extends Model {
    @Column(nullable = false)
    public String bundleJid;

    @Column(nullable = false)
    public String userJid;
}
