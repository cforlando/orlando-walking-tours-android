package com.codefororlando.orlandowalkingtours.event;

public class OnPermissionGrantEvent {
    public final String permission;

    public OnPermissionGrantEvent(String permission) {
        this.permission = permission;
    }
}
