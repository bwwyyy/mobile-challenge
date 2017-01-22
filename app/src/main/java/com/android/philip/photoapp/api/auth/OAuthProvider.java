package com.android.philip.photoapp.api.auth;


import org.apache.http.client.methods.HttpPost;

public interface OAuthProvider {
    void signForAccessToken(HttpPost req) throws FiveHundredException;

    void setOAuthConsumer(String consumerKey, String consumerSecret);
    void setOAuthRequestToken(String requestTokenKey, String requestTokenSecret);
}