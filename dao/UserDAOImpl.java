package com.z_s.dao.user.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.z_s.dao.dto.SelectItemDTO;
import com.z_s.dao.dto.user.UserLanguageDTO;
import com.z_s.dao.util.HqlQueryHelper;
import com.z_s.dao.util.storeparams.UserStoreParams;
import com.z_s.dao.util.storeparams.UsersForAttributionStoreParams;
import com.z_s.model.droits.DroitsEnum;
import com.z_s.model.user.RoleTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.z_s.commons.exceptions.rs.BadRequestMarsException;
import com.z_s.commons.util.string.HQLUtils;
import com.z_s.dao.dto.user.AttributionUserDTO;
import com.z_s.dao.dto.user.SimpleUserDTO;
import com.z_s.dao.dto.user.UserAlerteDTO;
import com.z_s.dao.generic.GenericDAOImpl;
import com.z_s.dao.user.IUserDAO;
import com.z_s.dao.util.EntitySortCriteria;
import com.z_s.model.organisation.Organisation;
import com.z_s.model.user.User;

@Repository
public class UserDAOImpl extends GenericDAOImpl<User> implements IUserDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

	/* (non-Javadoc)
	 * @see com.z_s.dao.user.IUserDAO#isNameUnique(java.lang.String)
	 */
	@Override
	public boolean isNameUnique(String name) {
		String queryString = "SELECT count(distinct u) FROM User u WHERE lower(u.loginName) = :loginName";
		Query query = getFilteredCurrentSession().createQuery(queryString);

		query.setParameter("loginName",  name.toLowerCase());

		Long nrEntities = (Long)query.uniqueResult();

		return nrEntities == 0;
	}

    @Override
    public boolean hasActiveAccount(final String userId){
		String queryString = "SELECT count(distinct u.idUser) FROM User u WHERE u.actif=true and u.idUser = :idUser";
		Query query = getFilteredCurrentSession().createQuery(queryString);
        query.setParameter("idUser", userId);

		Long nrEntities = (Long)query.uniqueResult();

		return nrEntities == 1;
	}

	/*
	 * Load the user with loginName= @loginName
	 *
	 * @see com.z_s.user.dao.IUserDAO#findByLoginName(java.lang.String)
	 */
	@Override
    public User findByLoginName(String loginName) {
		User result = null;

		String queryString = "SELECT u from User u WHERE u.loginName = :login";

		Query query = getFilteredCurrentSession().createQuery(
				queryString);
		query.setParameter("login", loginName);

		List<User> userList = query.list();

		if (userList != null && !userList.isEmpty()) {
			result = userList.get(0);
		}

		return result;
	}

	@Override
	public User findByCredentials(String loginName, String password) {
		User result = null;

		Criteria criteria = getFilteredCurrentSession().createCriteria(User.class);
		User dbUser = (User) criteria.add(Restrictions.like("loginName",loginName)).uniqueResult();
		if (dbUser == null) {
			// no user
			return null;
		}
		if(BCrypt.checkpw(password,dbUser.getPassword())) {
			result = dbUser;
		}

		return result;
	}


	@Override
	public User findByLoginNameIfActive(String loginName) {
		User result = null;

		String queryString = "SELECT u from User u WHERE u.loginName = :login AND u.actif=true AND u.abonne.actif=true";

		Query query = getFilteredCurrentSession().createQuery(
				queryString);
		query.setParameter("login", loginName);

		List<User> userList = query.list();

		if (userList != null && !userList.isEmpty()) {
			result = userList.get(0);
		}

		return result;
	}

	@Override
	public Long findUsersWithPaginationAndSearchCount(UserStoreParams userStoreParams, Collection<String> abonneIds, String lang) {

		String queryString = buildQuery(userStoreParams, abonneIds, true);
		Query query = createQuery(queryString, userStoreParams);

		if( !abonneIds.isEmpty() ){
			query.setParameterList("currentUserAbonnesIds", abonneIds);
		}

		return (Long)query.uniqueResult();
	}

	@Override
	public List<User> findUsersWithPaginationAndSearch(UserStoreParams userStoreParams, Collection<String> abonneIds, List<EntitySortCriteria> sortCriteria, String lang) {
		String queryString = buildQuery(userStoreParams, abonneIds, false);

		if (!CollectionUtils.isEmpty(sortCriteria)) {
			queryString = queryString + " order by ";
			for (EntitySortCriteria msc : sortCriteria) {
				if (!queryString.endsWith("order by ")) {
					queryString = queryString + ", ";
				}

				if ("nom".equals(msc.getProperty())) {
					queryString = queryString + " u.nom " + msc.getDirection();
				} else if ("prenom".equals(msc.getProperty())) {
					queryString = queryString + " u.prenom " + msc.getDirection();
				} else if ("loginName".equals(msc.getProperty())) {
					queryString = queryString + " u.loginName " + msc.getDirection();
				} else if ("email".equals(msc.getProperty())) {
					queryString = queryString + " u.preferences.account.email " + msc.getDirection();
				} else if ("actif".equals(msc.getProperty())) {
					queryString = queryString + " u.actif " + msc.getDirection();
				} else if ("abonne".equals(msc.getProperty())) {
					queryString = queryString + " u.abonne.nom " + msc.getDirection();
				}

			}
		}

		Query query = createQuery(queryString, userStoreParams);

		if( !abonneIds.isEmpty() ){
			query.setParameterList("currentUserAbonnesIds", abonneIds);
		}

		query.setFirstResult(userStoreParams.getStart());
		query.setMaxResults(userStoreParams.getLimit());

		return query.list();
	}

	/**
	 * @param queryString
	 * @param userStoreParams
	 * @return
	 */
	private Query createQuery(String queryString, UserStoreParams userStoreParams) {
		String searchUser = userStoreParams.getSearchUser();
		String searchActif = userStoreParams.getSearchActif();
		String searchRole = userStoreParams.getSearchRole();
		String searchAbonne = userStoreParams.getSearchAbonne();
		String searchProfilDroit = userStoreParams.getSearchProfilDroit();
		String searchDroit = userStoreParams.getSearchDroit();
		String searchProfilGed = userStoreParams.getSearchProfilGed();
		String searchGedFileType = userStoreParams.getSearchGedFileType();

		Query query = getFilteredCurrentSession().createQuery(queryString);

		if (!StringUtils.isEmpty(searchUser)) {
			query.setParameter("searchUser", buildPattern(searchUser));
		}
		//searchActif possible values: true, false, tous
		if (!StringUtils.isEmpty(searchActif) && (Boolean.TRUE.toString().equals(searchActif) || Boolean.FALSE.toString().equals(searchActif))) {
			query.setBoolean("searchActif", Boolean.valueOf(searchActif));
		}
		if (!StringUtils.isEmpty(searchRole)) {
			query.setParameter("searchRole", searchRole);
		}
		if (!StringUtils.isEmpty(searchProfilDroit)) {
			query.setParameter("searchProfilDroit", searchProfilDroit);
		}
		if (!StringUtils.isEmpty(searchDroit)) {
			query.setParameter("searchDroit", searchDroit);
		}
		if (!StringUtils.isEmpty(searchProfilGed)) {
			query.setParameter("searchProfilGed", searchProfilGed);
		}
		if (!StringUtils.isEmpty(searchGedFileType)) {
			query.setParameter("searchGedFileType", searchGedFileType);
		}
		if (!StringUtils.isEmpty(searchAbonne) ) {
			query.setParameter("searchAbonne", searchAbonne);
		}

		return query;
	}

	/**
	 * Replaces the spaces with %
	 * @param criteria
	 * @return
	 */
	private String buildPattern(String criteria) {
		String result = HQLUtils.normalizeParam(criteria.replaceAll("\\s+", "%"));

		result = "%" + result + "%";

		return result;
	}

	/**
	 * @param userStoreParams
	 * @param abonneIds
	 * @return
	 */
	private String buildQuery(UserStoreParams userStoreParams, Collection<String> abonneIds, Boolean isCount) {
		String searchProfilDroit = userStoreParams.getSearchProfilDroit();
		String searchDroit = userStoreParams.getSearchDroit();
		String searchProfilGed = userStoreParams.getSearchProfilGed();
		String searchGedFileType = userStoreParams.getSearchGedFileType();

		StringBuilder sb = new StringBuilder();
		if (isCount) {
			sb.append("SELECT count(distinct u.id) ");
		} else {
			sb.append("SELECT u ");
		}
		sb.append("from User u ")
				.append(" left join u.roleOrganisationsUtilisateurs r ")
				.append(!StringUtils.isEmpty(searchProfilDroit) || !StringUtils.isEmpty(searchDroit) ? " left join r.profilDroits pd " : "")
				.append(!StringUtils.isEmpty(searchDroit) ? " left join pd.droits d " : "")
				.append(!StringUtils.isEmpty(searchProfilGed) || !StringUtils.isEmpty(searchGedFileType) ? " left join r.profilGeds pg " : "")
				.append(!StringUtils.isEmpty(searchGedFileType) ? " left join pg.gedFileTypes gft " : "")
				.append(" where ( u.system is null or u.system = false ) ");

		if ( !abonneIds.isEmpty() ) {
			sb.append(" AND u.abonne.idAbonne IN (:currentUserAbonnesIds) ");
		}

		sb.append(addUserSearchCriteria(userStoreParams.getSearchUser()));
		sb.append(addActifSearchCriteria(userStoreParams.getSearchActif()));
		sb.append(addRoleSearchCriteria(userStoreParams.getSearchRole()));
		sb.append(addProfilDroitSearchCriteria(searchProfilDroit));
		sb.append(addDroitSearchCriteria(searchDroit));
		sb.append(addProfilGedSearchCriteria(searchProfilGed));
		sb.append(addGedSearchCriteria(searchGedFileType));
		sb.append(addAbonneSearchCriteria(userStoreParams.getSearchAbonne()));

		if (!isCount) {
			sb.append(" GROUP BY u, u.abonne.nom, u.preferences.account.email ");
		}

		return sb.toString();
	}

	/**
	 * @param searchAbonne
	 * @return
	 */
	private String addAbonneSearchCriteria(String searchAbonne){
		String result = "";

		if ( !StringUtils.isEmpty(searchAbonne) ) {
			result = " AND u.abonne.idAbonne = :searchAbonne";
		}

		return result;
	}

	/**
	 * @param searchUser
	 * @return
	 */
	private String addUserSearchCriteria(String searchUser){
		String result = "";

		if (searchUser != null && !searchUser.isEmpty()) {
			result  = " AND  ( ( " +  HQLUtils.normalizeMember("lower(u.nom)") + " LIKE lower(:searchUser)) "
					+ " OR ( " + HQLUtils.normalizeMember("lower(u.prenom)") + " LIKE lower(:searchUser))"
					+ " OR ( " + HQLUtils.normalizeMember("lower(u.loginName)") + " LIKE lower(:searchUser))"
					+ " OR ( " + HQLUtils.normalizeMember("lower(u.preferences.account.email)") + " LIKE lower(:searchUser)) ) ";

		}
		return result;
	}

	/**
	 * @param searchActif
	 * @return
	 */
	private String addActifSearchCriteria(String searchActif){
		String result = "";

		//searchActif possible values: true, false, tous
		if (!StringUtils.isEmpty(searchActif) && (Boolean.TRUE.toString().equals(searchActif) || Boolean.FALSE.toString().equals(searchActif))) {
			result  = " AND u.actif IS :searchActif ";
		}

		return result;
	}

	/**
	 * @param searchRole
	 * @return
	 */
	private String addRoleSearchCriteria(String searchRole){
		String result = "";

		if ( searchRole != null ) {
			result  = " AND r.role.idRole = :searchRole ";
		}

		return result;
	}


	/**
	 * @param searchProfilDroit
	 * @return
	 */
	private String addProfilDroitSearchCriteria(String searchProfilDroit){
		String result = "";

		if ( searchProfilDroit != null ) {
			result  = " AND pd.profil.idProfil = :searchProfilDroit ";
		}

		return result;
	}

	/**
	 * @param searchDroit
	 * @return
	 */
	private String addDroitSearchCriteria(String searchDroit){
		String result = "";

		if ( searchDroit != null ) {
			result  = " AND d.idDroit = :searchDroit ";
		}

		return result;
	}

	private String addProfilGedSearchCriteria(String searchProfilGed){
		String result = "";

		if (!StringUtils.isEmpty(searchProfilGed)) {
			result  = " AND pg.profil.idProfil = :searchProfilGed ";
		}

		return result;
	}

	private String addGedSearchCriteria(String searchGedFileType){
		String result = "";

		if (!StringUtils.isEmpty(searchGedFileType)) {
			result  = " AND gft.idFileType = :searchGedFileType ";
		}

		return result;
	}


	/**
     * returns all users for current abonne with given right
     *
     * @param abonneId
     * @param right
     * @return
     */
    @Override
    public List<String> getUsersForAbonneeWithRight(String abonneId, String right) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT distinct dm.user.idUser "
                + "FROM  UtilisateurDroitsMatrice dm  "
                + "WHERE dm.user.abonne.idAbonne = :abonneId ");

        if (!StringUtils.isEmpty(right)) {
            queryBuilder.append("AND dm.droit.libTechnique = :right_code");
        }

        Query query = getFilteredCurrentSession().createQuery(
                queryBuilder.toString());
        query.setParameter("abonneId", abonneId);
        if (!StringUtils.isEmpty(right)) {
            query.setParameter("right_code", right);
        }

        return (List<String>) query.list();
    }

    /**
     * get list of users for given role code and right code
     * @param role - mandatory
     * @param right - mandatory
     * @return
     */
    @Override
    public List<SimpleUserDTO> getUsersForRoleAndRight(String role, String right) {
        if (StringUtils.isEmpty(role) || StringUtils.isEmpty(right)) {
            LOGGER.error("getUsersForRoleAndRight: role and right cannot be null");
            throw new BadRequestMarsException();
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT distinct new com.z_s.dao.dto.user.SimpleUserDTO(dm.user.idUser, dm.user.nom, dm.user.prenom) " +
                " FROM  UtilisateurDroitsMatrice dm  " +
                " WHERE dm.droit.libTechnique = :right_code " +
                " AND dm.role.codeTech = :role_code");

        Query query = getFilteredCurrentSession().createQuery(
                queryBuilder.toString());

        query.setParameter("right_code", right);
        query.setParameter("role_code", role);

        return query.list();
    }

    /*
     * (non-Javadoc)
     * @see com.z_s.dao.user.IUserDAO#getPrestataireCandidates()
     */
    @Override
    public List<Organisation> getPrestataireCandidates() {

        String queryString = "SELECT o from Organisation o INNER JOIN o.roles r  WHERE r.codeTech = :roleCode";
        Query query = getFilteredCurrentSession().createQuery(queryString);
        query.setParameter("roleCode", RoleTypeEnum.ECSS.getTechCode());

        return query.list();
    }

    @Override
    public List<String> getPrestataireCandidatesIds() {

        String queryString = "SELECT o.idOrganisation from Organisation o INNER JOIN o.roles r  WHERE r.codeTech = :roleCode";
        Query query = getFilteredCurrentSession().createQuery(queryString);
        query.setParameter("roleCode", RoleTypeEnum.ECSS.getTechCode());

        return query.list();
    }

    @Override
    public UserAlerteDTO getUserAlerteById(String idUser) {
        UserAlerteDTO result = null;

        if(!StringUtils.isEmpty(idUser)) {
        	String queryString = "SELECT new com.z_s.dao.dto.user.UserAlerteDTO("
        			+ " u.idUser, u.nom, u.prenom, acc.email, lang.nom, c.formatDate, a.nom, o.nomOrganisation) "
					+ " FROM User as u"
					+ " LEFT JOIN u.abonne a"
					+ " LEFT JOIN a.organisation o"
					+ " LEFT JOIN a.culture c"
					+ " LEFT JOIN u.preferences.account acc"
					+ " LEFT JOIN u.preferences.appearance.language lang"
					+ " WHERE u.idUser = :idUser";

			Query query = getFilteredCurrentSession().createQuery(queryString);
			query.setParameter("idUser", idUser);

			result = (UserAlerteDTO)query.uniqueResult();
        }

        return result;
    }

    @Override
    public List<UserAlerteDTO> getUsersForAlerteByIds(List<String> userIds) {
        List<UserAlerteDTO> result = null;

        if(!StringUtils.isEmpty(userIds)) {
            String queryString = "SELECT new com.z_s.dao.dto.user.UserAlerteDTO("
                    + " u.idUser, u.nom, u.prenom, acc.email, upa.language.nom, c.formatDate, upa.noLinesPerPage) "
                    + " FROM User as u"
                    + " LEFT JOIN u.abonne.culture c"
                    + " LEFT JOIN u.preferences.account acc"
                    + " LEFT JOIN u.preferences.appearance upa "
                    + " WHERE u.idUser IN :userIds";

            Query query = getFilteredCurrentSession().createQuery(queryString);
            query.setParameterList("userIds", userIds.toArray(new String[userIds.size()]));

            result = query.list();
        }

        return result;
    }

	@Override
	public List<SimpleUserDTO> getUsersForRoleAndProfil(String role, String profil) {
        if (StringUtils.isEmpty(role) || StringUtils.isEmpty(profil)) {
            LOGGER.error("getUsersForRoleAndProfil: role and profil cannot be null");
            throw new BadRequestMarsException();
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT distinct new com.z_s.dao.dto.user.SimpleUserDTO(dm.user.idUser, dm.user.nom, dm.user.prenom) " +
                " FROM  UtilisateurDroitsMatrice dm  " +
                " WHERE dm.profil.idProfil = :profil " +
                " AND dm.role.codeTech = :role_code");

        Query query = getFilteredCurrentSession().createQuery(
                queryBuilder.toString());

        query.setParameter("profil", profil);
        query.setParameter("role_code", role);

        return query.list();
	}


	@Override
    public List<String> getPrestataireCandidateIds() {
        String queryString = "SELECT o.idOrganisation from Organisation o INNER JOIN o.roles r WHERE r.codeTech = :roleCode";

        Query query = getFilteredCurrentSession().createQuery(queryString);
        query.setParameter("roleCode", RoleTypeEnum.ECSS.getTechCode());

        return query.list();
    }

	@Override
	public List<SelectItemDTO> getUsersByNameAndOrgAndRole(String idOrganisation, RoleTypeEnum role, String searchUsername) {
		String queryString = "SELECT new com.z_s.dao.dto.SelectItemDTO(concat(u.nom,' ', u.prenom), concat(u.nom,' ', u.prenom)) FROM User u" +
		                " INNER JOIN u.roleOrganisationsUtilisateurs rou" +
		                //" INNER JOIN rou.organisations org" +
		                " INNER JOIN rou.role rol" +
		                " WHERE u.organisation.idOrganisation = :idOrganisation " +
		                " AND rol.codeTech=:roleCode ";

        if (!StringUtils.isEmpty(searchUsername)) {
            queryString += " AND " + HQLUtils.normalizeMember(" LOWER(concat(u.nom,' ', u.prenom)) ") + " LIKE LOWER(:searchUsername)";
        }

		Query query = getFilteredCurrentSession().createQuery(queryString);
		query.setParameter("roleCode", role.getTechCode());
		query.setParameter("idOrganisation", idOrganisation);

        if (!StringUtils.isEmpty(searchUsername)) {
            query.setParameter("searchUsername", buildPattern(searchUsername));
        }

		return query.list();
	}

	@Override
	public Long getUsersCount(String abonneId, String roleCode, String droitCode, UsersForAttributionStoreParams storeParams, String idGroup) {
		return (Long) getUsers(abonneId, roleCode, droitCode, storeParams, idGroup, true);
	}

    @Override
    public Date getTokenCreateDateByToken(String accessToken) {
		Date result = null;

        String queryString = "SELECT u.tokenCreateDate from User u WHERE u.accessToken = :accessToken";

        Query query = getFilteredCurrentSession().createQuery(
                queryString);
        query.setParameter("accessToken", accessToken);

        List<Date> dateList = query.list();

        if (dateList != null && !dateList.isEmpty()) {
            result = dateList.get(0);
        }

        return result;
    }

    @Override
    public UserLanguageDTO getUserLanguageByToken(String accessToken) {
        String queryString = "SELECT new com.z_s.dao.dto.user.UserLanguageDTO(u.loginName, up.appearance.language.nom) from User u " +
				"LEFT JOIN u.preferences up " +
				"WHERE u.accessToken = :accessToken ";

        Query query = getFilteredCurrentSession().createQuery(
                queryString);
        query.setParameter("accessToken", accessToken);
        query.setMaxResults(1);

        return (UserLanguageDTO) query.uniqueResult();
    }

    @Override
	public List<AttributionUserDTO> getUsers(String abonneId, String roleCode, String droitCode,
					UsersForAttributionStoreParams storeParams, String idGroup) {
		return (List<AttributionUserDTO>) getUsers(abonneId, roleCode, droitCode, storeParams, idGroup, false);
	}

	private Object getUsers(String abonneId, String roleCode, String droitCode, UsersForAttributionStoreParams storeParams, String idGroup, boolean count){
		Object result;
		if(!StringUtils.isEmpty(abonneId) && !StringUtils.isEmpty(roleCode) && !StringUtils.isEmpty(droitCode) ) {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT ")
				.append(count ?" count(distinct rou.user.idUser) " :
							   " distinct new com.z_s.dao.dto.user.AttributionUserDTO(rou.user.idUser, rou.user.nom, rou.user.prenom) ")
				.append(" FROM RoleOrganisationsUtilisateurs rou ")
				.append(" LEFT JOIN rou.profilDroits pd ")
				.append(" LEFT JOIN pd.droits d ")
				.append(" WHERE ")
				.append(" rou.user.abonne.idAbonne like :abonneId ")
				.append(" AND rou.role.codeTech like :roleCode ")
				.append(" AND d.libTechnique like :droitCode ")
				.append(" AND rou.user.supprime = FALSE ");

			String search = storeParams.getSearch();
			if(search != null){
				queryString.append(" AND ( ")
					.append(HQLUtils.normalizeMember(" LOWER(rou.user.nom) "))
					.append(" like LOWER(:search) OR ")
					.append(HQLUtils.normalizeMember(" LOWER(rou.user.prenom) "))
					.append(" like LOWER(:search) ")
					.append(" ) ");
			}
			List<String> usersInGroup = null;
			if (idGroup != null) {
				Query groupQuery = getFilteredCurrentSession().createQuery("SELECT u.id FROM UserGroup ug " +
						"LEFT JOIN ug.utilisateurs u " +
						"WHERE ug.id = :userGroupId");
				groupQuery.setParameter("userGroupId", idGroup);
				usersInGroup = groupQuery.list();
				if (!usersInGroup.isEmpty()) {
					queryString.append(" AND rou.user.idUser IN (:usersInGroup) ");
				}
			}
			if (!count){
				queryString.append(" GROUP BY rou.user.idUser, rou.user.nom, rou.user.prenom");

                Map<String, String> sorterMap = new HashMap<>();
                sorterMap.put("nom","rou.user.nom");
                sorterMap.put("prenom","rou.user.prenom");

                queryString.append(EntitySortCriteria.adz_srtCriterias(storeParams.getSortCriterias(), sorterMap));
			}

			Query query = getFilteredCurrentSession().createQuery(queryString.toString());
			query.setParameter("abonneId", abonneId);
			query.setParameter("roleCode", roleCode);
			query.setParameter("droitCode", droitCode);
			if(search != null) {
				query.setParameter("search", buildPattern(search));
			}
			if (idGroup != null && usersInGroup != null && !usersInGroup.isEmpty()) {
				query.setParameterList("usersInGroup", usersInGroup);
			}
			if(!count){
                HqlQueryHelper.addPaginationToQuery(query,storeParams);
			}
			result = count? query.uniqueResult() : query.list();
		}else {
			result = count? 0L: new ArrayList<AttributionUserDTO>();

		}
		return result;
	}

    @Override
    public SimpleUserDTO getSimpleUserById(String idUser) {
		String queryString = "SELECT new com.z_s.dao.dto.user.SimpleUserDTO(u.idUser, u.nom, u.prenom, o.nomOrganisation, o.idOrganisation) "
				+ "FROM User u JOIN u.organisation o WHERE u.idUser = :idUser";

		Query query = getFilteredCurrentSession().createQuery(queryString);
		query.setParameter("idUser", idUser);

		return (SimpleUserDTO) query.uniqueResult();
	}

	@Override
	public String getRoleForUserId(String idUser) {

		String queryString = "SELECT rol.codeTech FROM User u" +
				" INNER JOIN u.roleOrganisationsUtilisateurs rou" +
				" INNER JOIN rou.role rol" +
				" WHERE u.idUser=:idUser";

		Query query = getFilteredCurrentSession().createQuery(queryString);
		query.setParameter("idUser", idUser);

		return (String) query.uniqueResult();
	}

    @Override
    public List<User> findUsersNotLoginCreateDate() {

        String queryString = "SELECT u FROM User u " +
                "WHERE u.idUser not in (SELECT DISTINCT a.user.idUser FROM AuthenticationHistory a ) and u.actif = true and u.loginName != 'admintenant'";
        Query query = getFilteredCurrentSession().createQuery(queryString);

        return query.list();

    }

    @Override
    public List<String> findActiveUsersByLastPasswordUpdateBeforeDate(Date date) {
	    Query query = getFilteredCurrentSession().createQuery("select u.idUser from User u " +
                "left join u.preferences p " +
                "where u.actif = :active " +
                "and p.account.lastPasswordUpdate <= :lastDate " +
				"and p.account.passwordLocked = :locked");
	    query.setParameter("active", Boolean.TRUE);
	    query.setParameter("lastDate", date);
	    query.setParameter("locked", Boolean.FALSE);
        return query.list();
    }

	@Override
	public List<String> findActiveUsersByLastPasswordUpdateInDateRange(Date start, Date end) {
		Query query = getFilteredCurrentSession().createQuery("select u.idUser from User u " +
				"left join u.preferences p " +
				"where u.actif = :active " +
				"and p.account.lastPasswordUpdate between :startDate and :endDate " +
				"and p.account.passwordLocked = :locked)");
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("startDate", start);
		query.setParameter("endDate", end);
		query.setParameter("locked", Boolean.FALSE);
		return query.list();
	}

	@Override
	public String getRandomUserFromOrg(String idOrganisation, String idRole) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT u.idUser FROM User u ")
				.append(" LEFT JOIN u.organisation org ")
				.append(" LEFT JOIN u.roleOrganisationsUtilisateurs rou ")
				.append(" LEFT JOIN rou.role rol ")
				.append(" LEFT JOIN rou.profilDroits pd ")
				.append(" LEFT JOIN pd.droits d ")
				.append(" WHERE u.actif = true ")
				.append(" AND u.system = false ")
				.append(" AND org.idOrganisation = :idOrganisation")
				.append(" AND rol.idRole = :idRole ")
				.append(" AND d.libTechnique like :droitCode ");

		Query query = getFilteredCurrentSession().createQuery(queryString.toString());

		query.setParameter("idOrganisation", idOrganisation);
		query.setParameter("idRole", idRole);
		query.setParameter("droitCode", DroitsEnum.MNG_DOSSIERS.getTechCode());
		query.setMaxResults(1);
		return (String) query.uniqueResult();
	}
	
	@Override 
	public User getUserByToken(String accessToken){
		
		  String queryString = "from User u " +
					"WHERE u.accessToken = :accessToken ";
	        Query query = getFilteredCurrentSession().createQuery(
	                queryString);
	        query.setParameter("accessToken", accessToken);
	        query.setMaxResults(1);
	        return (User) query.uniqueResult();
	}
	
}
