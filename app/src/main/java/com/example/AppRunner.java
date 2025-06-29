package com.example;

public class AppRunner {
    public static void main(String[] args) {
        System.out.println(getApplication().getApplicationInfo());
    }

    public static ApplicationService getApplication() {
        return new ApplicationService() {
            private ApplicationInfo info = new ApplicationInfo("unconstrained", "1.0.0");
            
            @Override
            public ApplicationInfo getApplicationInfo() {
                return info;
            }

            @Override
            public ApplicationInfo setApplicationInfo(ApplicationInfo info) {
                this.info = info;
                return info;
            }
        };
    }
}
