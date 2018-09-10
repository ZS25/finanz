package com.z_s.model.user;

import com.z_s.commons.util.MarsConstants;
import com.z_s.model.GenericMarsClass;
import com.z_s.model.IHistoryEntity;
import com.z_s.model.historique.HistoriqueEnum;
import com.z_s.model.organisation.Organisation;
import com.z_s.model.util.IndexNameUtil;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.DefaultValue;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zsooruth
 * @created 5 Jun 2014
 * @since 5 Jun 2014
 *
 */


@Entity
@Table(name = "T_USER")
@Audited
public class User extends GenericMarsClass implements IHistoryEntity, IAccessRightsEnable{

    private static final long serialVersionUID = 791614318011070218L;

    public static final String COLUMN_ID = "ID_USER";

    @Id
    @Column(name = COLUMN_ID, columnDefinition = "CHAR(32)")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String idUser;

    @Column(name = "LIB_NOM", length = 200, nullable = false)
    private String nom;

    @Column(name = "LIB_PRENOM", length = 200, nullable = false)
    private String prenom;

    @Column(name = "LIB_LOGIN", length = 50, nullable = false)
    private String loginName;

    @ManyToOne(fetch = FetchType.LAZY)
    @Index(name = IndexNameUtil.T_USER_ID_ABONNE_IDX)
    @JoinColumn(name = "ID_ABONNE", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @NotFound(action = NotFoundAction.IGNORE)
    private Abonne abonne;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_ORG", nullable = false)
    @Index(name = IndexNameUtil.T_USER_ID_ORG_IDX)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Organisation organisation;

    @Column(name = "BOO_ACTIF", nullable = false)
    private Boolean actif = Boolean.FALSE;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserPreferences preferences;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<RoleOrganisationsUtilisateurs> roleOrganisationsUtilisateurs;

    @Column(name = "BOO_SYSTEM", nullable = false)
    @NotAudited
    private Boolean system = Boolean.FALSE;

    // for Campagne du Jour - mes dossiers  
    @Column(name = "ID_CAMPAGNE_JOUR_USER")
    @Index(name = IndexNameUtil.T_USER_ID_CAMPAGNE_JOUR_USER_IDX)
    @NotAudited
    private String campagneJourUserId;

    @Column(name = "BOO_CAMPAGNE_JOUR_MES_GROUPS")
    @NotAudited
    private Boolean campagneJourMesGroups;

    @Column(name = "ID_CAMPAGNE_JOUR_USER_GROUP")
    @Index(name = IndexNameUtil.T_USER_ID_CAMPAGNE_JOUR_USER_GROUP_IDX)
    @NotAudited
    private String campagneJourMesGroupId;

    @Column(name = "BOO_RETARD")
    @NotAudited
    private Boolean retard = Boolean.FALSE;

    // end for Campagne du Jour - mes dossiers

    @Column(name = "NUM_FAILED_LOGIN_ATTEMPTS")
    @DefaultValue("0")
    @NotAudited
    private Integer failedLoginAttempts;

    @Column(name="DAT_CREATION_TOKEN")
    @NotAudited
    private Date tokenCreateDate;

    @Column(name = "ACCESS_TOKEN")
    @NotAudited
    private String accessToken;

    /**
     *
     */
    public User() {
        roleOrganisationsUtilisateurs = new HashSet<>();
        failedLoginAttempts = 0;
        this.actif = Boolean.TRUE;
        preferences = new UserPreferences();
    }

    /**
     * @param id
     * @param nom
     * @param prenom
     * @param loginName
     */
    public User(String id, String nom, String prenom, String loginName) {
        this();

        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.loginName = loginName;
    }

    public Integer getFailedLoginAttempts() {
        if(failedLoginAttempts == null){
            resetLoginAttempts();
        }
        return failedLoginAttempts;
    }

    public User setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
        return this;
    }

    @Transient
    public User incrementFailedLoginAttempts(){
        this.failedLoginAttempts = getFailedLoginAttempts() + 1;
        return this;
    }

    @Transient
    public User resetLoginAttempts(){
        this.failedLoginAttempts = 0;
        return this;
    }

    public Date getTokenCreateDate() {
        return tokenCreateDate;
    }

