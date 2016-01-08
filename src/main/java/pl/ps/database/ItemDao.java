package pl.ps.database;

import org.apache.commons.lang3.StringUtils;
import pl.ps.utils.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {
//TODO zrobić insert i update jednym batchem tak jak tu: http://blog.rungeek.com/post/81611917/how-to-use-sqlite-with-java
    PreparedStatement listStmt;
    PreparedStatement readStmt;
    PreparedStatement insertStmt;
    PreparedStatement updateStmt;

    public ItemDao(Connection connection) throws SQLException {
        String[] columnList = {"config_name", "id", "url", "creation_date", "score", "score_date", "visit_date"};
        String columns = StringUtils.join(columnList, ", ");
        String listQuery = "SELECT " + columns + " FROM item WHERE config_name = ? AND score >= ? AND creation_date < ?";
        String readQuery = "SELECT " + columns + " FROM item WHERE config_name = ? AND id = ?"; // todo order
        String insertQuery = "INSERT INTO item(" + columns + ") VALUES (" + columns.replaceAll("\\w+", "?") + ")";
        String updateQuery = "UPDATE item SET ";
        for (int i = 0; i < columnList.length; i++) {
            String column = columnList[i];
            updateQuery += (i > 0 ? ", " : "") + column + " = ?";
        }
        updateQuery += " WHERE config_name = ? AND id = ?";

        listStmt = connection.prepareStatement(listQuery);
        readStmt = connection.prepareStatement(readQuery);
        insertStmt = connection.prepareStatement(insertQuery);
        updateStmt = connection.prepareStatement(updateQuery);
    }

    public List<ItemDto> list(String configName, Integer scoreThreshold, Integer delayDays) throws SQLException {
        int c = 0;
        listStmt.setString(++c, configName);
        listStmt.setInt(++c, scoreThreshold);
        setDate(listStmt, ++c, new Date(System.currentTimeMillis() - delayDays * 24 * 60 * 60 * 1000));

        listStmt.execute();
        List<ItemDto> list = mapToDtoList(listStmt.getResultSet());
        return list;
    }

    public ItemDto read(String configName, Integer id) throws SQLException {
        readStmt.setString(1, configName);
        readStmt.setInt(2, id);

        readStmt.execute();
        List<ItemDto> list = mapToDtoList(readStmt.getResultSet());
        Assert.isTrue(list.size() <= 1, "ItemDao.read(" + configName + ", " + id + ") zwróciło " + list.size() + " wierszy!");
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(ItemDto dto) throws SQLException {
        setDto(insertStmt, dto);
        insertStmt.execute();
    }

    public void update(ItemDto dto) throws SQLException {
        int usedParams = setDto(updateStmt, dto);
        updateStmt.setString(++usedParams, dto.getConfigName());
        updateStmt.setInt(++usedParams, dto.getId());
        updateStmt.execute();
    }

    public void insertOrUpdate(ItemDto itemDto) throws SQLException {
        if(read(itemDto.getConfigName(), itemDto.getId()) == null) {
            insert(itemDto);
        } else {
            update(itemDto);
        }
    }

    private int setDto(PreparedStatement stmt, ItemDto dto) throws SQLException {
        int c = 0;
        stmt.setString(++c, dto.getConfigName());
        stmt.setInt(++c, dto.getId());
        stmt.setString(++c, dto.getUrl());
        setDate(stmt, ++c, dto.getCreationDate());
        stmt.setInt(++c, dto.getScore());
        setDate(stmt, ++c, dto.getScoreDate());
        setDate(stmt, ++c, dto.getVisitDate());
        return c;
    }

    private void setDate(PreparedStatement stmt, int c, Date date) throws SQLException {
        if(date == null) {
            stmt.setNull(c, Types.INTEGER);
        } else {
            stmt.setLong(c, date.getTime() / 1000);
        }
    }

    private List<ItemDto> mapToDtoList(ResultSet rs) throws SQLException {
        List<ItemDto> list = new ArrayList<ItemDto>();
        while(rs.next()) {
            ItemDto dto = new ItemDto(
                    rs.getString("config_name"),
                    rs.getInt("id"),
                    rs.getString("url"),
                    getDate(rs, "creation_date"),
                    rs.getInt("score"),
                    getDate(rs, "score_date"),
                    getDate(rs, "visit_date")
            );
            list.add(dto);
        }
        return list;
    }

    private Date getDate(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName);
        return date != null ? new Date(date.getTime() * 1000) : null;
    }
}
