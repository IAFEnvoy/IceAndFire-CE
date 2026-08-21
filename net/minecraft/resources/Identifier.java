package net.minecraft.resources;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.IdentifierException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public final class Identifier implements Comparable<Identifier> {
    public static final Codec<Identifier> CODEC = Codec.STRING.<Identifier>comapFlatMap(Identifier::read, Identifier::toString).stable();
    public static final StreamCodec<ByteBuf, Identifier> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
    public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String REALMS_NAMESPACE = "realms";
    public static final String ALLOWED_NAMESPACE_CHARACTERS = "[a-z0-9_.-]";
    private final String namespace;
    private final String path;

    private Identifier(String namespace, String path) {
        assert isValidNamespace(namespace);

        assert isValidPath(path);

        this.namespace = namespace;
        this.path = path;
    }

    private static Identifier createUntrusted(String namespace, String path) {
        return new Identifier(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    public static Identifier fromNamespaceAndPath(String namespace, String path) {
        return createUntrusted(namespace, path);
    }

    public static Identifier parse(String identifier) {
        return bySeparator(identifier, ':');
    }

    public static Identifier withDefaultNamespace(String path) {
        return new Identifier("minecraft", assertValidPath("minecraft", path));
    }

    public static @Nullable Identifier tryParse(String identifier) {
        return tryBySeparator(identifier, ':');
    }

    public static @Nullable Identifier tryBuild(String namespace, String path) {
        return isValidNamespace(namespace) && isValidPath(path) ? new Identifier(namespace, path) : null;
    }

    public static Identifier bySeparator(String identifier, char separator) {
        int separatorIndex = identifier.indexOf(separator);
        if (separatorIndex >= 0) {
            String path = identifier.substring(separatorIndex + 1);
            if (separatorIndex != 0) {
                String namespace = identifier.substring(0, separatorIndex);
                return createUntrusted(namespace, path);
            } else {
                return withDefaultNamespace(path);
            }
        } else {
            return withDefaultNamespace(identifier);
        }
    }

    public static @Nullable Identifier tryBySeparator(String identifier, char separator) {
        int separatorIndex = identifier.indexOf(separator);
        if (separatorIndex >= 0) {
            String path = identifier.substring(separatorIndex + 1);
            if (!isValidPath(path)) {
                return null;
            } else if (separatorIndex != 0) {
                String namespace = identifier.substring(0, separatorIndex);
                return isValidNamespace(namespace) ? new Identifier(namespace, path) : null;
            } else {
                return new Identifier("minecraft", path);
            }
        } else {
            return isValidPath(identifier) ? new Identifier("minecraft", identifier) : null;
        }
    }

    public static DataResult<Identifier> read(String input) {
        try {
            return DataResult.success(parse(input));
        } catch (IdentifierException var2) {
            return DataResult.error(() -> "Not a valid resource location: " + input + " " + var2.getMessage());
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Identifier withPath(String newPath) {
        return new Identifier(this.namespace, assertValidPath(this.namespace, newPath));
    }

    public Identifier withPath(UnaryOperator<String> modifier) {
        return this.withPath(modifier.apply(this.path));
    }

    public Identifier withPrefix(String prefix) {
        return this.withPath(prefix + this.path);
    }

    public Identifier withSuffix(String suffix) {
        return this.withPath(this.path + suffix);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return !(o instanceof Identifier that) ? false : this.namespace.equals(that.namespace) && this.path.equals(that.path);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    // Normal compare sorts by path first, this compares namespace first.
    public int compareNamespaced(Identifier o) {
        int ret = this.namespace.compareTo(o.namespace);
        return ret != 0 ? ret : this.path.compareTo(o.path);
    }

    public int compareTo(Identifier o) {
        int result = this.path.compareTo(o.path);
        if (result == 0) {
            result = this.namespace.compareTo(o.namespace);
        }

        return result;
    }

    public Path resolveAgainst(Path root) {
        return root.resolve(this.getNamespace(), this.getPath());
    }

    public String toDebugFileName() {
        return this.toString().replace('/', '_').replace(':', '_');
    }

    public String toLanguageKey() {
        return this.namespace + "." + this.path;
    }

    public String toShortLanguageKey() {
        return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
    }

    public String toShortString() {
        return this.namespace.equals("minecraft") ? this.path : this.toString();
    }

    public String toLanguageKey(String prefix) {
        return prefix + "." + this.toLanguageKey();
    }

    public String toLanguageKey(String prefix, String suffix) {
        return prefix + "." + this.toLanguageKey() + "." + suffix;
    }

    private static String readGreedy(StringReader reader) {
        int start = reader.getCursor();

        while (reader.canRead() && isAllowedInIdentifier(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    public static Identifier read(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String raw = readGreedy(reader);

        try {
            return parse(raw);
        } catch (IdentifierException var4) {
            reader.setCursor(start);
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    public static Identifier readNonEmpty(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String raw = readGreedy(reader);
        if (raw.isEmpty()) {
            throw ERROR_INVALID.createWithContext(reader);
        } else {
            try {
                return parse(raw);
            } catch (IdentifierException var4) {
                reader.setCursor(start);
                throw ERROR_INVALID.createWithContext(reader);
            }
        }
    }

    public static boolean isAllowedInIdentifier(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    public static boolean isValidPath(String path) {
        for (int i = 0; i < path.length(); i++) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidNamespace(String namespace) {
        if (namespace.equals("..")) {
            return false;
        } else {
            for (int i = 0; i < namespace.length(); i++) {
                if (!validNamespaceChar(namespace.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static String assertValidNamespace(String namespace, String path) {
        if (!isValidNamespace(namespace)) {
            throw new IdentifierException("Non [a-z0-9_.-] character in namespace of identifier: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    public static boolean validPathChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    public static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    private static String assertValidPath(String namespace, String path) {
        if (!isValidPath(path)) {
            throw new IdentifierException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
        } else {
            return path;
        }
    }
}
