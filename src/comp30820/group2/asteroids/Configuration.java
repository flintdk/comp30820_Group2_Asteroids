package comp30820.group2.asteroids;

import java.io.InputStream;
// java.nio "NIO" = "Non-blocking IO"
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Asteroids Configuration Management
 * 
 * We know at the outset that our application will require some configuration,
 * but which aspects should be kept close to code and which should be user
 * configurable is not decided yet.  This class encapsulates all our config.
 * management.
 * 
 * See a discussion on configuration considerations here:
 * https://medium.com/transferwise-engineering/where-to-put-application-configuration-4a2a46bd1bdd
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Configuration {
	// Content of this class made with reference to following resources:
	//   -> https://stackoverflow.com/questions/39042024/simple-java-desktop-app-config-files-and-where-to-put-them
	//   -> https://stackoverflow.com/questions/35388882/find-place-for-dedicated-application-folder
	//   -> https://stackoverflow.com/questions/11113974/what-is-the-cross-platform-way-of-obtaining-the-path-to-the-local-application-da
	
    /** Prevents instantiation. */
    private Configuration() {
    }

	// A final variable can only be initialized once, either via an initializer
	// or an assignment statement. It does not need to be initialized at the point
	// of declaration: this is called a "blank final" variable.
	//   -> A blank final instance variable of a class must be definitely assigned
	//      at the end of every constructor of the class in which it is declared;
	//   -> a blank final static variable must be definitely assigned in a static
	//      initializer of the class in which it is declared;
	// ...otherwise, a compile-time error occurs in both cases.
	private static final Path config;
    private static final Path data;
    private static final Path cache;
    
    static {
        String os = System.getProperty("os.name");
        String home = System.getProperty("user.home");

        if (os.contains("Mac")) {
            config = Paths.get(home, "Library", "Application Support");
            data = config;
            cache = config;
        } else if (os.contains("Windows")) {
            String version = System.getProperty("os.version");
            if (version.startsWith("5.")) {
                config = getFromEnv("APPDATA", false,
                    Paths.get(home, "Application Data"));
                data = config;
                cache = Paths.get(home, "Local Settings", "Application Data");
            } else {
                config = getFromEnv("APPDATA", false,
                    Paths.get(home, "AppData", "Roaming"));
                data = config;
                cache = getFromEnv("LOCALAPPDATA", false,
                    Paths.get(home, "AppData", "Local"));
            }
        } else {
            config = getFromEnv("XDG_CONFIG_HOME", true,
                Paths.get(home, ".config"));
            data = getFromEnv("XDG_DATA_HOME", true,
                Paths.get(home, ".local", "share"));
            cache = getFromEnv("XDG_CACHE_HOME", true,
                Paths.get(home, ".cache"));
        }
    }
    
    /**
     * Retrieves a path from an environment variable, substituting a default
     * if the value is absent or invalid.
     *
     * @param envVar name of environment variable to read
     * @param mustBeAbsolute whether environment variable's value should be
     *                       considered invalid if it's not an absolute path
     * @param defaultPath default to use if environment variable is absent
     *                    or invalid
     *
     * @return environment variable's value as a {@code Path},
     *         or {@code defaultPath}
     */
    private static Path getFromEnv(String envVar,
                                   boolean mustBeAbsolute,
                                   Path defaultPath) {
        Path dir;
        String envDir = System.getenv(envVar);
        if (envDir == null || envDir.isEmpty()) {
            dir = defaultPath;
            logger.log(Level.CONFIG,
                envVar + " not defined in environment"
                + ", falling back on \"{0}\"", dir);
        } else {
            dir = Paths.get(envDir);
            if (mustBeAbsolute && !dir.isAbsolute()) {
                dir = defaultPath;
                logger.log(Level.CONFIG,
                    envVar + " is not an absolute path"
                    + ", falling back on \"{0}\"", dir);
            }
        }
        return dir;
    }
    
   
    /**
     * Returns directory where the native system expects an application
     * to store configuration files for the current user.  No attempt is made
     * to create the directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path configDir(String appName)
    {
        return config.resolve(appName);
    }

    /**
     * Returns directory where the native system expects an application
     * to store implicit data files for the current user.  No attempt is made
     * to create the directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path dataDir(String appName)
    {
        return data.resolve(appName);
    }

    /**
     * Returns directory where the native system expects an application
     * to store cached data for the current user.  No attempt is made
     * to create the directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path cacheDir(String appName)
    {
        return cache.resolve(appName);
    }
	
	/** Load the Asteroids application configuration
	 * 
	 */
	public void loadConfiguration() {
		Path configParent;

		// On Windows, the actual location of a configuration file is usually in
		// a subdirectory of the user’s AppData directory:
		String appData = System.getenv("APPDATA");
		if (appData != null) {
		    configParent = Paths.get(appData);
		}
		else {
		    configParent = Paths.get(
		        System.getProperty("user.home"), "AppData", "Local");
		}

		Path configDir = configParent.resolve(Asteroids.APP_NAME);
		Files.createDirectories(configDir);

		Path userConfigFile = configDir.resolve("config.json");
	
		// Files packaged inside a .jar file are referred to as resources and can
		// be read using the Class.getResource/Class.getResourceAsStream methods.
		// You cannot read them using File or Path objects, because they are parts
		// of a .zip archive (not actual files).
		
		// Copying the default configuration to a writable location...
		try (InputStream defaultConfig = getClass().getResourceAsStream("/config/config.json")) {
		    Files.copy(defaultConfig, userConfigFile);
		}
	}
	
	private void 

}
