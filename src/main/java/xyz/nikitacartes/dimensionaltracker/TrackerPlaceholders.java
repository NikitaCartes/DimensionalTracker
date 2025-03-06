package xyz.nikitacartes.dimensionaltracker;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Properties;

public class TrackerPlaceholders {

    private static final HashMap<String, PlaceHoldersTags> placeholdersTags = new HashMap<>();

    public static void loadValue(Properties properties ) {
        for (String key : properties.stringPropertyNames()) {
            if (key.equals("enable-teams") || key.equals("enable-placeholders")) {
                continue;
            }
            String[] parts = key.split("\\.");
            if (parts.length != 2) {
                continue;
            }
            PlaceHoldersTags tags = placeholdersTags.get(parts[0]);
            if (tags == null) {
                tags = new PlaceHoldersTags();
            }
            switch (parts[1]) {
                case "id":
                    tags.setId(properties.getProperty(key));
                    break;
                case "name":
                    tags.setName(properties.getProperty(key));
                    break;
                case "color":
                    tags.setColor(properties.getProperty(key));
                    break;
                default:
                    continue;
            }
            placeholdersTags.put(parts[0], tags);
        }
        registerPlaceholders();
    }

    public static void registerPlaceholders() {
        Placeholders.register(
                Identifier.of("dimensional-tracker", "dimension_color"),
                (ctx, arg) -> {
                    if (!ctx.hasPlayer())
                        return PlaceholderResult.invalid("No player!");
                    String dimension = ctx.player().getServerWorld().getRegistryKey().getValue().getPath();

                    PlaceHoldersTags tags = placeholdersTags.get(dimension);
                    if (tags == null) {
                        return PlaceholderResult.invalid("No tags found for this dimension!");
                    }
                    return PlaceholderResult.value(tags.color);
                }
        );

        Placeholders.register(
                Identifier.of("dimensional-tracker", "dimension_name"),
                (ctx, arg) -> {
                    if (!ctx.hasPlayer())
                        return PlaceholderResult.invalid("No player!");
                    String dimension = ctx.player().getServerWorld().getRegistryKey().getValue().getPath();

                    PlaceHoldersTags tags = placeholdersTags.get(dimension);
                    if (tags == null) {
                        return PlaceholderResult.invalid("No tags found for this dimension!");
                    }
                    return PlaceholderResult.value(tags.name);
                }
        );

        Placeholders.register(
                Identifier.of("dimensional-tracker", "dimension_id"),
                (ctx, arg) -> {
                    if (!ctx.hasPlayer())
                        return PlaceholderResult.invalid("No player!");
                    String dimension = ctx.player().getServerWorld().getRegistryKey().getValue().getPath();

                    PlaceHoldersTags tags = placeholdersTags.get(dimension);
                    if (tags == null) {
                        return PlaceholderResult.invalid("No tags found for this dimension!");
                    }
                    return PlaceholderResult.value(tags.id);
                }
        );
    }

    private static class PlaceHoldersTags {
        String id;
        String name;
        String color;

        public PlaceHoldersTags(String id, String name, String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }

        public PlaceHoldersTags() {
            this.id = "";
            this.name = "";
            this.color = "#FFFFFF";
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setColor(String color) {
            this.color = color;
        }

    }

}
