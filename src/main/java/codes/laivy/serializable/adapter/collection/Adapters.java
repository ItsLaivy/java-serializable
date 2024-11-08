package codes.laivy.serializable.adapter.collection;

import codes.laivy.serializable.adapter.Adapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

// todo: javadocs
public interface Adapters extends Collection<Adapter> {

    @NotNull Optional<Adapter> getByReference(@NotNull Class<?> reference);
    boolean contains(@NotNull Class<?> reference);

    /**
     * Esse método retorna false caso já exista um adaptador com alguma das referências
     * do adaptador do parâmetro
     * @param adapter element whose presence in this collection is to be ensured
     * @return
     */
    @Override
    boolean add(@NotNull Adapter adapter);
    @Override
    boolean remove(@NotNull Object o);

}