    public void setTokenCreateDate(Date tokenCreateDate) {
        this.tokenCreateDate = tokenCreateDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.setTokenCreateDate(new Date(System.currentTimeMillis()));
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Abonne getAbonne() {
        return abonne;
    }

    public void setAbonne(Abonne abonne) {
        this.abonne = abonne;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public Set<RoleOrganisationsUtilisateurs> getRoleOrganisationsUtilisateurs() {
        return roleOrganisationsUtilisateurs;
    }

    public void setRoleOrganisationsUtilisateurs(
            Set<RoleOrganisationsUtilisateurs> roleOrganisationsUtilisateurs) {
        this.roleOrganisationsUtilisateurs = roleOrganisationsUtilisateurs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (actif == null ? 0 : actif.hashCode());
        result = prime * result + (idUser == null ? 0 : idUser.hashCode());
        result = prime * result
                + (loginName == null ? 0 : loginName.hashCode());
        result = prime * result + (nom == null ? 0 : nom.hashCode());
        result = prime * result + (prenom == null ? 0 : prenom.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (actif == null) {
            if (other.actif != null) {
                return false;
            }
        } else if (!actif.equals(other.actif)) {
            return false;
        }
        if (idUser == null) {
            if (other.idUser != null) {
                return false;
            }
        } else if (!idUser.equals(other.idUser)) {
            return false;
        }
        if (loginName == null) {
            if (other.loginName != null) {
                return false;
            }
        } else if (!loginName.equals(other.loginName)) {
            return false;
        }
        if (nom == null) {
            if (other.nom != null) {
                return false;
            }
        } else if (!nom.equals(other.nom)) {
            return false;
        }
        if (prenom == null) {
            if (other.prenom != null) {
                return false;
            }
        } else if (!prenom.equals(other.prenom)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.z_s.model.GenericMarsClass#getGenericId()
     */
    @Override
    public String getGenericId() {
        return idUser;
    }

    @Override
    public String createDescriptor(String lang) {
        return getPrenom() + " " + getNom();
    }

    @Override
    public Set<IAccessRightsEnable> getDependentEntities() {
        return Collections.emptySet();
    }

    @Override
    public Set<RoleOrganisationsUtilisateurs> getAccessRoles() {
        return roleOrganisationsUtilisateurs;
    }
    
    
    /**
     * @return
     */
    public String getPhotoChecksum(){
        String checksum = null;
        if ( preferences != null && preferences.getAppearance() != null 
                && preferences.getAppearance().getPhoto() != null) {
            checksum = preferences.getAppearance().getPhoto().getChecksum() ;
        }
        return checksum;
    }
    
    /* (non-Javadoc)
     * @see com.z_s.model.IHistoryEntity#getHistoriqueEntityType()
     */
    @Override
    public HistoriqueEnum getHistoriqueEntityType() {
        return HistoriqueEnum.USER;
    }

    /**
     * @return
     */
    @Transient
    public String getFullName() {
        return nom + " " + prenom;
    }


    /**
     * @deprecated because it's slow,
     * replaced by <code>IUtilisateurDroitsMatriceDAO.hasDroit(... 3 implementations)</code>
     * @param roleCode
     * @param droitCode
     * @return
     */
    @Transient
    @Deprecated
    public boolean hasDroit(String roleCode, String droitCode) {
        boolean hasRight = false;
        for ( RoleOrganisationsUtilisateurs roa : getRoleOrganisationsUtilisateurs() ) {
            if ( roa.getRole().getCodeTech().equals(roleCode) ) {
                for ( ProfilDroits pd : roa.getProfilDroits() ) {
                    for ( Droit d : pd.getDroits() ) {
                        if ( d.getLibTechnique().equals(droitCode) ) {
                            hasRight = true;
                            break;
                        }
                    }
                    if ( hasRight ) {
                        break;
                    }
                }
            }
            if ( hasRight ) {
                break;
            }
        }
        return hasRight;
    }

    @Override
    public String getDescriptor(String lang) {
        return getPrenom() + " " + getNom();
    }

    public String getPassword(){
        String result = null;
        if(this.preferences != null && this.preferences.getAccount() != null){
            result = this.preferences.getAccount().getPassword();
        }
        return result;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }
    
    public Organisation getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
    
    public Boolean getRetard() {
        return retard;
    }

    public void setRetard(Boolean retard) {
        this.retard = retard;
    }

    public String getCampagneJourUserId() {
        return campagneJourUserId;
    }

    public void setCampagneJourUserId(String campagneJourUserId) {
        this.campagneJourUserId = campagneJourUserId;
    }

    public Boolean getCampagneJourMesGroups() {
        return campagneJourMesGroups;
    }

    public void setCampagneJourMesGroups(Boolean campagneJourMesGroups) {
        this.campagneJourMesGroups = campagneJourMesGroups;
    }

    public String getCampagneJourMesGroupId() {
        return campagneJourMesGroupId;
    }

    public void setCampagneJourMesGroupId(String campagneJourMesGroupId) {
        this.campagneJourMesGroupId = campagneJourMesGroupId;
    }

    @Transient
    public String getOrganisationId() {
        return getOrganisation().getIdOrganisation();
    }
    
}
