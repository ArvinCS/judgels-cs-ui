package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_bundle_manager")
@Table(indexes = {@Index(columnList = "bundleJid,userJid", unique = true)})
public class ContestBundleManagerModel extends Model {
    @Column(nullable = false)
    public String bundleJid;

    @Column(nullable = false)
    public String userJid;
}

