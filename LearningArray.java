// TMF2954 Java Programming (G01_java)
// Project
// Emannuel Gill Tony (83753)
// Jong Xin Yi (84169)
// Tan Kun Jie (85883)
// Veroan Wee Zhing Sheng (85991)

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

// Implemented by ALL members
public class LearningArray extends JFrame implements LearningModuleInterface, QuizInterface, TimerInterface, ScoreInterface {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel mainMenuPanel;
    
    private String userName;
    private Map<String, List<Integer>> userScores = new HashMap<>(); //List to store user's scores
    
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<String> selectedAnswers; // List to store selected answers
    
    private JLabel timerLabel;
    private Timer quizTimer;
    private int timeRemaining; // Time remaining in seconds
    private final int QUESTION_TIME_LIMIT = 1800;
    
    private List<Question> questions = new ArrayList<>(); // List to store questions
    
    private JProgressBar progressBar;

    private JTextArea questionArea;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionsGroup;
    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;
    private JPanel submitPanel;
    private JButton startQuizButton;
    private JButton scoreButton;
    private JTextField fillInTheBlankField;
    private JPanel fillInTheBlankPanel;

    // Constructor
    public LearningArray() {
        setTitle("ITP: Array");
        setSize(375, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the frame to be non-resizable
        setResizable(false);

        // Add a component listener to prevent maximizing
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // Check if the window is maximized
                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    // Revert back to normal state and set the original size
                    setExtendedState(JFrame.NORMAL);
                    setSize(375, 700);
                }
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize questions
        initializeQuestions();

        // Initialize selectedAnswers list
        selectedAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));

        // Initialize and add the main menu panel
        mainMenuPanel = createMainMenuPanel();
        mainPanel.add(mainMenuPanel, "MainMenu");

        // Initialize user scores
        initializeUserScores();

        // Create and add the learning and quiz panels
        mainPanel.add(createLearningPanel(), "Learning");
        mainPanel.add(createQuizPanel(), "Quiz");
        mainPanel.add(createQuizStartPanel(), "QuizStart");

        // Show the main menu first
        cardLayout.show(mainPanel, "MainMenu");

        // Add panels to frame
        add(mainPanel, BorderLayout.CENTER);
    }

    // Array to store learning titles
    private String[] learningTitle = {
	    "C Array","C Array Declaration","Access Array Elements","C Array Initialization", "Array Traversal",
        "Random Access to the Elements","Access Elements Out of Its Bound!","Types of Array in C","Multidimensional Array in C","Three-Dimensional Array in C"
    };

    // Array to store learning contents
    private String[] learningContent = {
        "An array in C is a fixed-size collection of similar data items stored in contiguous memory locations." +
        "It can be used to store the collection of primitive data types such as int, char, float, etc., and derived " +
        "and user-defined data types such as pointers, structures, etc.",

        "In C, we must declare the array like any other variable before using it. We can declare an array by specifying " +
        "its name, the type of its elements, and the size of its dimensions. When we declare an array in C, the compiler " +
        "allocates the memory block of the specified size to the array name.\n\n" +
        "Syntax of Array Declaration:\n" +
        "data_type array_name [size];\n" +
        "or\n" +
        "data_type array_name [size1] [size2]...[sizeN];\n\n" +
        "The C arrays are static in nature, i.e., they are allocated memory at the compile time.",

        "We can access any element of an array in C using the array subscript operator [ ] and the index value i of the element.\n\n" +
        "array_name [index];\n\n" +
        "One thing to note is that the indexing in the array always starts with 0, i.e., the first element is at index 0 " +
        "and the last element is at N – 1 where N is the number of elements in the array.",

        "Initialization in C is the process to assign some initial value to the variable. When the array is declared or " +
        "allocated memory, the elements of the array contain some garbage value. So, we need to initialize the array to " +
        "some meaningful value. There are multiple ways in which we can initialize an array in C.\n\n" +
        "1. Array Initialization with Declaration\n" +
        "data_type array_name [size] = {value1, value2, ... valueN};\n\n" +
        "2. Array Initialization with Declaration without Size\n" +
        "data_type array_name[] = {1,2,3,4,5};",

        "Traversal is the process in which we visit every element of the data structure. For C array traversal, we use loops to iterate through each element of the array.",

	    "It is one of the defining properties of an Array in C. It means that we can randomly access any element in the array without touching any other element using its index. This property is the result of Contiguous Storage as a compiler deduces the address of the element at the given index by using the address of the first element and the index number.\n\n" +
	    "Address of i th = Address of 1st Element + (Index * Size of Each Element)",

	    "Suppose you declared an array of 10 elements. Let's say, " +
	    "Int a[6];\n\n" +
	    "You can access the array elements from a[0] to a[5].\n" +
	    "Now let's say if you try to access a[7]. The element is not available. This may cause unexpected output (undefined behaviour). Sometimes you might get an error and some other time your program may run correctly.\n" +
	    "Hence, you should never access elements of an array outside of its bound.",

	    "There are two types of arrays based on the number of dimensions it has. \n\n" +
	    "They are as follows:\n" +
	    "1. One Dimensional Arrays (1D Array)\n" +
	    "2. Multidimensional Arrays\n\n" +
	    "One Dimensional Array in C\n" +
	    "The One-dimensional arrays, also known as 1-D arrays in C are those arrays that have only one dimension.\n\n" +
	    "Syntax of 1D Array in C\n" +
	    "array_name [size];\n",

	    "Multi-dimensional Arrays in C are those arrays that have more than one dimension. Some of the popular multidimensional arrays are 2D arrays and 3D arrays. We can declare arrays with more dimensions than 3D arrays but they are avoided as they get very complex and occupy a large amount of space.\n\n" +
	    "Two-Dimensional Array in C\n" +
	    "A Two-Dimensional array or 2D array in C is an array that has exactly two dimensions. They can be visualized in the form of rows and columns organized in a two-dimensional plane.\n\n" +
	    "Syntax of 2D Array in C\n" +
	    "array_name[size1] [size2];\n\n" +
        "Here\n" +
        "• size1: Size of the first dimension.\n" +
        "• size2: Size of the second dimension.\n",

	    "Another popular form of a multi-dimensional array is Three Dimensional Array or 3D Array. A 3D array has exactly three dimensions. It can be visualized as a collection of 2D arrays stacked on top of each other to create the third dimension.\n\n" +
	    "Syntax of 3D Array in C\n" +
	    "array_name [size1] [size2] [size3];\n"
    };

    // Array to store images
    private String[] images = {"1.png","2.png","3.png","4.png","5.png","6.png","7.jpg","8.png","9.png","10.png","11.png"};
    private ImageIcon icon;

    // Preset data for user scores
    private void initializeUserScores() {
        List<Integer> scores1 = Arrays.asList(10);
        List<Integer> scores2 = Arrays.asList(12);
        List<Integer> scores3 = Arrays.asList(14);
        List<Integer> scores4 = Arrays.asList(13);

        // Add preset data to user scores
        userScores.put("Kun Jie", scores1);
        userScores.put("Emannuel", scores2);
        userScores.put("Veroan", scores3);
        userScores.put("Xin Yi", scores4);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Use BorderLayout for the main panel
    
        ImageIcon mainIcon = new ImageIcon(getClass().getResource(images[10]));
    
        JLabel iconLabel = new JLabel(mainIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the image
    
        // Create title label
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>Introduction to Programming<br>Topic: Arrays</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font size and style
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        // Create center panel to hold the buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    
        JButton btnLearning = new JButton("Learning");
        JButton btnQuiz = new JButton("Quiz");
    
        // Set size for buttons
        Dimension buttonSize = new Dimension(200, 50);
        btnLearning.setPreferredSize(buttonSize);
        btnLearning.setMinimumSize(buttonSize);
        btnLearning.setMaximumSize(buttonSize);
        btnQuiz.setPreferredSize(buttonSize);
        btnQuiz.setMinimumSize(buttonSize);
        btnQuiz.setMaximumSize(buttonSize);
    
        // Center the buttons horizontally
        btnLearning.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuiz.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        btnLearning.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Learning");
            }
        });
    
        btnQuiz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "QuizStart");
            }
        });
    
        // Add components to the center panel 
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Space between image and first button
        centerPanel.add(btnLearning);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between buttons
        centerPanel.add(btnQuiz);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 200)));
    
        // Add the title, image, and center panel to the main panel
        panel.add(titleLabel, BorderLayout.NORTH); // Add title label to the top
        panel.add(iconLabel, BorderLayout.CENTER); // Add image label to the center
        panel.add(centerPanel, BorderLayout.SOUTH); // Add center panel to the bottom
    
        return panel;
    }

    @Override
    public JPanel createLearningPanel() {
        JPanel learningPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout()); // Use BorderLayout for better control

        // Learning title
        JLabel learningTitleLabel = new JLabel();
        learningTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        learningTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        learningTitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10)); // Add some padding

        // Panel for the image
        ImagePanel imagePanel = new ImagePanel();

        // Panel for learning content
        JTextArea learningTextArea = new JTextArea();
        learningTextArea.setEditable(false);
        learningTextArea.setFocusable(false);
        learningTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        learningTextArea.setLineWrap(true);
        learningTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(learningTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 200)); // Set size for the text area
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scrollPane);

        learningTextArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Button panel for Next and Previous buttons
        JButton nextPageButton = new JButton("Next Page");
        JButton prevPageButton = new JButton("Previous Page");

        nextPageButton.addActionListener(e -> {
            currentQuestionIndex = (currentQuestionIndex + 1) % learningTitle.length;
            updatePageContent(currentQuestionIndex, learningTextArea, imagePanel, learningTitleLabel);
        });

        prevPageButton.addActionListener(e -> {
            currentQuestionIndex = (currentQuestionIndex - 1 + learningTitle.length) % learningTitle.length;
            updatePageContent(currentQuestionIndex, learningTextArea, imagePanel, learningTitleLabel);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(prevPageButton);
        buttonPanel.add(nextPageButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Add components to the content panel
        contentPanel.add(learningTitleLabel, BorderLayout.NORTH); // Add title label to the top
        contentPanel.add(imagePanel, BorderLayout.CENTER); // Add image panel to the center
        contentPanel.add(scrollPane, BorderLayout.SOUTH); // Add scroll pane to the bottom

        // Add content panel and button panel to the learning panel
        learningPanel.add(createNavigationBar(), BorderLayout.NORTH);
        learningPanel.add(contentPanel, BorderLayout.CENTER);
        learningPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial content setup
        updatePageContent(currentQuestionIndex, learningTextArea, imagePanel, learningTitleLabel);

        return learningPanel;
    }

    @Override
    public void updatePageContent(int index, JTextArea textArea, ImagePanel imagePanel, JLabel titleLabel) {
        titleLabel.setText(learningTitle[index]); // Set the learning title
        textArea.setText(learningContent[index]); // Set the learning content
        icon = new ImageIcon(getClass().getResource(images[index]));
        imagePanel.setImage(icon); // Set the image
    }

    private void initializeQuestions() {
        // Array to store MCQ questions
        String[] mcqQuestions = {
            "What is the correct way to declare an array in C?",
            "What is the index range for an array declared as" + "\n" +"int arr[5];?",
            "What is the output when you print the third element" + "\n" + "of an array initialized as {1, 2, 3}?",
            "Which of the following statements about arrays in C" + "\n" + "is false?",
            "How can you initialize an array of 10 integers to" + "\n" + "zero?",
            "What will be the result when trying to print the sixth" + "\n" +  "element of an array initialized as {1, 2, 3, 4, 5}?",
            "If an array is declared as int arr[10];, what is the" + "\n" + "type of arr?",
            "Which of the following is a valid way to access the" + "\n" +  "last element of an array declared as int arr[5];?",
            "What is the size of an array declared as char"  + "\n" +  "arr[20]; in bytes?",
            "What will happen if you try to access an array" + "\n" +  "element out of its bounds?"
        };

        // Array to store MCQ options
        String[][] mcqOptions = {
            {"A. int array[10];", "B. array int[10];", "C. int array;", "D. int array()"},
            {"A. 0 to 4", "B. 1 to 5", "C. 0 to 5", "D. 1 to 4"},
            {"A. 1", "B. 2", "C. 3", "D. Garbage value"},
            {"A. Arrays can be of any data type.", "B. The size of an array can be changed once it is declared.", "C. Arrays are stored in contiguous memory locations.", "D. The first element of an array is at index 0."},
            {"A. int arr[10] = 0;", "B. int arr[10] = {0};", "C. int arr[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};", "D. Both B and C"},
            {"A. 5", "B. 0", "C. Garbage value", "D. Compilation error"},
            {"A. int", "B. int *", "C. int[10]", "D. int &"},
            {"A. arr[4]", "B. arr[5]", "C. arr[6]", "D. arr[-1]"},
            {"A. 10 bytes", "B. 20 bytes", "C. 40 bytes", "D. Depends on the compiler"},
            {"A. Compilation error", "B. Runtime error", "C. Undefined behavior", "D. It will access the next array element"}
        };

        // Array to store MCQ answers
        String[] mcqAnswers = {
            "A", "A", "C", "B", "D", "C", "C", "A", "B", "C"
        };

        // Array to store True/False questions
        String[] tfQuestions = {
            "True or False:\nIn C, array indices start at 1.",
            "True or False:\nThe name of an array represents the address of" + "\n" +  "the first element of the array.",
            "True or False:\nAn array in C can hold elements of different" + "\n" +  "data types.",
            "True or False:\nYou cannot change the size of an array once it is" + "\n" +  "declared.",
            "True or False:\nArrays in C are zero-based, meaning the first" + "\n" +  "element is at index 0.",
            "True or False:\nThe size of an array must be a constant integer" + "\n" +  "expression.",
            "True or False:\nAccessing an array element out of its bounds" + "\n" +  "always results in a segmentation fault.",
            "True or False:\nIt is possible to initialize an array during its" + "\n" + "declaration.",
            "True or False:\nThe sizeof operator can be used to determine the" + "\n" +  "number of elements in an array.",
            "True or False:\nMultidimensional arrays in C are stored in" + "\n" + "row-major order."
        };

        // Array to store True/False answers
        String[] tfAnswers = {
            "B", "A", "B", "A", "A", "A", "B", "A", "B", "A"
        };

        // Array to store Fill In The Blank questions
        String[] fitbQuestions = {
            "The correct syntax to declare an array of integers" + "\n" +  "with 10 elements in C is ___.",
            "The index range for an array declared as int arr[5]" + "\n" + "is from ___ to ___. (Use , to seperate the number)",
            "To access the first element of an array in C, we" + "\n" + "use the index ___.",
            "In a multi-dimensional array arr[3][4][5], the total" + "\n" + "number of elements is ___.",
            "To declare a two-dimensional array of integers with" + "\n" + "3 rows and 4 columns, we use the syntax ___."
        };

        // Array to store Fill In The Blank answers
        String[] fitbAnswers = {
            "int array[10];",
            "0, 4",
            "0",
            "60",
            "int array[3][4];"
        };

        for (int i = 0; i < mcqQuestions.length; i++) {
            questions.add(new MultipleChoiceQuestion(mcqQuestions[i], mcqOptions[i], mcqAnswers[i]));
        }

        for (int i = 0; i < tfQuestions.length; i++) {
            questions.add(new TrueFalseQuestion(tfQuestions[i], tfAnswers[i]));
        }
        for (int i = 0; i < fitbQuestions.length; i++) {
            questions.add(new FillInTheBlankQuestion(fitbQuestions[i], fitbAnswers[i]));
        }
    }

    private JPanel createQuizStartPanel() {
        JPanel quizStartPanel = new JPanel(new BorderLayout());
        quizStartPanel.add(createNavigationBar(), BorderLayout.NORTH);

        // Create center panel to hold the buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        startQuizButton = new JButton("Start Quiz");
        scoreButton = new JButton("Leaderboard");

        // Set size for buttons
        Dimension buttonSize = new Dimension(200, 50);
        startQuizButton.setPreferredSize(buttonSize);
        startQuizButton.setMinimumSize(buttonSize);
        startQuizButton.setMaximumSize(buttonSize);
        scoreButton.setPreferredSize(buttonSize);
        scoreButton.setMinimumSize(buttonSize);
        scoreButton.setMaximumSize(buttonSize);

        // Center the buttons horizontally
        startQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userName = JOptionPane.showInputDialog("Enter your name:");
                if (userName != null && !userName.trim().isEmpty()) {
                    currentQuestionIndex = 0;
                    score = 0;
                    selectedAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));
                    loadQuestion();
                    startTimer();
                    updateButtonVisibility();
                    progressBar.setValue(0); // Reset the progress bar
                    cardLayout.show(mainPanel, "Quiz");
                }
            }
        });

        scoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showScores();
            }
        });

        // Add components to the center panel
        centerPanel.add(Box.createVerticalGlue()); // Add vertical space before the buttons
        centerPanel.add(startQuizButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between buttons
        centerPanel.add(scoreButton);
        centerPanel.add(Box.createVerticalGlue()); // Add vertical space after the buttons

        quizStartPanel.add(centerPanel, BorderLayout.CENTER);

        return quizStartPanel;
    }

    private JPanel createQuizPanel() {
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BorderLayout());

        // Top panel contains the question area, timer label, and progress bar
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JPanel insidetopPanel = new JPanel();
        insidetopPanel.setLayout(new BorderLayout());

        // Timer label 
        timerLabel = new JLabel("Time left: " + formatTime(QUESTION_TIME_LIMIT), SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Progress bar 
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        insidetopPanel.add(progressBar, BorderLayout.NORTH); // Add progress bar below the timer label

        // Question
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFocusable(false); 
        questionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        insidetopPanel.add(questionArea, BorderLayout.CENTER); // Add question area below the progress bar

        // Blank Field
        fillInTheBlankPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fillInTheBlankField = new JTextField(20); 
        fillInTheBlankPanel.add(fillInTheBlankField);
        insidetopPanel.add(fillInTheBlankPanel, BorderLayout.SOUTH);

        topPanel.add(timerLabel, BorderLayout.NORTH); // Add timer label to the top of the topPanel
        topPanel.add(insidetopPanel, BorderLayout.CENTER);
        quizPanel.add(topPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 1));

        optionButtons = new JRadioButton[4];
        optionsGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        quizPanel.add(optionsPanel, BorderLayout.CENTER);

        submitPanel = new JPanel();
        submitButton = new JButton("Submit");
        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");

        submitButton.addActionListener(e -> checkAnswer());
        previousButton.addActionListener(e -> loadPreviousQuestion());
        nextButton.addActionListener(e -> loadNextQuestion());

        submitPanel.add(previousButton);
        submitPanel.add(nextButton);
        submitPanel.add(submitButton);

        quizPanel.add(submitPanel, BorderLayout.SOUTH);

        loadQuestion(); // Load initial question
        updateButtonVisibility(); // Update button visibility based on currentQuestionIndex

        return quizPanel;
    }

    private void updateButtonVisibility() {
        if (currentQuestionIndex == 0) {
            previousButton.setVisible(false);
            nextButton.setVisible(true);
            submitButton.setVisible(false);
        } else if (currentQuestionIndex == questions.size() - 1) {
            previousButton.setVisible(true);
            nextButton.setVisible(false);
            submitButton.setVisible(true);
        } else {
            previousButton.setVisible(true);
            nextButton.setVisible(true);
            submitButton.setVisible(false);
        }

        submitPanel.revalidate(); // Refresh layout
        submitPanel.repaint(); // Repaint panel
    }

    @Override
    public void loadQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionArea.setText("Question " + (currentQuestionIndex + 1) + ".\n" + "\n" + question.getQuestionText());
            
            if (question instanceof MultipleChoiceQuestion || question instanceof TrueFalseQuestion) {
                fillInTheBlankField.setVisible(false);
                loadOptions(question.getOptions());
                String selectedAnswer = selectedAnswers.get(currentQuestionIndex);
                if (selectedAnswer != null) {
                    int selectedOptionIndex = getOptionIndex(selectedAnswer);
                    if (selectedOptionIndex != -1) {
                        optionButtons[selectedOptionIndex].setSelected(true);
                    }
                } else {
                    optionsGroup.clearSelection();
                }
            } else if (question instanceof FillInTheBlankQuestion) {
                fillInTheBlankField.setText(selectedAnswers.get(currentQuestionIndex) != null ? selectedAnswers.get(currentQuestionIndex) : "");
                fillInTheBlankField.setVisible(true);
                hideOptions();
            }
        }
    }

    private void hideOptions() {
        for (JRadioButton button : optionButtons) {
            button.setVisible(false);
        }
    }

    private void loadOptions(String[] currentOptions) {
        for (int i = 0; i < 4; i++) {
            if (i < currentOptions.length) {
                optionButtons[i].setText(currentOptions[i]);
                optionButtons[i].setFont(new Font("Arial", Font.BOLD, 12));
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false);
            }
        }
    }

    @Override
    public void loadNextQuestion() {
        saveSelectedAnswer(); // Save current selection before moving
        currentQuestionIndex++;
        loadQuestion();
        updateButtonVisibility();
        updateProgressBar(); // Update progress bar
    }

    @Override
    public void loadPreviousQuestion() {
        saveSelectedAnswer(); // Save current selection before moving
        currentQuestionIndex--;
        loadQuestion();
        updateButtonVisibility();
        updateProgressBar(); // Update progress bar
    }

    private void updateProgressBar() {
        int answeredQuestions = 0;
        for (String answer : selectedAnswers) {
            if (answer != null) {
                answeredQuestions++;
            }
        }
        progressBar.setValue(answeredQuestions);
    }

    private void saveSelectedAnswer() {
        Question question = questions.get(currentQuestionIndex);
        String previousAnswer = selectedAnswers.get(currentQuestionIndex);
    
        if (question instanceof FillInTheBlankQuestion) {
            String answer = fillInTheBlankField.getText().trim();
            selectedAnswers.set(currentQuestionIndex, answer.isEmpty() ? null : answer);
        } else {
            int selectedOptionIndex = -1;
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedOptionIndex = i;
                    break;
                }
            }
    
            if (selectedOptionIndex != -1) {
                String selectedOption = getOptionLetter(selectedOptionIndex);
                selectedAnswers.set(currentQuestionIndex, selectedOption);
            } else {
                selectedAnswers.set(currentQuestionIndex, null); // No option selected
            }
        }
    
        // Update progress bar if an answer was selected or changed
        updateProgressBar();
    }

    @Override
    public void checkAnswer() {
        // Save current selection before checking
        saveSelectedAnswer();

        // Check if all questions have been answered
        if (currentQuestionIndex < questions.size() - 1) {
            // Move to the next question
            currentQuestionIndex++;
            loadQuestion();
            updateButtonVisibility();
        } else {
            // Quiz has ended, calculate score and show message
            calculateScoreAndShowMessage();
            stopTimer(); // Stop the timer when the quiz is submitted
        }
    }

    @Override
    public void calculateScoreAndShowMessage() {
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // Calculate correct answers
        for (int i = 0; i < totalQuestions; i++) {
            String selectedOption = selectedAnswers.get(i);
            Question question = questions.get(i);
            if (selectedOption != null && question.isCorrect(selectedOption)) {
                correctAnswers++;
            }
        }

        // Update user scores
        if (!userScores.containsKey(userName)) {
            userScores.put(userName, new ArrayList<>());
        }
        userScores.get(userName).add(correctAnswers);

        // Show the score message in a new window
        ScoreMessagePanel scorePanel = new ScoreMessagePanel(userName, totalQuestions, correctAnswers, userScores);
        scorePanel.showScoreMessage();
        cardLayout.show(mainPanel, "QuizStart");
    }

    private int getOptionIndex(String optionLetter) {
        return optionLetter.charAt(0) - 'A';
    }

    private String getOptionLetter(int index) {
        return String.valueOf((char) ('A' + index));
    }

    @Override
    public void showScores() {
        JFrame leaderboardFrame = new JFrame("Leaderboard");
        leaderboardFrame.setSize(400, 600);
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        int totalQuestions = questions.size(); // Get the total number of questions
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(userScores, totalQuestions);
        leaderboardFrame.add(leaderboardPanel);

        leaderboardFrame.setVisible(true);
    }

    @Override
    public void startTimer() {
        if (quizTimer != null) {
            quizTimer.stop(); // Stop any existing timer
        }
        timeRemaining = QUESTION_TIME_LIMIT;
        updateTimerLabel();

        quizTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                updateTimerLabel();
                if (timeRemaining <= 0) {
                    quizTimer.stop();
                    JOptionPane.showMessageDialog(LearningArray.this, "Time's up!");
                    saveSelectedAnswer(); // Save the selected answer when time is up
                    calculateScoreAndShowMessage();
                    if (currentQuestionIndex < questions.size() - 1) {
                        loadNextQuestion(); // Move to the next question
                    } else {
                        calculateScoreAndShowMessage(); // End the quiz if it is the last question
                    }
                }
            }
        });
        quizTimer.start();
    }

    @Override
    public void updateTimerLabel() {
        timerLabel.setText("Time left: " + formatTime(timeRemaining));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @Override
    public void stopTimer() {
        if (quizTimer != null) {
            quizTimer.stop();
        }
    }

    private JPanel createNavigationBar() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(1, 3));

        JButton btnHome = new JButton("Home");
        JButton btnLearning = new JButton("Learning");
        JButton btnQuiz = new JButton("Quiz");

        btnHome.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MainMenu");
            }
        });

        btnLearning.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Learning");
            }
        });

        btnQuiz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "QuizStart");
            }
        });

        navigationPanel.add(btnHome);
        navigationPanel.add(btnLearning);
        navigationPanel.add(btnQuiz);

        return navigationPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LearningArray().setVisible(true);
            }
        });
    }
}

