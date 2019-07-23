package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Entity;

public class JdbcCrudDao<E extends Entity, ID> implements CrudRepository<E, ID> {
    @Override
    public E save(E entity) {
        return null;
    }

    @Override
    public E findById(ID id) {
        return null;
    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    @Override
    public void deleteById(ID id) {

    }
}
