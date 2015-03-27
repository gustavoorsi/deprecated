package com.referaice.web.config.security.social;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SocialMongoConnectionRepository extends MongoRepository<MongoConnection, String> {

	// Query q = query(where("userId").is(userId)).with( new Sort(Sort.Direction.ASC, "providerId") ).with(new Sort(Sort.Direction.ASC, "rank"));
	List<MongoConnection> findByUserId(String userId, Pageable pageable);

	// new Query(where("userId").is(userId).and("providerId").is(providerId)).with( new Sort(Sort.Direction.ASC, "rank")
	List<MongoConnection> findByUserIdAndProviderId(String userId, String providerId, Pageable pageable);

	List<MongoConnection> findByUserIdAndProviderIdAndProviderUserId(String userId, String providerId, String providerUserId);

}
