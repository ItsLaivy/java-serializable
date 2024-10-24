package codes.laivy.serializable.properties;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

final class SerializationPropertiesImpl implements SerializationProperties {

    private final @Nullable Father father;
    private final @Nullable Object outerInstance;

    private final @NotNull Set<Class<?>> typeConcretes;
    private final @NotNull Set<Class<?>> genericConcretes;

    public boolean bypassTransients;

    private final @NotNull Set<Field> includedFields;

    private @NotNull ContextFactory contextFactory;
    private @NotNull InstanceFactory instanceFactory;

    private @Nullable Adapter adapter;

    public SerializationPropertiesImpl(@Nullable Father father, @Nullable Object outerInstance, @NotNull Set<Class<?>> typeConcretes, @NotNull Set<Class<?>> genericConcretes, boolean bypassTransients, @NotNull Set<Field> includedFields, @NotNull ContextFactory contextFactory, @NotNull InstanceFactory instanceFactory, @Nullable Adapter adapter) {
        this.father = father;
        this.outerInstance = outerInstance;
        this.typeConcretes = typeConcretes;
        this.genericConcretes = genericConcretes;
        this.bypassTransients = bypassTransients;
        this.includedFields = includedFields;
        this.contextFactory = contextFactory;
        this.instanceFactory = instanceFactory;
        this.adapter = adapter;
    }

    // Modules

    @Override
    public @Nullable Father getFather() {
        return father;
    }

    @Override
    public @Nullable Object getOuterInstance() {
        return outerInstance;
    }

    @Override
    public @NotNull Collection<Class<?>> getTypeConcretes() {
        return typeConcretes;
    }
    @Override
    public @NotNull Collection<Class<?>> getGenericConcretes(@NotNull Type type) {
        return genericConcretes;
    }

    @Override
    public boolean isBypassTransients() {
        return bypassTransients;
    }
    @Override
    public void setBypassTransients(boolean bypassTransients) {
        this.bypassTransients = bypassTransients;
    }

    @Override
    public @NotNull Collection<Field> getIncludedFields() {
        return includedFields;
    }

    @Override
    public @NotNull ContextFactory getContextFactory() {
        return contextFactory;
    }
    @Override
    public void setConextFactory(@NotNull ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public @NotNull InstanceFactory getInstanceFactory() {
        return instanceFactory;
    }
    @Override
    public void setInstancFactory(@NotNull InstanceFactory instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    @Override
    public @Nullable Adapter getAdapter() {
        return adapter;
    }
    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        this.adapter = adapter;
    }

    // Implementations

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SerializationPropertiesImpl)) return false;
        SerializationPropertiesImpl that = (SerializationPropertiesImpl) object;
        return isBypassTransients() == that.isBypassTransients() && Objects.equals(getFather(), that.getFather()) && Objects.equals(getOuterInstance(), that.getOuterInstance()) && Objects.equals(getTypeConcretes(), that.getTypeConcretes()) && Objects.equals(genericConcretes, that.genericConcretes) && Objects.equals(getIncludedFields(), that.getIncludedFields()) && Objects.equals(getContextFactory(), that.getContextFactory()) && Objects.equals(getInstanceFactory(), that.getInstanceFactory()) && Objects.equals(getAdapter(), that.getAdapter());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getFather(), getOuterInstance(), getTypeConcretes(), genericConcretes, isBypassTransients(), getIncludedFields(), getContextFactory(), getInstanceFactory(), getAdapter());
    }

    @Override
    public @NotNull String toString() {
        return "SerializationPropertiesImpl{" +
                "father=" + father +
                ", outerInstance=" + outerInstance +
                ", typeConcretes=" + typeConcretes +
                ", genericConcretes=" + genericConcretes +
                ", bypassTransients=" + bypassTransients +
                ", includedFields=" + includedFields +
                ", contextFactory=" + contextFactory +
                ", instanceFactory=" + instanceFactory +
                ", adapter=" + adapter +
                '}';
    }

}
