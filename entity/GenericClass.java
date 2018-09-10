/**
 *
 */
package com.dso.model;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dso.commons.util.SpringPropertiesUtils;
import com.dso.model.audit.annotation.AuditScannerIgnore;
import org.hibernate.annotations.*;

import com.dso.commons.util.MarsConstants;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * @author Zulfekar Sooruth
 * @created Jul 15, 2014
 * @since Jul 15, 2014
 *
 */
@MappedSuperclass
@FilterDefs({ @FilterDef(name = MarsConstants.SUPPRIME_FILTER, defaultCondition= "BOO_SUPPRIME = 'FALSE' "),
        @FilterDef(name = MarsConstants.SUPPRIME_FILTER_WITH_NULL, defaultCondition= "(BOO_SUPPRIME = 'FALSE' or  BOO_SUPPRIME is null) "),
        @FilterDef(name = MarsConstants.MIGRATION_FILTER, defaultCondition= "BOO_EN_MIGRATION = 'FALSE' "),
        @FilterDef(name = MarsConstants.MIGRATION_FILTER_WITH_NULL, defaultCondition= "(BOO_EN_MIGRATION = 'FALSE' or BOO_EN_MIGRATION is null) ")})
@Filters({ @Filter(name = MarsConstants.SUPPRIME_FILTER), @Filter(name = MarsConstants.MIGRATION_FILTER) })
@Audited
public abstract class GenericClass implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7212200820208226124L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DAT_UPDATE")
    @Type(type = "utcType")
    @AuditScannerIgnore
    private Date timestampUpdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DAT_CREATE", columnDefinition="TIMESTAMP DEFAULT (now() at time zone 'utc')", insertable=false, updatable=false, nullable = false)
    @Type(type = "utcType")
    @NotAudited
    @AuditScannerIgnore
    private Date timestampCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DAT_SUPPRIME")
    @Type(type = "utcType")
    @NotAudited
    @AuditScannerIgnore
    private Date timestampSupprime;

    @Column(name="BOO_SUPPRIME", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @AuditScannerIgnore
    private Boolean supprime = Boolean.FALSE;

    @Column(name="BOO_EN_MIGRATION", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @NotAudited
    @AuditScannerIgnore
    private Boolean enMigration = Boolean.FALSE;


    /**
     *
     */
    public GenericClass(){
        supprime = Boolean.FALSE;
        enMigration = SpringPropertiesUtils.isMigrationService();
    }

    public abstract String getGenericId();

    @PrePersist
    public void onCreate() {
        timestampUpdate = timestampCreate = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    public void onUpdate() {
        setTimestampUpdate( new Date(System.currentTimeMillis()));
    }

    public Date getTimestampCreate() {
        return timestampCreate;
    }

    public void setTimestampCreate(Date timestampCreate) {
        this.timestampCreate = timestampCreate;
    }

    public Date getTimestampUpdate() {
        return timestampUpdate;
    }

    public void setTimestampUpdate(Date timestampUpdate) {
        this.timestampUpdate = timestampUpdate;
    }

    public Boolean getSupprime() {
        return supprime;
    }

    public void setSupprime(Boolean supprime) {
        this.supprime = supprime;
        if ( supprime ) {
            setTimestampSupprime(new Date(System.currentTimeMillis()));
        }
    }

    @Transient
    public Boolean isNotSupprime(){
        return !supprime;
    }

    public Date getTimestampSupprime() {
        return timestampSupprime;
    }

    public void setTimestampSupprime(Date timestampSupprime) {
        this.timestampSupprime = timestampSupprime;
    }

    public Boolean getEnMigration() {
        return enMigration;
    }

    public void setEnMigration(Boolean enMigration) {
        this.enMigration = enMigration;
    }
}