// Implemented by Emannuel Gill Tony (83753)
class ImagePanel extends JPanel {
    private Image image;

    public void setImage(ImageIcon icon) {
        if (icon != null) {
            this.image = icon.getImage();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Calculate image dimensions to maintain aspect ratio and fill width
            int imageWidth = panelWidth;
            int imageHeight = imageWidth * image.getHeight(this) / image.getWidth(this);

            // Center vertically
            int y = (panelHeight - imageHeight) / 2;

            g.drawImage(image, 0, y, imageWidth, imageHeight, this);
        }
    }
}

// Implemented by Jong Xin Yi (84169)
abstract class Question {
    protected String questionText;
    protected String[] options;
    protected String correctAnswer;

    public Question(String questionText, String[] options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public abstract boolean isCorrect(String answer);
}

// Implemented by Jong Xin Yi (84169)
class MultipleChoiceQuestion extends Question {
    public MultipleChoiceQuestion(String questionText, String[] options, String correctAnswer) {
        super(questionText, options, correctAnswer);
    }

    @Override
    public boolean isCorrect(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
}

// Implemented by Tan Kun Jie (85883)
class TrueFalseQuestion extends Question {
    public TrueFalseQuestion(String questionText, String correctAnswer) {
        super(questionText, new String[]{"True", "False"}, correctAnswer);
    }

