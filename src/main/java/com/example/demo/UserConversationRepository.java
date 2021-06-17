package com.example.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class UserConversationRepository {
	
	private HashOperations hashOperations;
	private final String TABLE="USER_CONVERSATION";

	
	public UserConversationRepository(RedisTemplate<String,UserConversationDao> redisTemplate) {
		this.hashOperations = redisTemplate.opsForHash();
	}
	
	public boolean exists(UserConversationDao userConversation) {
		boolean exists=false;
		try {
			
			boolean hasit =hashOperations.hasKey(TABLE, userConversation.getId());
			
			System.err.println("hasit:"+hasit);
			
			return hasit;
			
			
		}catch(Exception e) {
			e.printStackTrace();
			exists=false;
		}
		
		return exists;
	}
	
	public void saveUserConversation(UserConversationDao userConversation) {
		List<HashMap<String,UserConversationDao>> lstConversations;
		ObjectMapper mapper = new ObjectMapper();
		String strConversations;
		try {
			if(exists(userConversation)) {
				strConversations=(String)hashOperations.get(TABLE, userConversation.getId());
				lstConversations = (List<HashMap<String,UserConversationDao>>)mapper.readValue(strConversations, Collection.class);
				
			}else {
				lstConversations=new ArrayList<HashMap<String,UserConversationDao>>();
				HashMap<String,UserConversationDao> map=new HashMap<String,UserConversationDao>();
				lstConversations.add(0,map);
				
			}
			
			HashMap<String,UserConversationDao> map = lstConversations.get(0);
			if(!map.containsKey(userConversation.getUsername())) {
				map.put(userConversation.getUsername(), userConversation);
				lstConversations.set(0, map);
				strConversations = mapper.writeValueAsString(lstConversations);
				hashOperations.put(TABLE, userConversation.getId(), strConversations);
			}
				
			//System.err.println(hashOperations.entries(TABLE));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		hashOperations.put(TABLE, userConversation.getIdConversation(), userConversation);
        System.err.println("UserConversation with ID %s saved:"+userConversation.getId());
	}
	
	public List<UserConversationDao> getConversations(String username){
		
		List<UserConversationDao> lstConversations;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			if(hashOperations.hasKey(TABLE, username)) {
				
				String conversations = (String)hashOperations.get(TABLE, username);
				System.err.println("Conversations:"+conversations);
				List<HashMap<String,HashMap<String,String>>> tmpConversations = (List<HashMap<String,HashMap<String,String>>>)mapper.readValue(conversations, Collection.class);
				HashMap<String,HashMap<String,String>> map =(HashMap<String,HashMap<String,String>>)tmpConversations.get(0);
				Iterator<Entry<String, HashMap<String,String>>> itr =map.entrySet().iterator();
				lstConversations=new ArrayList<UserConversationDao>();
				while(itr.hasNext()) {
					Entry<String, HashMap<String,String>> entry =itr.next();
					HashMap<String,String> map2=entry.getValue();
					Iterator<Entry<String,String>>itr2=map2.entrySet().iterator();
					UserConversationDao dao = new UserConversationDao();
					while(itr2.hasNext()) {
						Entry<String,String> entry2 =itr2.next();
						String field =entry2.getKey();
						String value = entry2.getValue();
						
						if(field.equals("id"))
							dao.setId(value);
						else if(field.equals("idConversation"))
							dao.setIdConversation(value);
						else if(field.equals("username"))
							dao.setUsername(value);
						else if(field.equals("name"))
							dao.setName(value);
						else if(field.equals("objectid"))
							dao.setObjectid(value);
					}
					lstConversations.add(dao);
				}
			}else {
				lstConversations=new ArrayList<UserConversationDao>();
			}
		}catch(Exception e) {
			e.printStackTrace();
			lstConversations=new ArrayList<UserConversationDao>();
		}
		
		return lstConversations;
	}
	
	public void deleteUserConversation(String id){
		hashOperations.delete(TABLE, id);
	}

}
