package com.z_s.model.personne;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.z_s.model.util.EntityTypeEnum;
import com.z_s.model.util.IndexNameUtil;

/**
 * @author Zulfekar Sooruth
 * @created Mar 12, 2015
 * @since Mar 12, 2015
 */

@Entity
@Table(name = "T_COMPTE_CLIENT_PERSONNE_MORALE")
@PrimaryKeyJoinColumn(name="ID_COMPTE_CLIENT_PERSONNE")
public class CompteClientPersonneMorale  extends CompteClientPersonne{

    private static final long serialVersionUID = -1856810505163777794L;
    
    @Column(name = "LIB_RAISON_SOCIALE", length = 200)
    private String raisonSociale;
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "ID_FORME_JURIDIQUE", nullable = true, updatable = true)
    @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_MORALE_ID_FORME_JURIDIQUE_IDX)
    private FormeJuridique formeJuridique;
    
    @Column(name = "LIB_SIREN", length = 50)
    private String siren;
    
    @Column(name = "LIB_SIRET", length = 50)
    private String siret;

    @Column(name = "LIB_COMMENTAIRE", length = 1000)
    private String commentaire;
    
    @Column(name = "LIB_RCS", length = 50)
    private String rcs;
    
    @Column(name = "NUM_CAPITAL")
    private BigDecimal capital; 
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "ID_DOMAINE_ACTIVITE", nullable = true, updatable = true)
    @Index(name = IndexNameUtil.T_COMPTE_CLIENT_PERSONNE_MORALE_ID_DOMAINE_ACTIVITE_IDX)
    private APE domaineActivite;
    
    @Column(name = "NUM_CHIFFRE_AFFAIRE")
    private BigDecimal chiffreAffaire;

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getRcs() {
        return rcs;
    }

    public void setRcs(String rcs) {
        this.rcs = rcs;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public APE getDomaineActivite() {
        return domaineActivite;
    }

    public void setDomaineActivite(APE domaineActivite) {
        this.domaineActivite = domaineActivite;
    }

    public BigDecimal getChiffreAffaire() {
        return chiffreAffaire;
    }

    public void setChiffreAffaire(BigDecimal chiffreAffaire) {
        this.chiffreAffaire = chiffreAffaire;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "CompteClientPersonneMorale{" +
                "raisonSociale='" + raisonSociale + '\'' +
                ", formeJuridique=" + formeJuridique +
                ", siren='" + siren + '\'' +
                ", siret='" + siret + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", rcs='" + rcs + '\'' +
                ", capital=" + capital +
                ", domaineActivite=" + domaineActivite +
                ", chiffreAffaire=" + chiffreAffaire +
                '}';
    }

    public EntityTypeEnum getType() {
        return EntityTypeEnum.COMPTE_CLIENT_PERSONNE_MORALE;
    }

    public String getDetails(){
        String details = "";
        if (this.getFormeJuridique()!=null){
            details = details + this.getFormeJuridique().getCode() + " ";
        }
        details = details + this.getRaisonSociale() + " ";
        if (this.getDateNaissance()!=null && this.getDateNaissance().getDateNaissance()!=null){
            details = details + this.getDateNaissance().getDateNaissance() + " ";
        }
        details = details + this.getSiren() + " " + this.getSiret();

        return details;
    }

}
