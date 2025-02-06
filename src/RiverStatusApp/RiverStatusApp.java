package RiverStatusApp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RiverStatusApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(RiverStatusApp.class);
    private JTable table;
    private RiverDataFetcher dataFetcher;
    private TableRowSorter<DefaultTableModel> sorter;
    private boolean showingPolishData = true;
    private DefaultTableModel model_PL;
    private DefaultTableModel model_EN;
    private final String[] columnNames = {"River", "Name of Station", "Current Level", "Trend", "Warning Level", "Critical Level", "Update Time"};
    private final String[] columnNames_EN = {"Station Name", "Catchment", "River Name", "Gauge Datum (mAOD)", "Catchment Area (km2)"};

    public RiverStatusApp() {
        logger.info("Initializing RiverStatusApp");
        dataFetcher = new RiverDataFetcher();
        setTitle("River Status in Poland");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        model_PL = new DefaultTableModel(columnNames, 0);
        model_EN = new DefaultTableModel(columnNames_EN, 0);
        table = new JTable(model_PL);

        // Initialize with Polish data
        refreshData(true);

        // Sekcja odpowiadająca za sortowanie
        sorter = new TableRowSorter<>(model_PL);
        table.setRowSorter(sorter);

        sorter.addRowSorterListener(new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
//                    logger.info("Table sort order changed.");
//                    List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();
//                    if (!sortKeys.isEmpty() && sortKeys.get(0).getColumn() == 0) {
//                        List<RowSorter.SortKey> newSortKeys = new ArrayList<>();
//                        newSortKeys.add(new RowSorter.SortKey(0, sortKeys.get(0).getSortOrder()));
//                        newSortKeys.add(new RowSorter.SortKey(1, sortKeys.get(0).getSortOrder()));
//                        sorter.setSortKeys(newSortKeys);
                        logger.info("Applied secondary sort on 'Name of Station' column.");
//                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> {
            logger.info("Refresh button clicked.");
            refreshData(showingPolishData);
        });
        controlPanel.add(refreshButton, BorderLayout.WEST);

        JTextField searchField = new JTextField();
        controlPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            logger.info("Search button clicked with query: " + searchField.getText());
            search(searchField.getText());
        });
        controlPanel.add(searchButton, BorderLayout.EAST);

        JButton switchDataButton = new JButton("Switch Data Source");
        switchDataButton.addActionListener(e -> {
            logger.info("Switch Data Source button clicked.");
            showingPolishData = !showingPolishData;
            switchTableModel();
            refreshData(showingPolishData);
            updateTitle();
        });
        controlPanel.add(switchDataButton, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void switchTableModel() {
        if (showingPolishData) {
            table.setModel(model_PL);
            sorter = new TableRowSorter<>(model_PL);
        } else {
            table.setModel(model_EN);
            sorter = new TableRowSorter<>(model_EN);
        }
        table.setRowSorter(sorter);
    }

    private void refreshData(boolean polishData) {
        logger.info("Refreshing data");
        SwingWorker<Void, String[][]> worker = new SwingWorker<Void, String[][]>() {
            @Override
            protected Void doInBackground() {
                String[][] data = polishData ? dataFetcher.fetchData() : dataFetcher.fetchDataWale();
                publish(data);
                return null;
            }

            @Override
            protected void process(List<String[][]> chunks) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                logger.info("Processing the refresh");
                //for (String[][] chunk : chunks) {
                logger.info("chunks size: " + chunks.size());
                String[][] chunk = chunks.getFirst();

                if(polishData) {
	                logger.info("rows: " + chunk.length);
                	for (int tr = 0; tr < chunk.length; tr++) {
                        model.addRow(new Object[]{chunk[tr][0], chunk[tr][1], chunk[tr][2], chunk[tr][3], chunk[tr][4], chunk[tr][5], chunk[tr][6]});
                    }
	                logger.info("Polish Data Refreshed");
            	}else {
                	for (int tr = 0; tr < 20; tr++) {
                        model.addRow(new Object[]{chunk[tr][0], chunk[tr][1], chunk[tr][2], chunk[tr][3], chunk[tr][4]});
                    }
            		logger.info("English Data Refreshed");
            	}
            }

            @Override
            protected void done() {
                logger.info("Data refresh completed");
            }
        };
        worker.execute();
    }

    private void search(String query) {
        if (query.isEmpty()) {
            logger.info("Clearing search filter.");
            sorter.setRowFilter(null);
        } else {
            logger.info("Applying search filter for query: " + query);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0));
        }
    }

    private void updateTitle() {
        if (showingPolishData) {
            setTitle("River Status in Poland");
        } else {
            setTitle("River Status in the UK");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RiverStatusApp().setVisible(true));
    }
}
