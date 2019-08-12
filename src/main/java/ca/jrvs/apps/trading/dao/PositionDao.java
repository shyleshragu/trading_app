package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PositionDao {
    private static final Logger logger = LoggerFactory.getLogger(Position.class);

    private final String TABLE_NAME = "position";
    private final String ID_NAME = "account_id";
    private final String selectSQL = "SELECT * FROM";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PositionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @SuppressWarnings("unchecked")
    public List<Position> findByAccount(Integer accountId) {
        if (accountId == null)
            throw new IllegalArgumentException("AccountId was null");

        List<Position> trader = null;
        String sql = selectSQL + TABLE_NAME + " WHERE " + ID_NAME + " =?";
        logger.info(sql);

        try {
            trader = (List<Position>) jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Position.class), accountId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("Trader id not found: " + accountId, ex);
        }

        if (trader == null)
            throw new ResourceNotFoundException("Resource not found");

        return trader;
    }

    public Position findByTickerAndAccount(String ticker, Integer accountId) {
        if (accountId == null || ticker.isEmpty())
            throw new IllegalArgumentException("Null arguments present");

        String sql = selectSQL + TABLE_NAME + " WHERE " + ID_NAME + " =? AND ticker =?";
        Position pos = (Position) jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Position.class), accountId, ticker);
        return pos;
    }
}
