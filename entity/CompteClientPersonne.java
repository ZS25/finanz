package com.z_s.model.personne;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.z_s.commons.util.MarsConstants;
import com.z_s.model.GenericClass;
import com.z_s.model.organisation.Organisation;
import com.z_s.model.util.EntityTypeEnum;
import com.z_s.model.util.IndexNameUtil;

/**
 * @author Zulfekar Sooruth
 * @created Mar 12, 2015
 * @since Mar 12, 2015
 */

@Entity
@org.hibernate.annotations.Table(appliesTo = "T_COMPTE_CLIENT_PERSONNE",
        indexes = {
                @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_ID_PAYS_IDX,
                        columnNames = {DateNaissance.COLUMN_ID_PAYS})
        }
)
@Table(name="T_COMPTE_CLIENT_PERSONNE")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CompteClientPersonne extends GenericClass{


    private static final long serialVersionUID = -616690373870337609L;

    public static final String COLUMN_ID_COMPTE_CLIENT_PERSONNE = "ID_COMPTE_CLIENT_PERSONNE";

    @Id
    @Column(name = CompteClientPersonne.COLUMN_ID_COMPTE_CLIENT_PERSONNE, columnDefinition = "CHAR(32)")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String idCompte;

    @Column(name = "LIB_REFERENCE_INTERNE", length = 50, nullable = false)
    @Generated(GenerationTime.ALWAYS)
    private String referenceInterne;

    @Column(name = "LIB_REFERENCE_CLIENT", length = 100, nullable = false)
    private String referenceClient;

    @ManyToOne(fetch=FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "ID_CLIENT")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_CLIENT")
    @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_ID_CLIENT_IDX)
    private Organisation client;

    @Embedded
    private DateNaissance dateNaissance;
    
    @Column(name = "BOO_PND")
    private boolean PND;
    
    @Column(name = "DAT_PND")
    @Type(type="utcType")
    private Date datePND;
    
    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "compteClientPersonne")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_ADRESSE_COURIER")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<AdresseCourrier> adressesCourier = new HashSet<>();

    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_ADRESSE_MAIL")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<AdresseMail> adressesMail = new HashSet<>();

    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_TELEPHONE")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<Telephone> telephones = new HashSet<>();
    
    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_CONTACT_PERSONNE_PHYSIQUE")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<ContactPersonnePhysique> contactsPersonnePhysique = new HashSet<>();
    
    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_COMPTE_BANCAIRE")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<CompteBancaire> compteBancaires = new HashSet<>();
    
    @Column(name = "LIB_DEPT_NAISSANCE")
    private String departementNaissance;
    
    @Column(name = "LIB_PAYS_NAISSANCE")
    private String paysNaissance;
    
    @Column(name = "LIB_ADRESSE_BRUTE")
    private String adresseBrute;
    
    @Column(name = "LIB_CIVILITE_BRUTE")
    private String civiliteBrute;
        
    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_TELEPHONE_BRUT")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<TelephoneBrut> telephonesBruts;
    
    @OneToMany(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
    @ForeignKey(name="FK_COMPTE_CLIENT_PERSONNE_ADRESSE_MAIL")
    @Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
    private Set<AdresseMailBrut> adressesMailBruts;

    @Column(name = "BOO_CNIL")
    private boolean CNIL = Boolean.FALSE;
    
    @Column(name = "EN_ENQUETE")
    private Boolean enEnquete = Boolean.FALSE;
    
    @Column(name = "DAT_EN_ENQUETE")
    @Type(type="utcType")
    private Date dateEnEnquete;
    
    @Column(name = "RETOUR_ENQUETE_NEGATIF")
    private Boolean retourEnqueteNegatif = Boolean.FALSE;
    
    public CompteClientPersonne(){
        super();
        adressesCourier = new HashSet<>();
        adressesMail = new HashSet<>();
        telephones = new HashSet<>();
    }

    @Override
    public String getGenericId() {
        return idCompte;
    }

    public boolean getCNIL() {
        return CNIL;
    }

    public void setCNIL(boolean CNIL) {
        this.CNIL = CNIL;
    }

    public String getReferenceInterne() {
        return referenceInterne;
    }

    public void setReferenceInterne(String referenceInterne) {
        this.referenceInterne = referenceInterne;
    }

    public String getReferenceClient() {
        return referenceClient;
    }

    public void setReferenceClient(String referenceClient) {
        this.referenceClient = referenceClient;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public Organisation getClient() {
        return client;
    }

    public void setClient(Organisation client) {
        this.client = client;
    }

    public DateNaissance getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(DateNaissance dateNaissance) {
        if (dateNaissance != null){
            this.dateNaissance = dateNaissance;
        } else {
            this.dateNaissance = new DateNaissance();
        }
    }
    

    /**
     * @return the pND
     */
    public boolean getPND() {
        return PND;
    }

    /**
     * @param pND the pND to set
     */
    public void setPND(boolean pND) {
        PND = pND;
    }

    /**
     * @return the datePND
     */
    public Date getDatePND() {
        return datePND;
    }

    /**
     * @param datePND the datePND to set
     */
    public void setDatePND(Date datePND) {
        this.datePND = datePND;
    }
    
    public Set<AdresseCourrier> getAdressesCourier() {
        return adressesCourier;
    }

    public void setAdressesCourier(Set<AdresseCourrier> adressesCourier) {
        this.adressesCourier = adressesCourier;
    }

    public Set<AdresseMail> getAdressesMail() {
        return adressesMail;
    }

    public void setAdressesMail(Set<AdresseMail> adressesMail) {
        this.adressesMail = adressesMail;
    }

    public Set<Telephone> getTelephones() {
        return telephones;
    }

    public void setTelephones(Set<Telephone> telephones) {
        this.telephones = telephones;
    }
    
    public Set<ContactPersonnePhysique> getContactsPersonnePhysique() {
        return contactsPersonnePhysique;
    }

    public void setContactsPersonnePhysique(Set<ContactPersonnePhysique> contactsPersonnePhysique) {
        this.contactsPersonnePhysique = contactsPersonnePhysique;
    }
    
    public Set<CompteBancaire> getCompteBancaires() {
        return compteBancaires;
    }

    public void setCompteBancaires(Set<CompteBancaire> compteBancaires) {
        this.compteBancaires = compteBancaires;
    }
    
    /**
     * 
     */
    public boolean refreshPND() {
        boolean updateCC = false;
        
        if (hasAtLeastOneNonPNDAdress()) {
            if (PND) {
                PND = false;
                datePND = null;
                updateCC = true;
            }
        } else if (!PND) {
            PND = true;
            datePND = new Date();
            updateCC = true;
        }
       
        return updateCC;
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public boolean hasAtLeastOneNonPNDAdress(){
        return getAdressesCourier().stream().filter(a -> !a.getPND()).findAny().isPresent();
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public boolean hasAtLeastOnePrincipaleTelephone(){
        return getTelephones()
                .stream()
                .filter(tel->!tel.getSupprime())
                .anyMatch(telephone -> telephone.getPrincipale() != null && telephone.getPrincipale());
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public boolean hasAtLeastOneValideTelephone(){
        return getTelephones().stream().filter(Telephone::getValide).findAny().isPresent();
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public boolean hasAtLeastOnePrincipalMail(){
        return getAdressesMail().stream().anyMatch(AdresseMail::getPrincipale);
    }
    

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public AdresseCourrier getOldPrincipaleAdresse(AdresseCourrier newPrincipaleAdresse) {
        AdresseCourrier principaleAdress = null;
        if ( newPrincipaleAdresse.getPrincipale() ) {
            for ( AdresseCourrier ac : getAdressesCourier() ) {
                if ( ac.getPrincipale() != null && ac.getPrincipale() && !ac.getIdAdresse().equals(newPrincipaleAdresse.getIdAdresse())) {
                    principaleAdress = ac;
                    break;
                }
            }
        }
        return principaleAdress;
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @return
     */
    @Transient
    @Deprecated
    public AdresseCourrier getPrincipaleAdresse() {
        Optional<AdresseCourrier> first = adressesCourier.stream().filter(AdresseCourrier::getPrincipale).findFirst();
        return first.orElse(null);
    }

    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @param newPrincipaleTelephone
     * @return
     */
    @Deprecated
    public Telephone getOldPrincipaleTelephone(Telephone newPrincipaleTelephone) {
        Telephone principaleTelephone = null;
        if ( newPrincipaleTelephone.getPrincipale() ) {
            for ( Telephone telephone : getTelephones() ) {
                if ( !telephone.getSupprime() && telephone.getPrincipale() != null && telephone.getPrincipale() && !telephone.getIdTelephone().equals(newPrincipaleTelephone.getIdTelephone())) {
                    principaleTelephone = telephone;
                    break;
                }
            }
        }
        return principaleTelephone;
    }
    
    /**
     * @deprecated because it's slow,
     * create use/query instead
     * @param newPrincipaleMail
     * @return
     */
    @Deprecated
    public AdresseMail getOldPrincipaleMail(AdresseMail newPrincipaleMail) {
        AdresseMail principaleMail = null;
        if ( newPrincipaleMail.getPrincipale() ) {
            for ( AdresseMail mail : getAdressesMail() ) {
                if ( !mail.getSupprime() && mail.getPrincipale() != null && mail.getPrincipale() && !mail.getIdAdresseMail().equals(newPrincipaleMail.getIdAdresseMail())) {
                    principaleMail = mail;
                    break;
                }
            }
        }
        return principaleMail;
    }
    
    public abstract EntityTypeEnum getType();

    public String getDepartementNaissance() {
        return departementNaissance;
    }

    public void setDepartementNaissance(String departementNaissance) {
        this.departementNaissance = departementNaissance;
    }

    public String getPaysNaissance() {
        return paysNaissance;
    }

    public void setPaysNaissance(String paysNaissance) {
        this.paysNaissance = paysNaissance;
    }

    public String getAdresseBrute() {
        return adresseBrute;
    }

    public void setAdresseBrute(String adresseBrute) {
        this.adresseBrute = adresseBrute;
    }

    public String getCiviliteBrute() {
        return civiliteBrute;
    }

    public void setCiviliteBrute(String civiliteBrute) {
        this.civiliteBrute = civiliteBrute;
    }

    public Set<TelephoneBrut> getTelephonesBruts() {
        return telephonesBruts;
    }

    public void setTelephonesBruts(Set<TelephoneBrut> telephonesBruts) {
        this.telephonesBruts = telephonesBruts;
    }

    public Set<AdresseMailBrut> getAdressesMailBruts() {
        return adressesMailBruts;
    }

    public void setAdressesMailBruts(Set<AdresseMailBrut> adressesMailBruts) {
        this.adressesMailBruts = adressesMailBruts;
    }

    /**
     * @return the enEnquete
     */
    public Boolean isEnEnquete() {
        return enEnquete;
    }

    /**
     * @param enEnquete the enEnquete to set
     */
    public void setEnEnquete(Boolean enEnquete) {
        this.enEnquete = enEnquete;
    }

    public Date getDateEnEnquete() {
        return dateEnEnquete;
    }

    public void setDateEnEnquete(Date dateEnEnquete) {
        this.dateEnEnquete = dateEnEnquete;
    }

    public Boolean getRetourEnqueteNegatif() {
        return retourEnqueteNegatif;
    }

    public void setRetourEnqueteNegatif(Boolean retourEnqueteNegatif) {
        this.retourEnqueteNegatif = retourEnqueteNegatif;
    }


}
