package com.z_s.dao.generic;

import com.z_s.commons.util.MarsConstants;
import com.z_s.commons.util.SpringPropertiesUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by zsooruth on 12/21/2016.
 */
@Transactional(propagation= Propagation.REQUIRED)
public class SimpleDAOImpl implements ISimpleDAO {

    @Resource
    protected SessionFactory sessionFactory;

    public void flush(){
        this.sessionFactory.getCurrentSession().flush();
    }

    public void clear(){
        this.sessionFactory.getCurrentSession().clear();
    }

    /**
     * method to return the current hibernate session WITH FILTERS ENABLED
     * @return
     */
    protected Session getFilteredCurrentSession(){
        Session result=this.sessionFactory.getCurrentSession();
        result.enableFilter(MarsConstants.SUPPRIME_FILTER);
        disableMigrationFilterForMigrationServiceOnly(result);

        return result;
    }

    /**
     * get the current hibernate session WITHOUT FILTERS
     * @return
     */
    protected Session getCurrentSessionWithoutFilters(){
        Session result= this.sessionFactory.getCurrentSession();
        result.disableFilter(MarsConstants.SUPPRIME_FILTER);
        disableMigrationFilterForMigrationServiceOnly(result);

        return result;
    }

    /**
     * If a call originates from the MigrationService disable the migration filter, otherwise ALWAYS keep it active 
     * @param result Session object
     */
    private void disableMigrationFilterForMigrationServiceOnly(Session result) {
        
        if(SpringPropertiesUtils.isMigrationService()) {
            result.disableFilter(MarsConstants.MIGRATION_FILTER);
        }
        else {
            result.enableFilter(MarsConstants.MIGRATION_FILTER);
        }
    }
}
