package com.referaice.model.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.referaice.model.entitties.login.SocialMongoConnection;

public interface SocialMongoConnectionRepository extends MongoRepository<SocialMongoConnection, String> {

	// Query q = query(where("userId").is(userId)).with( new Sort(Sort.Direction.ASC, "providerId") ).with(new Sort(Sort.Direction.ASC, "rank"));
	List<SocialMongoConnection> findByUserId(String userId, Pageable pageable);

	// new Query(where("userId").is(userId).and("providerId").is(providerId)).with( new Sort(Sort.Direction.ASC, "rank")
	List<SocialMongoConnection> findByUserIdAndProviderId(String userId, String providerId, Pageable pageable);
	
	List<SocialMongoConnection> findByProviderIdAndProviderUserId(String providerId, String providerUserId);
	
	Optional<SocialMongoConnection> findByUserIdAndProviderId(String userId, String providerId);
	
	Optional<SocialMongoConnection> findByUserIdAndProviderIdAndRank(String userId, String providerId, int rank);
	
	Long deleteByUserIdAndProviderId(String userId, String providerId);
	
	Long deleteByUserIdAndProviderIdAndProviderUserId(String userId, String providerId, String providerUserId);

	Optional<SocialMongoConnection> findByUserIdAndProviderIdAndProviderUserId(String userId, String providerId, String providerUserId);

}
