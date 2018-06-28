import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	public static void main(String[] args) throws IOException {
		TreeMap<String, String> dictionary = new TreeMap<>();
		insertToDictionary(dictionary, "src/vnedict.txt");

		// Big frame of the program
		JFrame frame = new JFrame("Viet-Anh Dictionary");
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// text field for user to enter word
		JTextField textField = new JTextField(20);
		textField.setSize(200, 20);
		textField.setLocation(100, 0);
		frame.add(textField);

		// used to let user know imported successfully
		JLabel labelSuccess = new JLabel("Test");
		labelSuccess.setText("");
		labelSuccess.setBounds(400, 100, 200, 50);
		labelSuccess.setVerticalAlignment(JLabel.TOP);
		frame.add(labelSuccess);

		// used to add words to dictionary
		JButton buttonAddWords = new JButton("Import dictionary!");
		buttonAddWords.setBounds(400, 0, 200, 50);
		buttonAddWords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frameFileChooser = new JFrame("File to choose");
				frameFileChooser.setSize(new Dimension(500, 400));

				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
				frameFileChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frameFileChooser.add(chooser);

				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					String filePath = selectedFile.getPath();
					try {
						insertToDictionary(dictionary, filePath);
						labelSuccess.setText("Imported successfully!");
						labelSuccess.setForeground(Color.RED);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					chooser.setVisible(false);
				} else if (result == JFileChooser.CANCEL_OPTION) {
				}
			}
		});
		frame.add(buttonAddWords);

		// place for showing 10 suggestions
		JLabel labelSuggestion = new JLabel();
		labelSuggestion.setVerticalAlignment(0);
		frame.add(labelSuggestion);

		// event when user enter a character
		Action actionTextField = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getMean(textField, dictionary, labelSuggestion);
			}
		};

		textField.addActionListener(actionTextField);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("change");
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				getTenSuggestions(textField, dictionary, labelSuggestion);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				getTenSuggestions(textField, dictionary, labelSuggestion);
			}
		});

	}

	public static void insertToDictionary(TreeMap<String, String> dictionary, String filePath) throws IOException {
		FileReader in = new FileReader(filePath);

		BufferedReader br = new BufferedReader(in);
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains(":")) {
				String[] words = line.split(":");
				dictionary.put(words[0].trim(), words[1].trim());
			} else {
				if (line.contains(",")) {
					int index = line.indexOf(',');
					String first = line.substring(0, index).trim();
					String second = line.substring(index + 1).trim();
					dictionary.put(first, second);
				}
			}
		}
		br.close();
	}

	public static void getTenSuggestions(JTextField textField, TreeMap<String, String> treeMap, JLabel label) {
		long start = System.nanoTime();
		String first_key = textField.getText();
		if (first_key.length() == 0) {
			return;
		}
		String key = first_key;
		String next_key;
		StringBuilder results = new StringBuilder();
		results.append("<html>");
		int i = 0;
		if (treeMap.containsKey(key)) {
			results.append(key + "<br/>");
			i += 1;
		}

		while ((next_key = treeMap.higherKey(key)).contains(first_key) && i < 10) {
			key = next_key;
			results.append(key);
			results.append("<br/>");
			i += 1;
		}
		long end = System.nanoTime();
		results.append("<br/>" + "<br/>" + "<br/>" + "Time to search: " + (end - start) + " nano seconds" + "<br/>");
		results.append("</html>");
		label.setText(results.toString());
		label.setForeground(Color.BLACK);
	}

	public static void getMean(JTextField textField, TreeMap<String, String> treeMap, JLabel label) {
		String first_key = textField.getText();
		if (first_key.length() == 0) {
			return;
		}
		String key = first_key;
		StringBuilder results = new StringBuilder();
		results.append("<html>");
		if (treeMap.containsKey(key)) {
			results.append(key + "<br/><br/>" + "Meaning: " + treeMap.get(key) + "<br/>");
		} else {
			label.setText("");
			return;
		}
		results.append("</html>");
		label.setText(results.toString());
		label.setForeground(Color.RED);
	}

}