    @Override
    public boolean isCorrect(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
}

// Implemented by Tan Kun Jie (85883)
class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(String questionText, String correctAnswer) {
        super(questionText, null, correctAnswer); // No options needed for fill-in-the-blank
    }

    @Override
    public boolean isCorrect(String answer) {
        // Shift the correct answer and the user-provided answer based on the question text
        String shiftedCorrectAnswer = shiftAnswer(correctAnswer, questionText);
        String shiftedUserAnswer = shiftAnswer(answer, questionText);
        return shiftedCorrectAnswer.equalsIgnoreCase(shiftedUserAnswer);
    }

    private String shiftAnswer(String answer, String questionText) {
        if (questionText.contains("0,4") || questionText.contains("range")) {
            // Remove spaces for answers related to range or specific numbers
            return answer.replaceAll("\\s+", "").toLowerCase();
        } else {
            // Trim spaces at the beginning and end, preserve inner spaces
            return answer.trim().toLowerCase();
        }
    }
}

// Implemented by Emannuel Gill Tony (83753) & Veroan Wee Zhing Sheng (85991)
class LeaderboardPanel extends JPanel {
    private Map<String, List<Integer>> userScores;
    private double percentage;
    private int totalQuestions;

    private int entryHeight = 40;
    private int rank = 1;
    private int x = 50;

