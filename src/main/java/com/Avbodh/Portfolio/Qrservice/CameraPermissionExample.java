package com.Avbodh.Portfolio.Qrservice;

import java.awt.AWTPermission;
import java.security.Permission;

public class CameraPermissionExample {

    public static void main(String[] args) {
        // Check if app has permission to access the camera
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            Permission cameraPermission = new AWTPermission("accessEventQueue");
            securityManager.checkPermission(cameraPermission);
        }

        // Set a custom SecurityManager that allows camera access
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                // Allow all permissions
                

            }
        });

        // Now you can access the camera in your Java Spring app
    }
}
