package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class QuoteDao extends JdbcCrudDao<Quote, String> {
    private final static String sqlSelect = "SELECT * FROM ";
    private final static String sqlWhere = "WHERE ID = ?";
    private final static String sqlUpdate = "UPDATE ";
    private final static String TABLE_NAME = "quote";
    private final static String ID_NAME = "ticker";
    private static Logger logger = LoggerFactory.getLogger(QuoteDao.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public QuoteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return simpleJdbcInsert;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdName() {
        return ID_NAME;
    }

    @Override
    Class getEntityClass() {
        return Quote.class;
    }

    @Override
    public Quote save(Quote entity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
        Number newId = simpleJdbcInsert.executeAndReturnKey(parameterSource);
        entity.setId(newId.toString());
        return entity;
    }

    public void update(List<Quote> quotes) {
        if (quotes.isEmpty())
            throw new IllegalArgumentException("Error:Empty quotes");
        String query = sqlUpdate + TABLE_NAME + "SET last_price=?, bid_price=?, bid_size=?, ask_price=?, ask_size=? WHERE ticker=?";

        List<Object[]> batch = new ArrayList<>();
        quotes.forEach(quote -> {
            if (!existsById(quote.getTicker())) {
                throw new ResourceNotFoundException("ticker not found: " + quote.getTicker());
            }
            Object[] values = new Object[]{
                    quote.getLastPrice(), quote.getBidPrice(), quote.getAskPrice(), quote.getAskSize(), quote.getTicker()
            };
            batch.add(values);
        });
        int[] rows = jdbcTemplate.batchUpdate(query, batch);
        int totalRow = Arrays.stream(rows).sum();
        if (totalRow != quotes.size())
            throw new IncorrectResultSizeDataAccessException("Number of rows ", quotes.size(), totalRow);
    }


    public List<Quote> findAll() {
        String selectSql = sqlSelect + TABLE_NAME;
        List<Quote> quoteslist = jdbcTemplate.query(selectSql, BeanPropertyRowMapper.newInstance(Quote.class));
        return quoteslist;
    }

}
