package com.example.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class MessageRepository{
	
     private HashOperations hashOperations;
     private final String TABLE="CONVERSATION";
	
	public MessageRepository(RedisTemplate<String,MessageDao> redisTemplate) {
		this.hashOperations = redisTemplate.opsForHash();
	}
	
	public void saveMessage(MessageDao message) {
		List<MessageDao> conversation; 
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			String strConversation;
			
			if(hashOperations.hasKey(TABLE, message.getId())) {
				try {
				strConversation = (String)hashOperations.get(TABLE, message.getId());
				}catch(Exception e) {
					deleteTable(message.getId());
					strConversation="";
				}
				conversation = (List<MessageDao>)mapper.readValue(strConversation, Collection.class);
			}else {
				conversation=new ArrayList<MessageDao>();
			}
			conversation.add(message);
			strConversation = mapper.writeValueAsString(conversation);
			hashOperations.put(TABLE, message.getId(), strConversation);
	        System.err.println("User with ID %s saved"+message.getId());
	        //System.err.println(hashOperations.entries("CONVERSATION"));
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MessageDao> getConversation(String idConversation){
		
		List<MessageDao> lstConversation;
		ObjectMapper mapper = new ObjectMapper();
		String strConversation;
		
		try {
			
			if(hashOperations.hasKey(TABLE, idConversation)) {
				
				strConversation =(String)hashOperations.get(TABLE, idConversation);
				lstConversation =(List<MessageDao>)mapper.readValue(strConversation, Collection.class);
				
			}else
				lstConversation=new ArrayList<MessageDao>();
			
		}catch(Exception e) {
			e.printStackTrace();
			lstConversation=new ArrayList<MessageDao>();
		}
		
		return lstConversation;
	}
	
	public void deleteTable(String id) {
		try {
			hashOperations.delete(TABLE, id);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
