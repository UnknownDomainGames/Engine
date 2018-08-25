package unknowndomain.engine.mod;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ModRepositoryCollection implements ModRepository {
    private List<ModRepository> list = Lists.newArrayList();

    public ModRepositoryCollection add(ModRepository repository) {
        list.add(Validate.notNull(repository));
        return this;
    }

    public List<ModRepository> getList() {
        return list;
    }

    @Override
    public Collection<RemoteModMetadata> fetchIndex() {
        return Collections.emptyList();
    }

    @Override
    public InputStream open(ModIdentifier identifier) {
        for (ModRepository repository : list) {
            if (repository.contains(identifier)) {
                return repository.open(identifier);
            }
        }
        return null;
    }

    @Override
    public boolean contains(ModIdentifier identifier) {
        for (ModRepository repository : list) {
            if (repository.contains(identifier)) {
                return true;
            }
        }
        return false;
    }
}
