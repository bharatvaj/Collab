package com.thing.allied.model;



public class ChatMetadata {
    
    private String name;
    
    private String members;
    
    private String chatCollectionUrl;
    
    
    
    
    public ChatMetadata(String chatCollectionUrl, String members, String name) {
        this.chatCollectionUrl = chatCollectionUrl;
        this.members = members;
        this.name = name;
    }
    

    
    public String getName() {
        return this.name;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    
    public String getMembers() {
        return this.members;
    }
    
    
    public void setMembers(String members) {
        this.members = members;
    }
    
    
    
    public String getChatCollectionUrl() {
        return this.chatCollectionUrl;
    }
    
    
    public void setChatCollectionUrl(String chatCollectionUrl) {
        this.chatCollectionUrl = chatCollectionUrl;
    }
    
    
    
    
}
