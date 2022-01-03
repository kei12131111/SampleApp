package com.example.todo.domain.model;

import java.io.Serializable;

/**
 * クライアント情報を保持するモデル。
 * 
 */
public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//クライアントを識別するクライアントIDを保持するフィールド
	private String clientId;
	
	//クライアントの認証に使用するパスワードを保持するフィールド
    private String clientSecret;
    
    //Spring Security OAuthでは提供されていない、クライアント名を保持する拡張フィールド
    private String clientName;
    
    //アクセストークンの有効期間[秒]を保持するフィールド
    private Integer accessTokenValidity;
    
    //リフレッシュトークンの有効期間[秒]を保持するフィールド
    private Integer refreshTokenValidity;
    
    
    
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}
	
	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}
	
	public Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}
	
	public void setRefreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

    
    
}