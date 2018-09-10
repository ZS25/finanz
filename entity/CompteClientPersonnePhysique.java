package com.z_s.model.personne;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.z_s.commons.util.MarsConstants;
import com.z_s.model.util.EntityTypeEnum;
import com.z_s.model.util.IndexNameUtil;


/**
 * @author Zulfekar Sooruth
 * @created Mar 12, 2015
 * @since Mar 12, 2015
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = "T_COMPTE_CLIENT_PERSONNE_PHYSIQUE",
        indexes = {
                @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_PHYSIQUE_ID_CIVILITE_IDX,
                        columnNames = {PersonnePhysique.COLUMN_ID_CIVILITE})
        }
)
@Table(name = "T_COMPTE_CLIENT_PERSONNE_PHYSIQUE")
@PrimaryKeyJoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
public class CompteClientPersonnePhysique extends CompteClientPersonne {

    private static final long serialVersionUID = -2699719668666080825L;
        
    @Column(name = "DAT_CREATION")
    @Type(type="utcType")
    private Date dateCreation;
    
    @Embedded
    private PersonnePhysique personnePhysique = new PersonnePhysique(); 
    
    @Column(name = "BOO_SURENDETTE")
    private Boolean surendette;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_BANQUE_FRANCE")
    @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_PHYSIQUE_ID_BANQUE_FRANCE_IDX)
    private BanqueDeFrance banqueDeFrance;
    
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE_PHYSIQUE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_PHYSIQUE_CONTACT_PERSONNE_MORALE")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<ContactPersonneMorale> contactsPersonneMorale = new HashSet<>();

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public PersonnePhysique getPersonnePhysique() {
        return personnePhysique;
    }

    public void setPersonnePhysique(PersonnePhysique personnePhysique) {
        this.personnePhysique = personnePhysique;
    }

    public Boolean getSurendette() {
        return surendette;
    }

    public void setSurendette(Boolean surendette) {
        this.surendette = surendette;
    }

    public BanqueDeFrance getBanqueDeFrance() {
        return banqueDeFrance;
    }

    public void setBanqueDeFrance(BanqueDeFrance banqueDeFrance) {
        this.banqueDeFrance = banqueDeFrance;
    }

    public Set<ContactPersonneMorale> getContactsPersonneMorale() {
        return contactsPersonneMorale;
    }

    public void setContactsPersonneMorale(Set<ContactPersonneMorale> contactsPersonneMorale) {
        this.contactsPersonneMorale = contactsPersonneMorale;
    }

    @Override
    public String toString() {
        return "CompteClientPersonnePhysique [dateCreation=" + dateCreation + ", personnePhysique=" + personnePhysique + ", surendette=" + surendette +  ", banqueDeFrance=" + banqueDeFrance + ", contactsPersonneMorale=" + contactsPersonneMorale +"]";
    }

    
    public EntityTypeEnum getType() {
        return EntityTypeEnum.COMPTE_CLIENT_PERSONNE_PHISIQUE;
    }

    public String getDetails(){
        String details = "";
        if (this.getPersonnePhysique()!= null){
            if (this.getPersonnePhysique().getCivilite()!=null){
                details = this.getPersonnePhysique().getCivilite().getCiviliteCode() + " ";
            }
            details = details + this.getPersonnePhysique().getNom() + " "
                    + this.getPersonnePhysique().getPrenom() + " ";
        }
        if (this.getDateNaissance()!=null){
            if (this.getDateNaissance().getDateNaissance()!=null){
                details = details + this.getDateNaissance().getDateNaissance() + " ";
            }
            if (this.getDateNaissance().getLieu() != null){
                details = details + this.getDateNaissance().getLieu();
            }
        }
        return details;
    }
}
