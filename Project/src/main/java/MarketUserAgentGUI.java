import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringJoiner;
import javax.swing.*;

public class MarketUserAgentGUI extends JFrame {

    File orderFile = null;

    JTextArea catTextArea = new JTextArea();
    JTextArea settleTextArea = new JTextArea();

    public MarketUserAgentGUI(final IExternalAccess agent, final MarketUserAgent mkAgent) {

        JFrame frame = new JFrame(); //creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("User: " + agent.getComponentIdentifier().getName());
        titleLabel.setFont(new Font("Default", Font.BOLD, 20));
        titleLabel.setBounds(50, 0, 500, 80);

        // Creation of non-editable textArea to preview pending orders
        JLabel pendingLabel = new JLabel("Pending Orders");
        pendingLabel.setFont(new Font("Default", Font.BOLD, 14));
        pendingLabel.setBounds(50, 60, 150, 20);

        JTextArea pendingOrderTextArea = new JTextArea();
        pendingOrderTextArea.setEditable(false);
        JScrollPane pendingOrdersScroll = new JScrollPane(pendingOrderTextArea);
        pendingOrdersScroll.setPreferredSize(new Dimension(400, 200));
        pendingOrdersScroll.setBounds(50, 80, 400, 200);

        // The File input button
        JLabel orderFileLabel = new JLabel("Order file : ");
        orderFileLabel.setBounds(50, 280, 200, 30);
        JButton fileButton = new JButton("Load Orders From File");
        fileButton.setBounds(50, 310, 180, 30);

        // the create button creation and location setting
        JButton readOrderFromFileButton = new JButton("Send Orders");
        readOrderFromFileButton.setBounds(330, 310, 120, 30);

        // Creation of non-editable textArea to display Catalogue
        JLabel catLabel = new JLabel("Catalogue");
        catLabel.setFont(new Font("Default", Font.BOLD, 14));
        catLabel.setBounds(50, 360, 150, 20);

        catTextArea = new JTextArea();
        catTextArea.setEditable(false);
        JScrollPane catScroll = new JScrollPane(catTextArea);
        catScroll.setPreferredSize(new Dimension(400, 200));
        catScroll.setBounds(50, 380, 400, 100);

        JButton refreshCatButton = new JButton("Refresh Catalogue");
        refreshCatButton.setBounds(50, 490, 150, 30);

        // Creation of non-editable textArea to display Settlements
        JLabel settleLabel = new JLabel("Settlement Log");
        settleLabel.setFont(new Font("Default", Font.BOLD, 14));
        settleLabel.setBounds(50, 540, 150, 20);

        settleTextArea = new JTextArea();
        settleTextArea.setEditable(false);
        JScrollPane settleScroll = new JScrollPane(settleTextArea);
        settleScroll.setPreferredSize(new Dimension(400, 200));
        settleScroll.setBounds(50, 560, 400, 100);


        //adding all parts to frame
        frame.add(titleLabel);
        frame.add(pendingLabel);
        frame.add(pendingOrdersScroll);
        frame.add(fileButton);
        frame.add(orderFileLabel);
        frame.add(readOrderFromFileButton);

        frame.add(catLabel);
        frame.add(catScroll);
        frame.add(refreshCatButton);

        frame.add(settleLabel);
        frame.add(settleScroll);

        //frame settings
        frame.setSize(512, 720);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        fileButton.addActionListener(e -> {
            JFileChooser orderFileChooser = new JFileChooser(".");
            int option = orderFileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                orderFile = orderFileChooser.getSelectedFile();
                orderFileLabel.setText("Order file : " + orderFile.getName());
                try {
                    pendingOrderTextArea.append(Files.readString(Paths.get(String.valueOf(orderFile))));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        readOrderFromFileButton.addActionListener(e -> agent.scheduleStep(ia -> {
            if (orderFile == null) {
                orderFileLabel.setText("Order file : not selected");
            } else {
                try {
                    mkAgent.sendOrder(mkAgent.readOrders(orderFile.getPath()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return IFuture.DONE;
        }));

        refreshCatButton.addActionListener(e -> {
            agent.scheduleStep(ia -> {
                mkAgent.requestCatalogue();
                return IFuture.DONE;
            });
        });
    }

    public void refreshCatalogue(final Catalogue cat, final MarketUserAgent mkAgent) throws JsonProcessingException {
        String catalogueJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(cat);
        catTextArea.setText(catalogueJsonString);
    }

    public void addSettlement(ArrayList<String> settlement) {
        StringJoiner jointSettlement = new StringJoiner(", ");
        settlement.forEach(jointSettlement::add);
        settleTextArea.append(jointSettlement.toString() + "\n");
    }
}
