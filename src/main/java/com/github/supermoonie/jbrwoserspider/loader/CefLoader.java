package com.github.supermoonie.jbrwoserspider.loader;

import lombok.extern.slf4j.Slf4j;
import org.cef.*;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author super_w
 * @since 2021/06/14
 */
@Slf4j
public class CefLoader {
    public static final String JAVA_LIBRARY_PATH = "java.library.path";
    public static final String JCEF_VERSION = "84.3.8+gc8a556f+chromium-84.0.4147.105-{os}{arch}";
    public static final String DEFAULT_BUNDLE_LOCATION = "cef_bundle";
    public static final String LOCK_FILE_NAME = "install.lock";

    private static boolean loaded = false;

    /**
     * Installs JCEF if needed and constructs a {@see org.cef.CefApp} instance.<br/>
     * <br/>
     * When run from a bundle:<br/>
     * <p>
     * Loads libraries from bundle, initialises JCEF. Does not auto update old versions.<br/>
     * <br/>
     * When run without a bundle:<br/>
     * <p>
     * When included natives are a different version than the installed version, REMOVES install directory to also
     * remove all other versions.
     * MAKE SURE THAT NO OTHER REQUIRED DATA APART FROM JCEF RESIDES IN INSTALL DIRECTORY!
     * Extracts natives to version directory inside of the install directory, if needed.
     * Loads libraries from extracted natives, initialises jcef.<br/>
     * <br/>
     * Install Dir: "cef_bundle" relative to work dir of the JVM process
     * Cef settings: Default settings
     *
     * @param args The command line arguments to pass to the CEF environment. These can be independent from your program
     *             arguments.
     * @return constructed {@see org.cef.CefApp} instance
     * @throws IOException      When a required file operation failed
     * @throws RuntimeException When JCEF failed to load
     */
    public static CefApp installAndLoadCef(String... args) throws IOException, RuntimeException {
        return installAndLoadCef(new File(DEFAULT_BUNDLE_LOCATION), args);
    }

    /**
     * Installs JCEF if needed and constructs a {@see org.cef.CefApp} instance.<br/>
     * <br/>
     * When run from a bundle:<br/>
     * <p>
     * Loads libraries from bundle, initialises JCEF. Does not auto update old versions.<br/>
     * <br/>
     * When run without a bundle:<br/>
     * <p>
     * When included natives are a different version than the installed version, REMOVES install directory to also
     * remove all other versions.
     * MAKE SURE THAT NO OTHER REQUIRED DATA APART FROM JCEF RESIDES IN INSTALL DIRECTORY!
     * Extracts natives to version directory inside of the install directory, if needed.
     * Loads libraries from extracted natives, initialises jcef.<br/>
     * <br/>
     * Install Dir: As specified by the installDir param
     * Cef settings: Default settings
     *
     * @param installDir The directory to use when extracting natives. THIS DIRECTORY WILL BE DELETED WHEN UPDATING
     *                   YOUR BUNDLE!
     * @param args       The command line arguments to pass to the CEF environment. These can be independent from your program
     *                   arguments.
     * @return constructed {@see org.cef.CefApp} instance
     * @throws IOException      When a required file operation failed
     * @throws RuntimeException When JCEF failed to load
     */
    public static CefApp installAndLoadCef(File installDir, String... args) throws IOException, RuntimeException {
        return installAndLoadCef(installDir, new CefSettings(), args);
    }

    /**
     * Installs JCEF if needed and constructs a {@see org.cef.CefApp} instance.<br/>
     * <br/>
     * When run from a bundle:<br/>
     * <p>
     * Loads libraries from bundle, initialises JCEF. Does not auto update old versions.<br/>
     * <br/>
     * When run without a bundle:<br/>
     * <p>
     * When included natives are a different version than the installed version, REMOVES install directory to also
     * remove all other versions.
     * MAKE SURE THAT NO OTHER REQUIRED DATA APART FROM JCEF RESIDES IN INSTALL DIRECTORY!
     * Extracts natives to version directory inside of the install directory, if needed.
     * Loads libraries from extracted natives, initialises jcef.<br/>
     * <br/>
     * Install Dir: "cef_bundle" relative to work dir of the JVM process
     * Cef settings: As specified by the settings param
     *
     * @param settings The CEF settings to use
     * @param args     The command line arguments to pass to the CEF environment. These can be independent from your program
     *                 arguments.
     * @return constructed {@see org.cef.CefApp} instance
     * @throws IOException      When a required file operation failed
     * @throws RuntimeException When JCEF failed to load
     */
    public static CefApp installAndLoadCef(CefSettings settings, String... args) throws IOException, RuntimeException {
        return installAndLoadCef(new File(DEFAULT_BUNDLE_LOCATION), settings, args);
    }

