package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Trader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;

@Repository
public class TraderDao extends JdbcCrudDao<Trader, Integer> {

    private static Logger logger = LoggerFactory.getLogger(TraderDao.class);

    private final String TABLE_NAME = "trader";
    private final String ID_COLUMN = "id";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public TraderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME).usingGeneratedKeyColumns(ID_COLUMN);
    }


    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return simpleInsert;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdName() {
        return ID_COLUMN;
    }

    @Override
    Class getEntityClass() {
        return Trader.class;
    }

    public Trader createTrader(String firstname, String lastname, String dob, String country, String email) {
        Trader trader = new Trader();
        trader.setFirstName(firstname);
        trader.setLastName(lastname);
        trader.setDob(LocalDate.parse(dob));
        trader.setCountry(country);
        trader.setEmail(email);
        return trader;
    }
}
