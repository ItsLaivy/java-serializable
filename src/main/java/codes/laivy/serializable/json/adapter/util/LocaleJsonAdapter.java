package codes.laivy.serializable.json.adapter.util;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.json.TestJson;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.util.locale.BaseLocale;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IllformedLocaleException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

final class LocaleJsonAdapter implements JsonAdapter<Locale> {

    // Static initializers

    private static final @NotNull Method CONVERT_ISO_METHOD;
    private static final @NotNull Field baseLocaleField;
    private static final @NotNull Field localeExtensionsField;

    static {
        try {
            CONVERT_ISO_METHOD = Locale.class.getDeclaredMethod("convertOldISOCodes", String.class);
            baseLocaleField = Locale.class.getDeclaredField("baseLocale");
            localeExtensionsField = Locale.class.getDeclaredField("localeExtensions");
        } catch (@NotNull NoSuchMethodException e) {
            throw new RuntimeException("cannot find old ISO codes converter method at Locale class", e);
        } catch (@NotNull NoSuchFieldException e) {
            throw new RuntimeException("cannot find baseLocale and/or localeExtensions field at Locale class", e);
        }
    }

    public static @NotNull BaseLocale getBaseLocale(@NotNull Locale locale) {
        try {
            boolean accessible = baseLocaleField.isAccessible();
            baseLocaleField.setAccessible(true);

            @NotNull BaseLocale base = (BaseLocale) baseLocaleField.get(locale);
            baseLocaleField.setAccessible(accessible);

            return base;
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setBaseLocale(@NotNull Locale locale, @NotNull BaseLocale base) {
        try {
            boolean accessible = baseLocaleField.isAccessible();
            baseLocaleField.setAccessible(true);

            baseLocaleField.set(locale, base);
            baseLocaleField.setAccessible(accessible);
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull String convertOldISOCodes(@NotNull String string) {
        try {
            boolean accessible = CONVERT_ISO_METHOD.isAccessible();
            CONVERT_ISO_METHOD.setAccessible(true);

            @NotNull String value = (String) CONVERT_ISO_METHOD.invoke(null, string);
            CONVERT_ISO_METHOD.setAccessible(accessible);

            return value;
        } catch (@NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot convert old ISO codes from Locale", e);
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Object

    @Override
    public @NotNull Class<Locale> getReference() {
        return Locale.class;
    }

    @Override
    public @Nullable JsonElement serialize(@NotNull TestJson serializer, @Nullable Locale instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        }

        @NotNull BaseLocale baseLocale = getBaseLocale(instance);

        // Start serialize
        @NotNull JsonObject object = new JsonObject();
        object.addProperty("language", instance.getLanguage());

        if (!instance.getScript().isEmpty()) {
            object.addProperty("script", instance.getScript());
        } if (!baseLocale.getRegion().isEmpty()) {
            object.addProperty("country", baseLocale.getRegion());
        } if (!instance.getVariant().isEmpty()) {
            object.addProperty("variant", instance.getVariant());
        } if (!instance.getExtensionKeys().isEmpty()) {
            @NotNull JsonArray extensions = new JsonArray();

            for (@NotNull Character character : instance.getExtensionKeys()) {
                extensions.add(character);
            }

            object.add("extensions", extensions);
        }

        // Finish
        return object;
    }
    @Override
    public @Nullable Locale deserialize(@NotNull TestJson serializer, @NotNull Class<Locale> reference, @Nullable JsonElement element) throws IllformedLocaleException {
        if (element == null || element.isJsonNull()) {
            return null;
        } else if (element.isJsonObject()) {
            // Deserialization
            @NotNull JsonObject object = element.getAsJsonObject();
            @NotNull Set<Character> extensions = new LinkedHashSet<>();

            @NotNull String language = object.get("language").getAsString();
            @NotNull String script = object.has("script") ? object.get("script").getAsString() : "";
            @NotNull String country = object.has("country") ? object.get("country").getAsString() : "";
            @NotNull String variant = object.has("variant") ? object.get("variant").getAsString() : "";

            if (object.has("extensions")) {
                for (@NotNull JsonElement extensionElement : object.getAsJsonArray("extensions")) {
                    extensions.add(extensionElement.getAsString().charAt(0));
                }
            }

            @NotNull BaseLocale base = BaseLocale.getInstance(convertOldISOCodes(language), script, country, variant);

            // Reserve instance
            @NotNull Locale locale = Allocator.allocate(Locale.class);
            setBaseLocale(locale, base);
            locale.getExtensionKeys().addAll(extensions);

            // Finish
            return locale;
        } else {
            throw new IllegalArgumentException("the " + getReference() + " element must be an json object: '" + element + "'");
        }
    }

}
