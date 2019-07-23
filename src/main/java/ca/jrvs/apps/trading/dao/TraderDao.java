package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Trader;

public class TraderDao implements CrudRepository<Trader, Integer>{

    @Override
    public Trader save(Trader entity) {
        return null;
    }

    @Override
    public Trader findById(Integer integer) {
        return null;
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public void deleteById(Integer integer) {

    }
}
