package com.referaice.web.config.security.social;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

import com.referaice.model.entitties.SocialMongoConnection;
import com.referaice.model.repository.mongo.SocialMongoConnectionRepository;

public class MongoUsersConnectionRepository implements UsersConnectionRepository {

	private final MongoTemplate mongoTemplate;

	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final SocialMongoConnectionRepository socialMongoConnectionRepository;

	private final TextEncryptor textEncryptor;

	private ConnectionSignUp connectionSignUp;

	public MongoUsersConnectionRepository(MongoTemplate mongoTemplate, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor,
			SocialMongoConnectionRepository socialMongoConnectionRepository) {
		this.mongoTemplate = mongoTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.socialMongoConnectionRepository = socialMongoConnectionRepository;
	}

	/**
	 * The command to execute to create a new local user profile in the event no user id could be mapped to a connection. Allows for implicitly creating a user
	 * profile from connection data during a provider sign-in attempt. Defaults to null, indicating explicit sign-up will be required to complete the provider
	 * sign-in attempt.
	 * 
	 * @see #findUserIdsWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}

	public List<String> findUserIdsWithConnection(Connection<?> connection) {

		ConnectionKey key = connection.getKey();
		
		List<SocialMongoConnection> results = socialMongoConnectionRepository.findByProviderIdAndProviderUserId(key.getProviderId(), key.getProviderUserId());

		List<String> localUserIds = new ArrayList<String>();
		for (SocialMongoConnection mc : results) {
			localUserIds.add(mc.getUserId());
		}

		if (localUserIds.size() == 0 && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null) {
				createConnectionRepository(newUserId).addConnection(connection);
				return Arrays.asList(newUserId);
			}
		}
		return localUserIds;

	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {

		Query q = query(where("providerId").is(providerId).and("providerUserId").in(new ArrayList<String>(providerUserIds)));
		q.fields().include("userId");

		List<SocialMongoConnection> results = mongoTemplate.find(q, SocialMongoConnection.class);
		Set<String> userIds = new HashSet<String>();
		for (SocialMongoConnection mc : results) {
			userIds.add(mc.getUserId());
		}

		return userIds;

	}

	@Autowired
	private ConnectionConverter converter;

	public ConnectionRepository createConnectionRepository(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId cannot be null");
		}

		return new MongoConnectionRepository(userId, mongoTemplate, connectionFactoryLocator, textEncryptor, converter, socialMongoConnectionRepository);
	}

}
