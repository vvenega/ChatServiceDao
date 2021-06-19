package com.example.demo;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ChatServiceDaoController {
	
	public static void main(String args[]) {
		
		MessageRepository messageRepository;
		
		MessageDao messageDao=new MessageDao();
		messageDao.setMessage("Hola, como estas");
 	   messageDao.setChannel("mychannel_vvenega");
 	   messageDao.setId("vvenega:owner3759:6238551");
 	   messageDao.setObjectid(6238551);
 	   messageDao.setReceiver("owner3759");
 	   messageDao.setSender("vvenega");
		
		try {
			
			RedisConfig config = new RedisConfig();
			config.topic=messageDao.getChannel();
			
			//ObjectMapper mapper = new ObjectMapper();
			//MessageDao messageDao = mapper.readValue(message.toString(), MessageDao.class);
			/*System.err.println("Subscriber:"+messageDao.getObjectid());
			System.err.println("Sender:"+messageDao.getSender());
			System.err.println("Receiver:"+messageDao.getReceiver());
			
			
			
			messageRepository=new MessageRepository(config.redisTemplate());
			messageRepository.saveMessage(messageDao);*/
			
			/*UserConversationRepository userConRep= 
					new UserConversationRepository(config.redisTemplateUserConversation());
			
			userConRep.deleteUserConversation("vvenega");
			userConRep.deleteUserConversation("owner160");
			
			userConRep.getConversations("vvenega");
			UserConversationDao dao = new UserConversationDao();
			dao.setIdConversation(messageDao.getId());
			dao.setUsername(messageDao.getReceiver());
			dao.setId(dao.getIdConversation()+":"+dao.getUsername());
			
			userConRep.saveUserConversation(dao);*/
			
		 	new ChatServiceDaoController().publishMessage(messageDao.getMessage(),messageDao.getSender(),
					messageDao.getReceiver(),messageDao.getChannel(),
					messageDao.getId(),messageDao.getObjectid()+"",
					messageDao.getNameSender(),messageDao.getNameReceiver());

			}catch(Exception e) {
				e.printStackTrace();
			}
		
	}
	 
	private static Map<String,RedisMessageListenerContainer> containers = 
			new HashMap<String,RedisMessageListenerContainer>();

	
	@GetMapping("/BroadcastDAO/{message}/{sender}/{receiver}/{channel}/{id}/{objectid}/{namesender}/{namereceiver}")
	public boolean publishMessage (@PathVariable String message,
			@PathVariable String sender,@PathVariable String receiver,
			@PathVariable String channel, @PathVariable String id,
			@PathVariable String objectid,@PathVariable String namesender,
			@PathVariable String namereceiver) {
		
		       boolean result=false;
		       MessageDao messageDao = new MessageDao();
		       try {
		    	   messageDao.setMessage(message);
		    	   messageDao.setChannel(channel);
		    	   messageDao.setId(id);
		    	   messageDao.setObjectid(Long.parseLong(objectid));
		    	   messageDao.setReceiver(receiver);
		    	   messageDao.setSender(sender);
		    	   messageDao.setNameReceiver(namereceiver);
		    	   messageDao.setNameSender(namesender);
		    	   
		    	   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		           LocalDateTime now = LocalDateTime.now();
		           messageDao.setDate(dtf.format(now));
		    	   
		    	   RedisConfig config = new RedisConfig();
		    	   UserConversationRepository userConRep= 
			   				new UserConversationRepository(config.redisTemplateUserConversation());
		    	   /**Saving Conversation for Publisher**/

			   		UserConversationDao dao = new UserConversationDao();
			   		dao.setIdConversation(messageDao.getId());
			   		dao.setUsername(messageDao.getReceiver());
			   		dao.setId(messageDao.getSender());
			   		dao.setName(messageDao.getNameReceiver());
			   		dao.setObjectid(messageDao.getObjectid()+"");
			   		userConRep.saveUserConversation(dao);
			   		
			   		/**Saving Conversation for Subscriber**/
			   		UserConversationDao dao1 = new UserConversationDao();
			   		dao1.setIdConversation(messageDao.getId());
			   		dao1.setUsername(messageDao.getSender());
			   		dao1.setId(messageDao.getReceiver());
			   		dao1.setName(messageDao.getNameSender());
			   		dao1.setObjectid(messageDao.getObjectid()+"");
					userConRep.saveUserConversation(dao1);
					
					/** Saving message in List of messages **/
					MessageRepository messageRepository=new MessageRepository(config.redisTemplate());
					messageRepository.saveMessage(messageDao);
					

		    	   
		    	   /*RedisMessageListenerContainer container;
		    	   RedisConfig config = new RedisConfig();
	    		   config.topic=messageDao.getChannel();
		    	   
		    	   
		    	   if(!containers.containsKey(messageDao.getChannel())) {
	
			    	   container =config.redisContainer();
			    	   
			    	   MessageSubscriber chatter = new MessageSubscriber();
			    	   container.addMessageListener(chatter, config.topic());
			    	   
			    	   containers.put(messageDao.getChannel(), container);
			    	   
			   
		    	   }
	

		    	   MessagePublisher publisher = config.redisPublisher();
		    	   
		    	   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		           LocalDateTime now = LocalDateTime.now();
		           messageDao.setDate(dtf.format(now));
		    	   publisher.publish(messageDao);*/
		    	   
		       }catch(Exception e) {
		    	   e.printStackTrace();
		       }
		       
		       
		       return result;
		
	}
	
	@GetMapping("/GetConversationDAO/{idconversation}")
	public List<MessageDao> getConversation (@PathVariable String idconversation) {
		
		List<MessageDao> lstMessages;
		RedisConfig config = new RedisConfig();

		try {
			
			MessageRepository messageRepository=new MessageRepository(config.redisTemplate());
			lstMessages = messageRepository.getConversation(idconversation);
			
		}catch(Exception e) {
			e.printStackTrace();
			lstMessages=new ArrayList<MessageDao>();
		}
		
		return lstMessages;
		
	}
	
	@GetMapping("/GetConversationsDAO/{username}")
	public List getConversations (@PathVariable String username) {
		
		List lstConversations;
		RedisConfig config = new RedisConfig();

		try {
			
			UserConversationRepository userConversationRepository=new UserConversationRepository(config.redisTemplateUserConversation());
			lstConversations = userConversationRepository.getConversations(username);
			
		}catch(Exception e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());
			lstConversations=new ArrayList();
		}
		
		return lstConversations;
		
	}

}
