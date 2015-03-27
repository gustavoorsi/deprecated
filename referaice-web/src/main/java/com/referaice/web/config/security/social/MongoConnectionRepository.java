package com.referaice.web.config.security.social;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.referaice.model.entitties.login.SocialMongoConnection;
import com.referaice.model.repository.mongo.SocialMongoConnectionRepository;

public class MongoConnectionRepository implements ConnectionRepository {

	private final String userId;

	private final MongoTemplate mongoTemplate;

	private final SocialMongoConnectionRepository socialMongoConnectionRepository;

	private final ConnectionConverter converter;

	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;

	public MongoConnectionRepository(String userId, MongoTemplate mongoTemplate, ConnectionFactoryLocator connectionFactoryLocator,
			TextEncryptor textEncryptor, ConnectionConverter converter, SocialMongoConnectionRepository socialMongoConnectionRepository) {
		this.userId = userId;
		this.mongoTemplate = mongoTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.converter = converter;
		this.socialMongoConnectionRepository = socialMongoConnectionRepository;
	}

	private List<Connection<?>> runQuery(Query query) {
		List<SocialMongoConnection> results = mongoTemplate.find(query, SocialMongoConnection.class);
		List<Connection<?>> l = new ArrayList<Connection<?>>();
		for (SocialMongoConnection mc : results) {
			l.add(converter.convert(mc));
		}

		return l;
	}

	private List<Connection<?>> runConverter(List<SocialMongoConnection> mongoConnections) {

		List<Connection<?>> l = new ArrayList<Connection<?>>();
		for (SocialMongoConnection mc : mongoConnections) {
			l.add(converter.convert(mc));
		}

		return l;
	}

	public MultiValueMap<String, Connection<?>> findAllConnections() {

		PageRequest pageParam = new PageRequest(0, 2, new Sort(Sort.Direction.ASC, "providerId"));
		List<SocialMongoConnection> mongoConnections = socialMongoConnectionRepository.findByUserId(userId, pageParam);
		List<Connection<?>> resultList = runConverter(mongoConnections);

		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<Connection<?>> emptyList());
		}
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	public List<Connection<?>> findConnections(String providerId) {

		PageRequest pageable = new PageRequest(0, 2, new Sort(Sort.Direction.ASC, "providerId"));
		List<SocialMongoConnection> mongoConnections = socialMongoConnectionRepository.findByUserIdAndProviderId(userId, providerId, pageable);
		List<Connection<?>> resultList = runConverter(mongoConnections);
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers == null || providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}

		List<Criteria> lc = new ArrayList<Criteria>();
		for (Entry<String, List<String>> entry : providerUsers.entrySet()) {
			String providerId = entry.getKey();

			lc.add(where("providerId").is(providerId).and("providerUserId").in(entry.getValue()));
		}

		Criteria criteria = where("userId").is(userId);
		criteria.orOperator(lc.toArray(new Criteria[lc.size()]));

		Query q = new Query(criteria).with(new Sort(Sort.Direction.ASC, "providerId")).with(new Sort(Sort.Direction.ASC, "rank"));

		List<Connection<?>> resultList = runQuery(q);

		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<Connection<?>>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;

	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {

			SocialMongoConnection socialMongoConnection = socialMongoConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId,
					connectionKey.getProviderId(), connectionKey.getProviderUserId()).orElseThrow(
					() -> new ResourceNotFoundException(connectionKey.getProviderId(), "Not found"));

			return converter.convert(socialMongoConnection);

		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Transactional
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();

			SocialMongoConnection cnn = socialMongoConnectionRepository.findByUserIdAndProviderId(userId, data.getProviderId()).orElseThrow(
					() -> new ResourceNotFoundException(data.getProviderId(), "Not found"));

			int rank = cnn == null ? 1 : cnn.getRank() + 1;

			SocialMongoConnection mongoCnn = converter.convert(connection);
			mongoCnn.setUserId(userId);
			mongoCnn.setRank(rank);

			socialMongoConnectionRepository.save(mongoCnn);

		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}

	@Transactional
	public void updateConnection(Connection<?> connection) {
		// convert the social connection to an instance of our domain social connection. Connection -> SocialMongoConnection
		SocialMongoConnection toSave = converter.convert(connection);

		// lets find the already persisted connection just to get the id. (it would be better to make userId + providerId + providerUserId a composite key in
		// mongo).
		SocialMongoConnection socialMongoConnection = socialMongoConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId,
				toSave.getProviderId(), toSave.getProviderUserId()).orElseThrow(() -> new ResourceNotFoundException(toSave.getProviderId(), "Not found"));

		// save the values.
		toSave.setUserId(userId);
		toSave.setId(socialMongoConnection.getId()); // set the mongo primary key so it updates the existing social connection instead of creating a new one.

		// update.
		socialMongoConnectionRepository.save(toSave);

	}

	@Transactional
	public void removeConnections(String providerId) {
		socialMongoConnectionRepository.deleteByUserIdAndProviderId(providerId, providerId);
	}

	@Transactional
	public void removeConnection(ConnectionKey connectionKey) {

		socialMongoConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
	}

	// internal helpers

	private Connection<?> findPrimaryConnection(String providerId) {

		SocialMongoConnection socialMongoConnection = socialMongoConnectionRepository.findByUserIdAndProviderIdAndRank(providerId, providerId, 1).orElse(null);
		return converter.convert(socialMongoConnection);
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}

}
