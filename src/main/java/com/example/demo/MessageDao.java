package com.example.demo;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

@RedisHash("MessageDao")
public class MessageDao implements Serializable {
	
	private @Id String id;
	private String message;
	private String date;
	private String sender;
	private String receiver;
	private String channel;
	private String nameSender;
	private String nameReceiver;
	

	private long objectid;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public long getObjectid() {
		return objectid;
	}
	public void setObjectid(long objectid) {
		this.objectid = objectid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNameReceiver() {
		return nameReceiver;
	}
	public void setNameReceiver(String nameReceiver) {
		this.nameReceiver = nameReceiver;
	}
	public String getNameSender() {
		return nameSender;
	}
	public void setNameSender(String nameSender) {
		this.nameSender = nameSender;
	}

	

}
