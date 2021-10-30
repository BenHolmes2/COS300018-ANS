import jadex.bridge.IExternalAccess;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;
import javax.swing.*;


public class FileIOGUI {

    public FileIOGUI() {
        JFrame frame=new JFrame();//creating instance of JFrame

        //creating title label
        JLabel titleLabel = new JLabel("File Input");
        titleLabel.setFont(new Font("Default", Font.BOLD, 20));
        titleLabel.setBounds( 100, 0, 200, 80);

        //creation of the agent name input box and its label as well as setting their location within the frame.
        JFileChooser fileInput = new JFileChooser();
        JLabel fileInputLabel = new JLabel("File Input:");
        fileInput.setBounds( 90, 60, 600, 600);
        fileInputLabel.setBounds( 10, 60, 80, 20);

        //adding all parts to frame
        frame.add(titleLabel);
        frame.add(fileInputLabel);
        frame.add(fileInput);

        //frame settings
        frame.setSize(800,700);//400 width and 500 height
        frame.setLayout(null);//using no layout managers
        frame.setVisible(true);//making the frame visible

        fileInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Handle open button action.
                System.out.println(e.getSource());
                System.out.println(e.getActionCommand());

                if (Objects.equals(e.getActionCommand(), "ApproveSelection")) {
                    // the file string is the path of the selected file. this only takes place on a file select not folder select
                    String file = fileInput.getSelectedFile().getAbsolutePath();
                    System.out.println(file);
                    //TODO: File Input code
                } else if(Objects.equals(e.getActionCommand(), "CancelSelection")){
                    frame.dispose();
                }
            }
        });
    }

}
