package judgels.jophiel.persistence;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import judgels.persistence.Model;

@Entity(name = "jophiel_user_group")
public class UserGroupModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    @ElementCollection
    @CollectionTable(name = "jophiel_user_group_group", joinColumns = @JoinColumn(name = "userGroupId"), indexes = {@Index(columnList = "groupItem")})
    @Column(name = "groupItem")
    public List<String> groups;
}
