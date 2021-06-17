package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessagePublisher {
	
	@Autowired
	private RedisTemplate<String,MessageDao> redisTemplate;

	@Autowired
	public ChannelTopic topic;
	
	public MessagePublisher() {}
	
	public MessagePublisher(RedisTemplate<String,MessageDao> redisTemplate, ChannelTopic topic) {
		this.redisTemplate = redisTemplate;
		this.topic = topic;
	}

	public void publish(MessageDao message) {
		
		try {
		ObjectMapper mapper = new ObjectMapper();
		String strMessage = mapper.writeValueAsString(message);
		RedisConfig config = new RedisConfig();
		config.topic=message.getChannel();
		
		MessageRepository messageRepository= new MessageRepository(config.redisTemplate());
		
		UserConversationRepository userConRep= 
				new UserConversationRepository(config.redisTemplateUserConversation());
		
		UserConversationDao dao = new UserConversationDao();
		dao.setIdConversation(message.getId());
		dao.setUsername(message.getReceiver());
		dao.setId(message.getSender());
		dao.setName(message.getNameReceiver());
		dao.setObjectid(message.getObjectid()+"");
		userConRep.saveUserConversation(dao);
		
		redisTemplate.convertAndSend(topic.getTopic(), strMessage);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
