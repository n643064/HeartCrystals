package n643064.heart_crystals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Config(
        short version,
        boolean addHeartCrystalShardsToDungeonLoot,
        int maxLife,
        int starterLife,
        int lifeIncrement,
        boolean resetOnDeath,
        boolean canLoseLifeBeneathStarterLife,
        int lifeLostOnDeath
)
{
    private static final short CURRENT_VERSION = 1;
    private static final Config DEF_CONFIG = new Config(
        CURRENT_VERSION,
        true,
        80,
        20,
        1,
        false,
        true,
        0
    );
    public static Config CONFIG;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    static final String CONFIG_PATH = "config" + File.separator + "heart_crystals.json";

    public static void create() throws IOException
    {
        Path p = Path.of("config");
        if (Files.exists(p))
        {
            if (Files.isDirectory(p))
            {
                FileWriter writer = new FileWriter(CONFIG_PATH);
                writer.write(GSON.toJson(DEF_CONFIG));
                writer.flush();
                writer.close();
            }
        } else
        {
            Files.createDirectory(p);
            create();
        }
    }

    public static void read() throws IOException
    {
        FileReader reader = new FileReader(CONFIG_PATH);
        CONFIG = GSON.fromJson(reader, Config.class);
        reader.close();
    }

    public static boolean checkVersion()
    {
        try
        {
            Field versionField = Config.class.getDeclaredField("version");
            versionField.setAccessible(true);

            short configVersion = (short) versionField.get(CONFIG);

            if (!(configVersion == CURRENT_VERSION))
            {
                CONFIG = DEF_CONFIG;
                return false;
            }

            return true;
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            CONFIG = DEF_CONFIG;
            return false;
        }
    }

    public static void setup()
    {
        try
        {
            if (Files.exists(Path.of(CONFIG_PATH)))
            {
                read();
                if (!checkVersion())
                    // Should only update fields as needed, this is quick and easy
                    create();
            } else
            {
                create();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}