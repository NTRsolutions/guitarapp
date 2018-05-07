package com.yelo.com.mqttchat.ModelClasses;
/**
 *
 * @since  26/07/17.
 */
public class ChatlistItem
{
    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    private boolean isSold=false;
    private String productImage;
    private String productName;
    private String receiverUid;
    private String documentId;
    private String newMessage;
    private String newMessageCount;
    private String newMessageTime;
    private boolean receiverInContacts;
    private int tickStatus;
    public int getTickStatus() {
        return tickStatus;
    }
    public void setTickStatus(int tickStatus) {
        this.tickStatus = tickStatus;
    }
    public boolean isShowTick() {
        return showTick;
    }
    public void setShowTick(boolean showTick) {
        this.showTick = showTick;
    }
    private boolean showTick;
    /**
     * Can be receiver phoneNumber,email or the userName
     */
    private String receiverIdentifier;
    public String getReceiverIdentifier() {
        return receiverIdentifier;
    }
    public void setReceiverIdentifier(String receiverIdentifier) {
        this.receiverIdentifier = receiverIdentifier;
    }
    private String receiverName;
    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    private String receiverImage;

    private boolean isNewMessage;


    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String ReceiverName) {
        this.receiverName = ReceiverName;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String DocumentId) {
        this.documentId = DocumentId;
    }


    public String getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(String newMessageCount) {
        this.newMessageCount = newMessageCount;
    }


    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }


    public String getNewMessageTime() {
        return newMessageTime;
    }

    public void setNewMessageTime(String newMessageTime) {
        this.newMessageTime = newMessageTime;
    }


    public boolean hasNewMessage() {
        return isNewMessage;
    }

    public void sethasNewMessage(boolean isNewMessage) {
        this.isNewMessage = isNewMessage;
    }


    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String ReceiverUid) {
        this.receiverUid = ReceiverUid;
    }





    private String secretId;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
