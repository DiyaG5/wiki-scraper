import com.microsoft.playwright.*;
import java.util.*;

public class TableUtil {
    public static List<List<String>> extractTables(Page page, String selector) {
        List<List<String>> allTables = new ArrayList<>();
        Locator tables = page.locator(selector);

        for (int t = 0; t < tables.count(); t++) {
            Locator rows = tables.nth(t).locator("tr");
            List<List<String>> table = new ArrayList<>();

            for (int i = 0; i < rows.count(); i++) {
                List<String> row = new ArrayList<>();
                Locator cells = rows.nth(i).locator("th, td");
                for (int c = 0; c < cells.count(); c++) {
                    row.add(cells.nth(c).textContent().trim());
                }
                if (!row.isEmpty()) table.add(row);
            }

            if (!table.isEmpty()) allTables.addAll(table);
        }

        return allTables;
    }
}