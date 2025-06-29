package com.example.graalvm.sandbox;

import java.io.IOException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

import com.example.ApplicationInfo;
import com.example.ApplicationService;

/**
 * Sandbox
 */
public class SandboxRunner {
    /**
     * Standalone runner
     * @param args args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final String jdkHome = args[0];
        final String application = args[1];

        System.out.println("JDK Home: " + jdkHome);
        System.out.println("Application: " + application);

        final Engine engine = Engine
            .newBuilder()
            .build();

        sandbox(application, jdkHome, engine);
    }

    private static void sandbox(String application, String jdkHome, Engine engine) throws IOException {
        // See please:
        // - https://github.com/oracle/graal/issues/10239
        // - https://github.com/oracle/graal/blob/master/espresso/src/com.oracle.truffle.espresso/src/com/oracle/truffle/espresso/EspressoOptions.java
        // - https://github.com/oracle/graal/blob/master/espresso/src/com.oracle.truffle.espresso.launcher/src/com/oracle/truffle/espresso/launcher/EspressoLauncher.java
        final Context context = Context
            .newBuilder("java")
            .option("java.JavaHome", jdkHome)
            .option("java.Classpath", application)
            .option("java.Properties.java.security.manager", "allow")
            .option("java.PolyglotInterfaceMappings", getInterfaceMappings())
            .option("java.Polyglot", "true")
            .option("java.EnableGenericTypeHints", "true")
            .allowExperimentalOptions(true)
            .allowNativeAccess(true)
            .allowCreateThread(false)
            .allowHostAccess(HostAccess
                .newBuilder(HostAccess.ALL)
                .targetTypeMapping(
                        Value.class,
                        ApplicationInfo.class,
                        v -> true,
                        v -> new ApplicationInfo(
                            v.invokeMember("env").asString(), 
                            v.invokeMember("version").asString())
                    )
                .build()
            )
            .allowIO(IOAccess.NONE)
            .allowPolyglotAccess(PolyglotAccess
                .newBuilder()
                .allowBindingsAccess("java")
                .build())
            .engine(engine)
            .build();

        final Value runtime = context.getBindings("java").getMember("java.lang.Runtime");
        System.out.println("Host JVM version: " + Runtime.version());
        System.out.println("Guest JVM version: " + runtime.invokeMember("version"));

        final ApplicationService service = context.getBindings("java")
            .getMember("com.example.AppRunner")
            .invokeMember("getApplication")
            .as(ApplicationService.class);
        System.out.println("ApplicationInfo? " + service.getApplicationInfo());

        final ApplicationInfo newInfo = context.getBindings("java")
            .getMember("com.example.ApplicationInfo")
            .newInstance("sandboxed", "1.0.0"
            ).as(ApplicationInfo.class);

        final ApplicationInfo info = service.setApplicationInfo(newInfo);
        System.out.println("ApplicationInfo? " + info);
    }

    private static String getInterfaceMappings(){
        return "com.example.ApplicationService;";
    }

}
