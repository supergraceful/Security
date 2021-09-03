package com.example.lib.securitylibaray.general;

public class EncryptResult {

    private String encryptParams;
    private String privateKeyHex;
    private String publicKeyHex;
    private String errorMessage;

    public String getEncryptParams() {
        return encryptParams;
    }

    public void setEncryptParams(String encryptParams) {
        this.encryptParams = encryptParams;
    }

    public String getPrivateKeyHex() {
        return privateKeyHex;
    }

    public void setPrivateKeyHex(String privateKeyHex) {
        this.privateKeyHex = privateKeyHex;
    }

    public String getPublicKeyHex() {
        return publicKeyHex;
    }

    public void setPublicKeyHex(String publicKeyHex) {
        this.publicKeyHex = publicKeyHex;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
