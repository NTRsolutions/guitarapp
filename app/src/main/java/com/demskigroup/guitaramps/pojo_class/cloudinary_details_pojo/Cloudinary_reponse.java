package com.demskigroup.guitaramps.pojo_class.cloudinary_details_pojo;

/**
 * @since  20/12/16.
 */

public class Cloudinary_reponse
{
    private String timestamp;

    private String signature;

    private String apiKey;

    private String cloudName;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }
}
