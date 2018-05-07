package com.yelo.com.mqttchat.Utilities;

/**
 * @since  29/06/17.
 */


public enum MqttEvents
{
    OnlineStatus("OnlineStatus"),
    Acknowledgement("Acknowledgement"),
    Typing("Typ"),
    Message("Message"),
    OfferMessage("MessageOffer"),
    UpdateProduct("Product"),
    Calls("Calls"),
    CallsAvailability("CallsAvailability"),
    /*
     * Not a topic on server,just are used locally
     */
    Connect("Connect"),

    MessageResponse("MessageResponse"),
    Disconnect("Disconnect"),
    /**
     * For contact Sync
     */
    ContactSync("ContactSync"),
    /*
     *For the user updates of various kinds
     */
    UserUpdates("UserUpdate"),
    FetchChats("GetChats"),
    FetchMessages("GetMessages");
    public String value;
    MqttEvents(String value) {
        this.value = value;
    }


}