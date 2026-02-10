import javax.swing.*;
import ui.EntryPanel;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Parking System");
        frame.setContentPane(new EntryPanel());
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