    /**
     * Installs JCEF if needed and constructs a {@see org.cef.CefApp} instance.<br/>
     * <br/>
     * When run from a bundle:<br/>
     * <p>
     * Loads libraries from bundle, initialises JCEF. Does not auto update old versions.<br/>
     * <br/>
     * When run without a bundle:<br/>
     * <p>
     * When included natives are a different version than the installed version, REMOVES install directory to also
     * remove all other versions.
     * MAKE SURE THAT NO OTHER REQUIRED DATA APART FROM JCEF RESIDES IN INSTALL DIRECTORY!
     * Extracts natives to version directory inside of the install directory, if needed.
     * Loads libraries from extracted natives, initialises jcef.<br/>
     * <br/>
     * Install Dir: As specified by the installDir param
     * Cef settings: As specified by the settings param
     *
     * @param installDir The directory to use when extracting natives. THIS DIRECTORY WILL BE DELETED WHEN UPDATING
     *                   *                   YOUR BUNDLE!
     * @param settings   The CEF settings to use
     * @param args       The command line arguments to pass to the CEF environment. These can be independent from your program
     *                   arguments.
     * @return constructed {@see org.cef.CefApp} instance
     * @throws IOException      When a required file operation failed
     * @throws RuntimeException When JCEF failed to load
     */
    public static synchronized CefApp installAndLoadCef(File installDir, CefSettings settings, String... args)
            throws IOException, RuntimeException {
        //Dont load twice
        if (loaded) return CefApp.getInstance();
        loaded = true;
        //Only bundle is supported for macosx, no need to install
        if (OS.isMacintosh()) {
            boolean success = CefApp.startup(args);
            if (!success) throw new RuntimeException("JCef did not initialize correctly!" +
                    "Mac OS version can not be used outside of a bundle!");
            return CefApp.getInstance(args, settings);
        }
        //Check if lock file is present in installDir
        File versionDir;
        if (new File(installDir, LOCK_FILE_NAME).exists()) {
            //Seems like bundle is installed in install dir, without a version description (most likely bundle)
            versionDir = installDir;
        } else {
            //Append version number to install dir
            log.info("JCEF_VERSION: {}", JCEF_VERSION);
            log.info("os: {}", OSUtil.getOsName());
//            log.info("arch: {}", OSUtil.getJvmArch());
            versionDir = new File(installDir, JCEF_VERSION
                    .replace("{os}", OSUtil.getOsName())
                    .replace("{arch}", "64"));
        }
        //Extract to install dir, if not available
        File lockFile = new File(versionDir, LOCK_FILE_NAME);
        if (!versionDir.exists() || !lockFile.exists()) {
            //We delete the install directory to remove previous versions as well (cef is quite large)
            deleteDir(installDir);
            //Extract new version
            versionDir.mkdirs();
            String nativeResourceName = "cef-win64.zip";
            InputStream resource = JCefLoader.class.getResourceAsStream("/" + nativeResourceName);
            if (resource == null) {
                try {
                    resource = new FileInputStream(nativeResourceName);
                } catch (Exception e) {
                    log.error("{} not found", nativeResourceName);
                    throw new RuntimeException("Could not initialize cef: " +
                            "Native resource " + nativeResourceName + " not found! Did you include it?", e);
                }
            }
            ZipInputStream zipInputStream = new ZipInputStream(resource);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory() || zipEntry.getName().isEmpty()) continue;
                File out = new File(versionDir, zipEntry.getName());
                out.getParentFile().mkdirs();
                out.createNewFile();
                FileOutputStream fos = new FileOutputStream(out);
                byte[] buffer = new byte[4096];
                while (true) {
                    int r = zipInputStream.read(buffer);
                    if (r < 0) break;
                    fos.write(buffer, 0, r);
                }
                fos.flush();
                fos.close();
                //Make certain files executable (under linux and macosx)
                if (OS.isLinux()) {
                    if (out.getName().equals("jcef_helper")) {
                        out.setExecutable(true);
                    }
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            //Write install lock to indicate success for future startups
            lockFile.createNewFile();
        }
        //Patch java library path to scan the install dir of our application
        //This is required for jcef to find all resources
        String path = System.getProperty(JAVA_LIBRARY_PATH);
        if (!path.endsWith(File.pathSeparator)) path += File.pathSeparator;
        path += versionDir.getAbsolutePath();
        System.setProperty(JAVA_LIBRARY_PATH, path);
        //Remove dependency loader from jcef (causes unnecessary errors)
        SystemBootstrap.setLoader(libname -> {
        });
        //Load native libraries for jcef, as the jvm does not update the java library path
        System.loadLibrary("jawt");
        if (OS.isWindows()) {
            System.load(new File(versionDir, "chrome_elf.dll").getAbsolutePath());
            System.load(new File(versionDir, "libcef.dll").getAbsolutePath());
            System.load(new File(versionDir, "jcef.dll").getAbsolutePath());
        } else if (OS.isLinux()) {
            //Make jcef_helper executable
            File jcef_helper = new File(versionDir, "jcef_helper");
            jcef_helper.setExecutable(true);
            //Load jcef native library
            System.load(new File(versionDir, "libjcef.so").getAbsolutePath());
            //Initialize cef
            boolean success = CefApp.startup(args);
            if (!success) throw new RuntimeException("JCef did not initialize correctly!");
            System.load(new File(versionDir, "libcef.so").getAbsolutePath());
        }
        //Configure cef settings and create app instance (currently nothing to configure, may change in the future)
        return CefApp.getInstance(args, settings);
    }

    /**
     * Utility method to delete a directory recursively
     *
     * @param dir The directory to delete
     */
    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDir(file);
            }
        }
        dir.delete();
    }
}
