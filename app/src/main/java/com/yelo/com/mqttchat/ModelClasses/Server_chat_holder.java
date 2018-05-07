package com.yelo.com.mqttchat.ModelClasses;

public class Server_chat_holder
{
    private String receiverId;
    private String secretId;

    public String getReceiverId()
    {
        return receiverId;
    }

    public void setReceiverId(String receiverId)
    {
        this.receiverId = receiverId;
    }

    public String getSecretId()
    {
        return secretId;
    }

    public void setSecretId(String secretId)
    {
        this.secretId = secretId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj!=null&&obj instanceof Server_chat_holder)
        {
            Server_chat_holder temp=(Server_chat_holder)obj;
            return temp.getSecretId().equals(secretId) && temp.getReceiverId().equals(receiverId);
        }else
        {
            return false;
        }

    }
}