    public LeaderboardPanel(Map<String, List<Integer>> userScores, int totalQuestions) {
        this.userScores = userScores;
        this.totalQuestions = totalQuestions;
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLeaderboard((Graphics2D) g);
    }

    private List<Map.Entry<String, Integer>> sortTheLeaderboard(Map<String, List<Integer>> userScores) {
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : userScores.entrySet()) {
            for (int score : entry.getValue()) {
                sortedScores.add(new AbstractMap.SimpleEntry<>(entry.getKey(), score));
            }
        }

        sortedScores.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return sortedScores;
    }

    private void drawLeaderboard(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sort users by their highest score
        List<Map.Entry<String, Integer>> sortedScores = sortTheLeaderboard(userScores);

        int width = getWidth();
        int height = getHeight();
        int y = 50;
        rank = 1;

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.BLACK);
        g2.drawString("LEADERBOARD", width / 2 - 100, y);
        y += 50;

        // Draw column labels
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Name", x, y);
        g2.drawString("Score", width - 100, y);
        y += 30; // Adjust spacing between labels and entries

        // Draw leaderboard entries
        for (Map.Entry<String, Integer> entry : sortedScores) {
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.setColor(Color.BLACK);
            g2.drawString(rank + ". " + entry.getKey(), x, y);
            percentage = (double) entry.getValue() /  totalQuestions * 100;
            g2.drawString(String.format("%.2f", percentage)+"%", width - 100, y);
            y += entryHeight;
            rank++;
        }
    }
}

