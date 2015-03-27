package com.referaice.web.config.security.social;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.encrypt.TextEncryptor;
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

import ch.qos.logback.core.rolling.helper.MonoTypedConverter;

import com.mongodb.WriteConcern;

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
		List<MongoConnection> results = mongoTemplate.find(query, MongoConnection.class);
		List<Connection<?>> l = new ArrayList<Connection<?>>();
		for (MongoConnection mc : results) {
			l.add(converter.convert(mc));
		}

		return l;
	}
	
	private List<Connection<?>> runConverter( List<MongoConnection> mongoConnections ){
		
		List<Connection<?>> l = new ArrayList<Connection<?>>();
		for( MongoConnection mc : mongoConnections ){
			l.add( converter.convert(mc) );
		}
		
		return l;
	}

	public MultiValueMap<String, Connection<?>> findAllConnections() {
		
		PageRequest pageParam = new PageRequest(0, 2, new Sort(Sort.Direction.ASC, "providerId"));
		List<MongoConnection> mongoConnections = socialMongoConnectionRepository.findByUserId(userId, pageParam);
		List<Connection<?>> resultList = runConverter(mongoConnections);

//		Query q = query(where("userId").is(userId)).with( new Sort(Sort.Direction.ASC, "providerId") ).with(new Sort(Sort.Direction.ASC, "rank"));
//		List<Connection<?>> resultList = runQuery(q);

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
		List<MongoConnection> mongoConnections = socialMongoConnectionRepository.findByUserIdAndProviderId(userId, providerId, pageable);
		List<Connection<?>> resultList = runConverter(mongoConnections);
		
		return resultList;
		
//		Query q = new Query(where("userId").is(userId).and("providerId").is(providerId)).with( new Sort(Sort.Direction.ASC, "rank") );
//		return runQuery(q);
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

		Query q = new Query(criteria).with( new Sort(Sort.Direction.ASC, "providerId") ).with( new Sort(Sort.Direction.ASC, "rank") );

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

			Query q = query(where("userId").is(userId).and("providerId").is(connectionKey.getProviderId()).and("providerUserId")
					.is(connectionKey.getProviderUserId()));

			MongoConnection mc = mongoTemplate.findOne(q, MongoConnection.class);

			return converter.convert(mc);

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

			Query q = query(where("userId").is(userId).and("providerId").is(data.getProviderId()));
			MongoConnection cnn = mongoTemplate.findOne(q, MongoConnection.class);

			int rank = cnn == null ? 1 : cnn.getRank() + 1;

			MongoConnection mongoCnn = converter.convert(connection);
			mongoCnn.setUserId(userId);
			mongoCnn.setRank(rank);
			
			socialMongoConnectionRepository.insert(mongoCnn);
			
//			mongoTemplate.insert(mongoCnn);

		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}

	@Transactional
	public void updateConnection(Connection<?> connection) {
		MongoConnection mongoCnn = converter.convert(connection);
		mongoCnn.setUserId(userId);
		try {
//			mongoTemplate.setWriteConcern(WriteConcern.SAFE);
			
			List<MongoConnection> mongoConnections = socialMongoConnectionRepository.findByUserIdAndProviderIdAndProviderUserId(userId, mongoCnn.getProviderId(), mongoCnn.getProviderUserId());
			
			if( !mongoConnections.isEmpty() && mongoConnections.size() == 1 ){
				mongoCnn.setId( mongoConnections.get(0).getId() );
				
				socialMongoConnectionRepository.save(mongoCnn);
//				mongoTemplate.save(mongoCnn);
			}
			

			
		} catch (DuplicateKeyException e) {
			Query q = query(where("userId").is(userId).and("providerId").is(mongoCnn.getProviderId()).and("providerUserId").is(mongoCnn.getProviderUserId()));

			Update update = Update.update("expireTime", mongoCnn.getExpireTime()).set("accessToken", mongoCnn.getAccessToken())
					.set("profileUrl", mongoCnn.getProfileUrl()).set("imageUrl", mongoCnn.getImageUrl()).set("displayName", mongoCnn.getDisplayName());

			mongoTemplate.findAndModify(q, update, MongoConnection.class);
		}
	}

	@Transactional
	public void removeConnections(String providerId) {
		Query q = query(where("userId").is(userId).and("providerId").is(providerId)).with(new Sort(Sort.Direction.DESC, "rank"));

		mongoTemplate.remove(q, MongoConnection.class);
	}

	@Transactional
	public void removeConnection(ConnectionKey connectionKey) {
		Query q = query(where("userId").is(userId).and("providerId").is(connectionKey.getProviderId()).and("providerUserId")
				.is(connectionKey.getProviderUserId()));
		mongoTemplate.remove(q, MongoConnection.class);
	}

	// internal helpers

	private Connection<?> findPrimaryConnection(String providerId) {
		Query q = query(where("userId").is(userId).and("providerId").is(providerId).and("rank").is(1));

		MongoConnection mc = mongoTemplate.findOne(q, MongoConnection.class);
		return converter.convert(mc);
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}

}
