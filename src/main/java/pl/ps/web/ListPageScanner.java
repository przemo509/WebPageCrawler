package pl.ps.web;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.ps.database.ItemDao;
import pl.ps.database.ItemDto;
import pl.ps.properties.PageProperties;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ListPageScanner {
    private final PageProperties pageProperties;
    private final ItemDao itemDao;

    public ListPageScanner(PageProperties pageProperties) throws ClassNotFoundException, SQLException {
        this.pageProperties = pageProperties;
        Class.forName("org.sqlite.JDBC");
        itemDao = new ItemDao(DriverManager.getConnection("jdbc:sqlite:data/database.sqlite"));
    }

    public void load() {
        for (int i = 1; i <= pageProperties.getPagesToCheck(); i++) {
            String currentPageUrl = pageProperties.getListPage() + i;
            try {
                Connection connection = Jsoup.connect(currentPageUrl);
                connection.timeout(0);
                Element document = connection.get();
                Elements items = pageProperties.applyItemElementsPattern(document);
                int itemsCount = 0;
                for (Element item : items) {
                    try {
                        itemsCount++;
                        if (pageProperties.isNodeExcluded(item)) {
//                            System.out.println("Pominięto: " + item);
                            continue;
                        }
                        String url = pageProperties.applyUrlPattern(item);

                        Integer itemId = pageProperties.applyItemIdPattern(item);
                        Integer score = pageProperties.applyScorePattern(item);

                        Date date = pageProperties.applyDatePattern(item);
                        itemDao.insertOrUpdate(new ItemDto(pageProperties.getConfigName(), itemId, url, date, score, today(), null));
                    } catch (AssertionError e) {
                        e.printStackTrace(System.out);
                    }
                }
                System.out.println(pageProperties.getConfigName() + " Strona " + i + ", znaleziono: " + itemsCount);
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout na stronie " + i);
            } catch (IOException e) {
                System.out.println("Błąd na stronie " + i);
                e.printStackTrace(System.out);
            } catch (Throwable e) {
                System.out.println("Błąd na stronie " + i);
                e.printStackTrace(System.out);
            }
        }
    }

    public void openPages() throws SQLException {
        List<ItemDto> list = itemDao.list(pageProperties.getConfigName(), pageProperties.getScoreThreshold(), pageProperties.getDelayDays());
        System.out.println(pageProperties.getConfigName() + " - warunki spełnia " + list.size() + " stron:");
        for (int i = 0; i < list.size(); i++) {
            ItemDto itemDto = list.get(i);
            System.out.println(pageProperties.getConfigName() + " " + i + " " + itemDto.getScore() + " " + itemDto.getUrl());
        }
    }

    private Date today() {
        return new Date(System.currentTimeMillis());
    }
}