// Implemented by Veroan Wee Zhing Sheng (85991)
class ScoreMessagePanel extends JPanel {
    private String userName;
    private int totalQuestions;
    private int correctAnswers;
    private Map<String, List<Integer>> userScores;

    public ScoreMessagePanel(String userName, int totalQuestions, int correctAnswers, Map<String, List<Integer>> userScores) {
        this.userName = userName;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.userScores = userScores;

        setPreferredSize(new Dimension(400, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate percentage and determine message
        double percentage = (double) correctAnswers / totalQuestions * 100;
        String message = determineMessage(percentage);

        // Display the detailed score message
        String detailedMessage = userName + ", you have scored " + String.format("%.2f", percentage) + "%!";
        String detailedMessage2 = "You have got " + correctAnswers + "/" + totalQuestions + ".";

        // Display messages
        g2.setFont(new Font("Arial", Font.BOLD, 30)); // Larger font for main message
        g2.setColor(Color.BLACK);
        g2.drawString(message, 50, 100);

        g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Smaller font for detailed message
        g2.setColor(Color.BLACK);
        g2.drawString(detailedMessage, 50, 150);

        g2.setFont(new Font("Arial", Font.PLAIN, 20)); 
        g2.setColor(Color.BLACK);
        g2.drawString(detailedMessage2, 50, 150);
    }

    private String determineMessage(double percentage) {
        if (percentage >= 80) {
            return "Outstanding!";
        } else if (percentage >= 60) {
            return "That's good!";
        } else if (percentage >= 40) {
            return "Good try!";
        } else if (percentage >= 20) {
            return "You can do better!";
        } else {
            return "Don't give up!";
        }
    }

    public void updateUserScores(Map<String, List<Integer>> userScores) {
        this.userScores = userScores;
        repaint(); // Refresh panel after updating scores
    }

    public void showScoreMessage() {
        double percentage = (double) correctAnswers / totalQuestions * 100;
        String message = determineMessage(percentage);

        String detailedMessage = userName + ", you have scored " + String.format("%.2f", percentage) + "%!";
        String detailedMessage2 = "You have got " + correctAnswers + "/" + totalQuestions + ".";

        // Create a new JDialog to show the message
        JDialog scoreDialog = new JDialog((Frame) null, "Score Message", true);
        scoreDialog.setLayout(new BorderLayout());
        scoreDialog.setSize(300, 200);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel mainMessageLabel = new JLabel(message);
        mainMessageLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font for main message
        mainMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel detailedMessageLabel = new JLabel(detailedMessage);
        detailedMessageLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Smaller font for detailed message
        detailedMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel detailedMessageLabel2 = new JLabel(detailedMessage2);
        detailedMessageLabel2.setFont(new Font("Arial", Font.PLAIN, 16)); 
        detailedMessageLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(mainMessageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between messages
        contentPanel.add(detailedMessageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        contentPanel.add(detailedMessageLabel2);

        scoreDialog.add(contentPanel, BorderLayout.CENTER);

        // Center the dialog on the screen
        scoreDialog.setLocationRelativeTo(null);
        scoreDialog.setVisible(true);
    }
}
