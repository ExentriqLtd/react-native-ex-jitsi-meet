// 
// Decompiled by Procyon v0.5.36
// 

package com.reactnativejitsimeet.sdk;

import com.google.gson.annotations.SerializedName;

public class ParticipantInfo
{
    @SerializedName("participantId")
    public String id;
    @SerializedName("displayName")
    public String displayName;
    @SerializedName("avatarUrl")
    public String avatarUrl;
    @SerializedName("email")
    public String email;
    @SerializedName("name")
    public String name;
    @SerializedName("isLocal")
    public boolean isLocal;
    @SerializedName("role")
    public String role;
}
