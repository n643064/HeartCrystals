package n643064.heart_crystals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Config(
        boolean addHeartCrystalShardsToDungeonLoot,
        int maxLife,
        int starterLife,
        int lifeIncrement,
        boolean resetOnDeath,
        int lifeLostOnDeath
)
{
    public static Config CONFIG = new Config(
            true,
            80,
            20,
            1,
            false,
            0
    );
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
                writer.write(GSON.toJson(CONFIG));
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

    public static void setup()
    {
        try
        {
            if (Files.exists(Path.of(CONFIG_PATH)))
            {
                read();
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
