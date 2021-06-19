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
			String usernameObjectid=userConversation.getUsername()+":"+userConversation.getObjectid();
			if(!map.containsKey(usernameObjectid)) {
				map.put(usernameObjectid, userConversation);
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
	
	public List getConversations(String username){
		
		//List<UserConversationDao> lstConversations;
		ObjectMapper mapper = new ObjectMapper();
		
		ArrayList response=new ArrayList();
		HashMap<String,List<UserConversationDao>> hmpConversations = new HashMap<String,List<UserConversationDao>>();
		
		try {
			if(hashOperations.hasKey(TABLE, username)) {
				
				String conversations = (String)hashOperations.get(TABLE, username);
				System.err.println("Conversations:"+conversations);
				List<HashMap<String,HashMap<String,String>>> tmpConversations = (List<HashMap<String,HashMap<String,String>>>)mapper.readValue(conversations, Collection.class);
				HashMap<String,HashMap<String,String>> map =(HashMap<String,HashMap<String,String>>)tmpConversations.get(0);
				Iterator<Entry<String, HashMap<String,String>>> itr =map.entrySet().iterator();
				List<UserConversationDao> lstConversations=null;
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
					
					if(hmpConversations.containsKey(dao.getUsername()))
						lstConversations = hmpConversations.get(dao.getUsername());
					else 
						lstConversations=new ArrayList<UserConversationDao>();
					
					lstConversations.add(dao);
					hmpConversations.put(dao.getUsername(), lstConversations);
				}
			}else {
				//lstConversations=new ArrayList<UserConversationDao>();
				hmpConversations = new HashMap<String,List<UserConversationDao>>();
			}
		}catch(Exception e) {
			//e.printStackTrace();
			//lstConversations=new ArrayList<UserConversationDao>();
			System.err.println(e.getMessage());
			hmpConversations = new HashMap<String,List<UserConversationDao>>();
		}
		
		Iterator<Entry<String,List<UserConversationDao>>>itr2= hmpConversations.entrySet().iterator();
			
			while(itr2.hasNext()) {
				Map.Entry<String,List<UserConversationDao>> entry =itr2.next();
				
				List<UserConversationDao> list = entry.getValue();

				
				Iterator<UserConversationDao> itr3 = list.iterator();
				
				int cont=0;
				
				while(itr3.hasNext()) {
					UserConversationDao request = itr3.next();
					
					if(cont==0) {
						cont++;
						ChatterDao group = new ChatterDao();
						//group.setName(request.getName()+"_"+request.getObjectid());
						group.setUsername(request.getUsername());
						group.setGroup(request.getName());
						
						response.add(group);
						
					}
					
					response.add(request);
				}
				
			}
		
		//return lstConversations;
		return response;
	}
	
	public void deleteUserConversation(String id){
		hashOperations.delete(TABLE, id);
	}

}
