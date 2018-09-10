package com.z_s.dao.user;

import com.z_s.dao.dto.SelectItemDTO;
import com.z_s.dao.dto.user.AttributionUserDTO;
import com.z_s.dao.dto.user.SimpleUserDTO;
import com.z_s.dao.dto.user.UserAlerteDTO;
import com.z_s.dao.dto.user.UserLanguageDTO;
import com.z_s.dao.generic.IGenericDAO;
import com.z_s.dao.util.EntitySortCriteria;
import com.z_s.dao.util.storeparams.UserStoreParams;
import com.z_s.dao.util.storeparams.UsersForAttributionStoreParams;
import com.z_s.model.organisation.Organisation;
import com.z_s.model.user.RoleTypeEnum;
import com.z_s.model.user.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface IUserDAO extends IGenericDAO<User> {

	/**
	 * @param loginName
	 * @return
	 */
    User findByLoginName(String loginName);
	
	/**
	 * @param loginName
	 * @return
	 */
    User findByCredentials(String loginName, String password);
	
	/**
	 * @param loginName
	 * @return
	 */
    User findByLoginNameIfActive(String loginName);


	/**
     *
     * @param userStoreParams
     * @param abonneIds
     * @param lang
     * @return
     */
    Long findUsersWithPaginationAndSearchCount(UserStoreParams userStoreParams, Collection<String> abonneIds, String lang);

    /**
     *
     * @param userStoreParams
     * @param abonneIds
     * @param sortCriteria
     * @param lang
     * @return
     */
    List<User> findUsersWithPaginationAndSearch(UserStoreParams userStoreParams, Collection<String> abonneIds, List<EntitySortCriteria> sortCriteria, String lang);

	/**
	 * Checks if the user LoginName is unique (that is if other users with same LoginName exists)
	 * @param name
	 * @return
	 */
    boolean isNameUnique(String name);

	boolean hasActiveAccount(final String userId);

	/**
	 * @param abonneId
	 * @param right
	 * @return
	 */
    List<String> getUsersForAbonneeWithRight(String abonneId, String right);
	
	
	/**
	 * Gets the existing prestataires for a user and a specific role. 
	 *  
	 * @return
	 */
    List<Organisation> getPrestataireCandidates();
	
	/**
	 * 
	 * @return Gets the ids of existing prestataires for a user and a specific role. 
	 */
    List<String> getPrestataireCandidatesIds();
	
	/**
	 * 
	 * @param idUser
	 * @return
	 */
    UserAlerteDTO getUserAlerteById(String idUser);

    /**
     *
     * @param userIds
     * @return
     */
    List<UserAlerteDTO> getUsersForAlerteByIds(List<String> userIds);

	/**
	 *
	 * @param role
	 * @param right
	 * @return
	 */
    List<SimpleUserDTO> getUsersForRoleAndRight(String role, String right);
	
	/**
	 * 
	 * @param role
	 * @param profil
	 * @return
	 */
    List<SimpleUserDTO> getUsersForRoleAndProfil(String role, String profil);


	/**
	 * @return
	 */
    List<String> getPrestataireCandidateIds();
	
	SimpleUserDTO getSimpleUserById(String idUser);

	List<AttributionUserDTO> getUsers(String abonneId, String roleCode, String droitCode, UsersForAttributionStoreParams storeParams, String idGroup);

    List<SelectItemDTO> getUsersByNameAndOrgAndRole(String idOrganisation, RoleTypeEnum role, String searchUsername);

	Long getUsersCount(String abonneId, String roleCode, String droitCode, UsersForAttributionStoreParams storeParams, String idGroup);

    Date getTokenCreateDateByToken(String accessToken);

    UserLanguageDTO getUserLanguageByToken(String accessToken);

	String getRoleForUserId(String idUser);
	
	List<User> findUsersNotLoginCreateDate();

	List<String> findActiveUsersByLastPasswordUpdateBeforeDate(Date date);

	List<String> findActiveUsersByLastPasswordUpdateInDateRange(Date start, Date end);

	/**
	 *
	 * @param idOrganisation
	 * @param idRole
	 * @return id of a random active, non-system user with provided role from an organisation
	 */
	String getRandomUserFromOrg(String idOrganisation, String idRole);

	User getUserByToken(String accessToken);
}
