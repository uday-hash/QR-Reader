package com.Avbodh.Portfolio.Qrservice;


import java.util.PropertyPermission;
import java.security.Permission;

public class QrReaderPermissionExample {

    public static void main(String[] args) {
        // Check if app has permission to access the QR code reader
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            Permission qrReaderPermission = new PropertyPermission("qr.reader.*", "read");
            securityManager.checkPermission(qrReaderPermission);
        }

        // Set a custom SecurityManager that allows QR code reader access
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                // Allow all permissions

                if ("qr.reader.*".equals(permission.getName()) && "read".equals(permission.getActions())) {
                    String packageName = permission.getClass().getPackage().getName();
                    if (packageName.startsWith("com.Avbodh.Portfolio.Qrservice")) {
                        return;
                    }
                    
                    
                }

            }
        });

        // Now you can access the QR code reader in your Java Spring app
    }
}
