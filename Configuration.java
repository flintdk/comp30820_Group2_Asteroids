package comp30820.group2.asteroids;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
// java.nio "NIO" = "Non-blocking IO"
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import comp30820.group2.asteroids.AsteroidsCodes.Result;

/** Asteroids Configuration Management
 * 
 * We know at the outset that our application will require some configuration,
 * but which aspects should be kept close to code and which should be user
 * configurable is not decided yet.  This class encapsulates all our config.
 * management.  We have three layers:
 * <ul>
 * 	<li>Hard-Coded conservative defaults (minimum to make Asteroids run)</li>
 * 	<li>Default Configuration File (best-guess configuration parameters)</li>
 * 	<li>User Configuration File (optimal game configuration, chosen by user)</li>
 * </ul>
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
	
	// A final variable can only be initialized once, either via an initializer
	// or an assignment statement. It does not need to be initialized at the point
	// of declaration: this is called a "blank final" variable.
	//   -> A blank final instance variable of a class must be definitely assigned
	//      at the end of every constructor of the class in which it is declared;
	//   -> a blank final static variable must be definitely assigned in a static
	//      initializer of the class in which it is declared;
	// ...otherwise, a compile-time error occurs in both cases.
	private static final Path config;
	
    // Operating system supported
    public enum OS {
        WINDOWS("Microsoft Windows"),
        LINUX("All Linux Variants"),
        MAC("Apple Macintosh (macOS / Mac OS X / OS X)");
        
        public final String description;

        private OS(String description) {
            this.description = description;
        }
    };
    // Graphics modes supported
    public enum GraphicsMode {
        CLASSIC("Ships and Asteroids represented by basic shapes (polygons)"),
        ARCADE("Ships and Asteroids represented graphically (icons)"),
        EASTER_EGG("???");
        
        public final String description;

        private GraphicsMode(String description) {
            this.description = description;
        }
    };
    // Resource folders
    public enum Resource {
    	// The "/config/" hard coded here is the resource directory and not the
    	// final Path config above that we work so hard to work out!
        CONFIG("Bundled configuration directory.", "/config/"),
        IMG("Graphics files in the .PNG format, for in-game sprites (when used)", "/img/"),
        SND("Audio files in the .WAV format, for in-game sound effects", "/snd/");
        
        public final String description;
        public final String path;

        private Resource(String description, String path) {
            this.description = description;
            this.path = path;
        }
    };
    // Sound effect resources
    public enum SoundEffects {
        BANG_LARGE(Resource.SND.path + "bangLarge.wav"),
        BANG_MEDIUM(Resource.SND.path + "bangMedium.wav"),
        BANG_SMALL(Resource.SND.path + "bangSmall.wav"),
        BEAT1(Resource.SND.path + "beat1.wav"),
        BEAT2(Resource.SND.path + "beat2.wav"),
        EXTRA_SHIP(Resource.SND.path + "extraShip.wav"),
        FIRE(Resource.SND.path + "fire.wav"),
        SAUCER_BIG(Resource.SND.path + "saucerBig.wav"),
        SAUCER_SMALL(Resource.SND.path + "saucerSmall.wav"),
        THRUST(Resource.SND.path + "thrust.wav");
        
        public final String path;

        private SoundEffects(String path) {
            this.path = path;
        }
    };
	
    /** Prevents instantiation. */
    private Configuration() {
    }

	// Mutable Configuration:
	// The following are set to 'minimum acceptable' values to get Asteroids to
	// run...
	public static GraphicsMode GRAPHICS_MODE = GraphicsMode.CLASSIC;
	public static int SCENE_WIDTH = 800;
	public static int SCENE_HEIGHT = 600;
	public static int SPEED_MAX = 200;
	public static int SPEED_INCREMENT = 10;
	public static int SPEED_BULLET = 300;
	public static int ASTEROID_LRG_SIZE = 60;
	public static int ASTEROID_LRG_SPEED = 50;
	public static int ASTEROID_MED_SIZE = 40;
	public static int ASTEROID_MED_SPEED = 100;
	public static int ASTEROID_SML_SIZE = 20;
	public static int ASTEROID_SML_SPEED = 150;
	public static int ASTEROID_RADIUS_VARIANCE = 30;  // Percent
	public static int ASTEROID_GRANULARITY = 25;  // No. of Points to divide circle
	public static int SPEED_AlIEN = 150;
	
	public static Result CONFIGURATION_LOADED = Result.FAILURE;
    
    // A Static Initialization Block in Java is a block that runs before the
    // main( ) method in Java.
	// In our initialiser, we initialise the Configuration Path. This is the path
	// to the user-specific configuration file.
    static {
    	// To work out where to store our configuration file, we need to try to
    	// figure out what operating system we're looking at....
    	String home = System.getProperty("user.home");
    	
    	OS os = getOperatingSystem();
	
		if (os == OS.MAC) {
	        config = Paths.get(home, "Library", "Application Support");
	    }
	    else if (os == OS.WINDOWS) {
	        //String version = System.getProperty("os.version");
	        // For Windows before Vista (version 6) the folder was 'Application Data'
	        // I'm not supporting Windows XP or older...
	    	String envDirString = System.getenv("APPDATA");
	    	if (envDirString == null || envDirString.isEmpty()) {
	    		config = Paths.get(home, "AppData", "Roaming");
	    	}
	    	else {
	    		config = Paths.get(envDirString);
	    	}
	    }
	    else if (os == OS.LINUX) {
	    	// According to https://wiki.archlinux.org/title/XDG_Base_Directory,
	    	// if $XDG_CONFIG_HOME is either not set or empty, a default equal to
	    	// $HOME/.config should be used.
	    	String envDirString = System.getenv("XDG_CONFIG_HOME");
	    	Path defaultPath = Paths.get(home, ".config");
	    	if (envDirString == null || envDirString.isEmpty()) {
	    		config = defaultPath;
	    	}
	    	else {
	    		Path envDirPath = Paths.get(envDirString);
	    		if (envDirPath.isAbsolute()) {
	    			config = envDirPath;
	    		}
	    		else {
	    			config = defaultPath;
	    		}
	    	}
	    }
	    else {
	    	// If no valid config path is found, we default to the current
	    	// working directory.
	    	//######################################################### Does this work inside a jar??
	    	config = Paths.get("");
	    }
		//System.out.println("Configuration initialised. Configuration file path is: " + config.toString());
    }
    
    /** Load the User-specific Asteroids application configuration
	 * 
	 */
    public static void loadConfig() {
    	// Our Asteroids application may be running in a jar!  It will contain a
        // default configuration file.  But the user may want to customise their
        // own configuration.  So -
    	
    	boolean configLoaded = false;
    	String propsFileName = Main.APP_NAME.concat(".properties");
    	Properties configProps = new Properties();

    	// First see if a user-specific file exists...
        try
        {
        	Path userConfigPath = config.resolve(propsFileName);
        	File userConfigFile = userConfigPath.toFile();
        	if (userConfigFile.exists()) {
	        	FileInputStream fis = new FileInputStream(userConfigFile);
	            configProps.load(fis); // load asteroids .properties file
	
	            setConfigFromProperties(configProps);
	                       
	            fis.close();
	            
	            System.out.println("Asteroid: Configuration loaded from User file.");
	            configLoaded = true;
        	}
            
            // Next - attempt to load the default configuration file
            if (!configLoaded) {
		        // Files packaged inside a .jar file are referred to as resources and can
				// be read using the Class.getResource/Class.getResourceAsStream methods.
				// You cannot read them using File or Path objects, because they are parts
				// of a .zip archive (not actual files).

            	// We use Asteroids as our resource-anchor class...
		        InputStream in = Main.class.getResourceAsStream(Resource.CONFIG.path + propsFileName);
		        if (in != null) {
			        configProps.load(in);
		
		            setConfigFromProperties(configProps);
		        }
		        else {
		        	// #################################################### Logging????
		        	System.out.println("ERROR: DEFAULT Asteroids configuration NOT FOUND!");
		        }
	                       
		        in.close();
		        
		        System.out.println("Asteroid: Configuration loaded from Default file.");
		        configLoaded = true;
            }
        }
        catch(IOException e)
        {
            System.out.println("WARNING: Could not load Asteroids configuration file.");
            System.out.println(e.getMessage());
        }
        
        // Finally - if all attempts to load a configuration file failed - don't
        // worry!  We still have our hard-coded minimum acceptable configuration
        // to work with.  We continue... but set a status
        if (configLoaded) {
        	CONFIGURATION_LOADED = Result.SUCCESS;
        }
        else {
        	// Not needed of course... but explicit is good...
        	CONFIGURATION_LOADED = Result.FAILURE;
        }
    }

	/** Set the Configuration class variables from the properties file.
	 * 
	 * @param configProps
	 */
	private static void setConfigFromProperties(Properties configProps) {
		// Populate our config variables:
		// The properties are all just strings, so we need to cast them
		// appropriately when evaluating them...
		if (configProps.getProperty("GRAPHICS_MODE") != null) {
			GRAPHICS_MODE = GraphicsMode.valueOf(configProps.getProperty("GRAPHICS_MODE").toString());
		}
		if (configProps.getProperty("SCENE_WIDTH") != null) {
			SCENE_WIDTH = Integer.parseInt(configProps.getProperty("SCENE_WIDTH"));
		}
		if (configProps.getProperty("SCENE_HEIGHT") != null) {
			SCENE_HEIGHT = Integer.parseInt(configProps.getProperty("SCENE_HEIGHT"));
		}
		if (configProps.getProperty("SPEED_MAX") != null) {
			SPEED_MAX = Integer.parseInt(configProps.getProperty("SPEED_MAX"));
		}
		if (configProps.getProperty("SPEED_INCREMENT") != null) {
			SPEED_INCREMENT = Integer.parseInt(configProps.getProperty("SPEED_INCREMENT"));
		}
		if (configProps.getProperty("SPEED_BULLET") != null) {
			SPEED_BULLET = Integer.parseInt(configProps.getProperty("SPEED_BULLET"));
		}
		if (configProps.getProperty("ASTEROID_LRG_SIZE") != null) {
			ASTEROID_LRG_SIZE = Integer.parseInt(configProps.getProperty("ASTEROID_LRG_SIZE"));
		}
		if (configProps.getProperty("ASTEROID_LRG_SPEED") != null) {
			ASTEROID_LRG_SPEED = Integer.parseInt(configProps.getProperty("ASTEROID_LRG_SPEED"));
		}
		if (configProps.getProperty("ASTEROID_MED_SIZE") != null) {
			ASTEROID_MED_SIZE = Integer.parseInt(configProps.getProperty("ASTEROID_MED_SIZE"));
		}
		if (configProps.getProperty("ASTEROID_MED_SPEED") != null) {
			ASTEROID_MED_SPEED = Integer.parseInt(configProps.getProperty("ASTEROID_MED_SPEED"));
		}
		if (configProps.getProperty("ASTEROID_SML_SIZE") != null) {
			ASTEROID_SML_SIZE = Integer.parseInt(configProps.getProperty("ASTEROID_SML_SIZE"));
		}
		if (configProps.getProperty("ASTEROID_SML_SPEED") != null) {
			ASTEROID_SML_SPEED = Integer.parseInt(configProps.getProperty("ASTEROID_SML_SPEED"));
		}
		if (configProps.getProperty("ASTEROID_RADIUS_VARIANCE") != null) {
			ASTEROID_RADIUS_VARIANCE = Integer.parseInt(configProps.getProperty("ASTEROID_RADIUS_VARIANCE"));
		}
		if (configProps.getProperty("ASTEROID_GRANULARITY") != null) {
			ASTEROID_GRANULARITY = Integer.parseInt(configProps.getProperty("ASTEROID_GRANULARITY"));
		}
	}
    
    /** Save the User-specific Asteroids application configuration
     * 
     */
    public static void saveConfig() {
    	
    	String propsFileName = Main.APP_NAME.concat(".properties");

    	// First see if a user-specific file exists...
        try
        {
        	Path userConfigPath = config.resolve(propsFileName);
        	File userConfigFile = userConfigPath.toFile();
        	
        	// If the user-specific file doesn't exist - create one!
        	if (!userConfigFile.exists()) {
        		userConfigFile.createNewFile();
        	}

        	FileOutputStream out = new FileOutputStream(userConfigFile);

        	Properties configProps = new Properties();
        	setPropertiesFromConfig(configProps);
        	configProps.store(out, null);

        	out.close();
        	
        	System.out.println("Asteroids: Configuration saved to User file.");
        }
        catch(IOException e)
        {
            System.out.println("WARNING: Could not save User-specific Asteroids configuration file.");
            System.out.println(e.getMessage());
        }
    	
    }
    
	/** Set the Configuration class variables from the properties file.
	 * 
	 * @param configProps
	 */
	private static void setPropertiesFromConfig(Properties configProps) {
		// Populate our properties file from the current config variable settings
		configProps.setProperty("GRAPHICS_MODE", GRAPHICS_MODE.toString());
		configProps.setProperty("SCENE_WIDTH", String.valueOf(SCENE_WIDTH));
		configProps.setProperty("SCENE_HEIGHT", String.valueOf(SCENE_HEIGHT));
		configProps.setProperty("SPEED_MAX", String.valueOf(SPEED_MAX));
		configProps.setProperty("SPEED_INCREMENT",  String.valueOf(SPEED_INCREMENT));
		configProps.setProperty("SPEED_BULLET",  String.valueOf(SPEED_BULLET));
		configProps.setProperty("ASTEROID_LRG_SIZE",  String.valueOf(ASTEROID_LRG_SIZE));
		configProps.setProperty("ASTEROID_LRG_SPEED",  String.valueOf(ASTEROID_LRG_SPEED));
		configProps.setProperty("ASTEROID_MED_SIZE",  String.valueOf(ASTEROID_MED_SIZE));
		configProps.setProperty("ASTEROID_MED_SPEED",  String.valueOf(ASTEROID_MED_SPEED));
		configProps.setProperty("ASTEROID_MED_SPEED",  String.valueOf(ASTEROID_MED_SPEED));
		configProps.setProperty("ASTEROID_SML_SIZE",  String.valueOf(ASTEROID_SML_SIZE));
		configProps.setProperty("ASTEROID_SML_SPEED",  String.valueOf(ASTEROID_SML_SPEED));
		configProps.setProperty("ASTEROID_RADIUS_VARIANCE",  String.valueOf(ASTEROID_RADIUS_VARIANCE));
		configProps.setProperty("ASTEROID_GRANULARITY",  String.valueOf(ASTEROID_GRANULARITY));
	}
    
    /** Get the Host Operating System.
     * 
     * @return
     */
    public static OS getOperatingSystem()
    {
        // detecting the operating system using `os.name` System property
        String os = System.getProperty("os.name").toLowerCase();
 
        if (os.contains("win")) {
            return OS.WINDOWS;
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OS.LINUX;
        }
        else if (os.contains("mac")) {
            return OS.MAC;
        }
 
        return null;
    }

    /**
     * Returns directory where the native system expects an application
     * to store configuration files for the current user.
     *
     * @param appName name of application
     */
    public static Path configDir(String appName)
    {
        return config.resolve(appName);
    }
	
}
