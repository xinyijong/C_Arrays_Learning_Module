// Implemented by Emannuel Gill Tony (83753)
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLabel;

public interface LearningModuleInterface {
    public JPanel createLearningPanel();
    public void updatePageContent(int index, JTextArea textArea, ImagePanel imagePanel, JLabel titleLabel);
}