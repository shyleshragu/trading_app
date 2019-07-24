package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class QuoteDao extends JdbcCrudDao<Quote, String>{
    private static Logger logger = LoggerFactory.getLogger(QuoteDao.class);

    private final static String sqlSelect = "SELECT * FROM ";
    private final static String sqlWhere = "WHERE ID = ?";
    private final static String sqlUpdate = "UPDATE ";


    private final static String TABLE_NAME = "quote";
    private final static String ID_NAME = "ticker";

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


    public Quote findById(String id) {
        return super.findById("trader_id", id, false, getEntityClass());
    }


    public Quote findByIdForUpdate(String id) {
        return super.findById("trader_id", id, true, getEntityClass());
    }

    @Override
    public boolean existsById(String id) {
        return super.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        super.deleteById(id);
    }

    public void update(List<Quote> singletonList) {
        String query = sqlUpdate + TABLE_NAME + "SET last_price=?, bid_price=?, bid_size=?, ask_price=?, ask_size=? WHERE ticker=?";
        int row = jdbcTemplate.update(query, singletonList, getIdName());
        logger.debug("Update quote rows= ", row);
        if (row != 1){
            throw new IncorrectResultSizeDataAccessException(1, row);
        }
    }

    public List<Quote> findAll() {
        return null;
    }







}
