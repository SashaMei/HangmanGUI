import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class HangmanGUIByPH extends Application {

	private String currentWord; // the randomly selected word
	private String[] maskString; // String array to mask word to be guesses, length is fixed
	private String guessedLetters; // the letter inputed by the user
	private int wrongGussesCounter; // counter for wrong guess
	private ArrayList<String> wrongGuesses; // String arrayList to store wrong guesses, length is not fixed
	private TextField guessField; // the user enters their guess here
	private Text currentWordText; // show the current word (with - for unguessed letters)
	private Text outcomeText; // show the outcome of each guess and the game
	private Text wrongGuessesText; // show a list of incorrect guesses
	private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
	private Button playAgainButton;
	private final static int MAX_WRONG_GUESSES = 7;
	private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
	private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
	private static final Color WORD_COLOR = Color.rgb(224, 255, 255);

	public void start(Stage primaryStage) {
		startGame(primaryStage);
	}
	
	private void startGame(Stage stage) {
		VBox mainVBox = new VBox();
		mainVBox.setStyle("-fx-background-color: royalblue");
		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setSpacing(10);
		
		Text welcomeText = new Text("Welcome to Hangman!");
		welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
		welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		Text introText1 = new Text("Guess a letter.");
		Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
		introText1.setFont(Font.font("Helvetica", 24));
		introText1.setFill(INFO_COLOR);
		introText2.setFont(Font.font("Helvetica", 24));
		introText2.setFill(INFO_COLOR);

		VBox introBox = new VBox(welcomeText, introText1, introText2);
		introBox.setAlignment(Pos.CENTER);
		introBox.setSpacing(10);
		mainVBox.getChildren().add(introBox);

		initialize();

		currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
		currentWordText.setFill(WORD_COLOR);
		HBox currentBox = new HBox(currentWordText);
		currentBox.setAlignment(Pos.CENTER);
		currentBox.setSpacing(10);
		mainVBox.getChildren().add(currentBox);

		Text guessIntroText = new Text("Enter your guess: ");
		guessIntroText.setFont(Font.font("Helvetica", 26));
		guessIntroText.setFill(INFO_COLOR);
		guessField.setOnAction(this::handleGuessField);
		HBox guessBox = new HBox(guessIntroText, guessField);
		guessBox.setAlignment(Pos.CENTER);
		guessBox.setSpacing(10);
		mainVBox.getChildren().add(guessBox);

		outcomeText.setFont(Font.font("Helvetica", 28));
		outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		HBox outcomeBox = new HBox(outcomeText);
		outcomeBox.setAlignment(Pos.CENTER);
		outcomeBox.setSpacing(10);
		mainVBox.getChildren().add(outcomeBox);

		wrongGuessesText.setFont(Font.font("Helvetica", 24));
		wrongGuessesText.setFill(INFO_COLOR);
		HBox wrongGuessesBox = new HBox(wrongGuessesText);
		wrongGuessesBox.setAlignment(Pos.CENTER);
		wrongGuessesBox.setSpacing(10);
		mainVBox.getChildren().add(wrongGuessesBox);

		wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
		wrongGuessNumberText.setFill(INFO_COLOR);
		HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
		wrongGuessNumberBox.setAlignment(Pos.CENTER);
		mainVBox.getChildren().add(wrongGuessNumberBox);

		playAgainButton = new Button("Play again");
		playAgainButton.setOnAction(e -> {
			playAgainButton.setVisible(false);
			guessField.setDisable(false);
			reStart(stage);
	    });
		playAgainButton.setVisible(false);
		mainVBox.getChildren().add(playAgainButton);

		Scene scene = new Scene(mainVBox, 550, 500);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	private void reStart(Stage stage) {
		initialize();
	    startGame(stage);
	}
	
	private void initialize() {
		guessedLetters = "";
		wrongGussesCounter = 0;
		wrongGuesses = new ArrayList<String>();
		outcomeText = new Text("");
		guessField = new TextField();
		wrongGuessNumberText = new Text("Number of wrong guesses remaining: " + MAX_WRONG_GUESSES);
		currentWord = chooseWord();
		// Set mask words to be guessed
		maskString = new String[currentWord.length()];
		for(int i = 0; i < currentWord.length(); i++) {
			maskString[i] = "-";
		}
		currentWordText = new Text(displayWord());
		wrongGuessesText = new Text("Wrong Guesses: []");		
	}
	
	private void handleGuessField(ActionEvent event) {
		// YOUR CODE HERE
		try {
			String letter = guessField.getText();
			guessField.clear();
			
			// Get valid input first
			if(letter.isEmpty() ) {
				throw new InvalidInputException("Guesses cannot be empty");
			}else if(letter.length() != 1) {
				throw new InvalidInputException("Guesses must be one character only");
			}else if(!Character.isAlphabetic(letter.charAt(0))) {
				throw new InvalidInputException("Guesses must be characters only");
			}
			
			letter = letter.toUpperCase();
			
			// Check if the letter user input is guessed or not
			if(!isGuessed(guessedLetters, letter)) {
				guessedLetters += letter;
				// Reveal Word
				revealWord(letter);	
				//Check if the player guess the word or used out chances
				if(!displayWord().contains("-") || wrongGussesCounter == MAX_WRONG_GUESSES) {
					guessField.setDisable(true);
					playAgainButton.setVisible(true);
					if(!displayWord().contains("-")) {
						outcomeText.setText("You did it! Great job!");
					}else {
						outcomeText.setText("Better luck next time(" + currentWord +")");
					}	
				}
			}		
		}catch(InvalidInputException ex) {
			outcomeText.setText(ex.getMessage());
		}	
	}
		
	
	private boolean isGuessed(String guessedLetters, String letter) {
		if(guessedLetters.contains(letter)) {
			outcomeText.setText("You've already guessed that letter.");
			return true;
		}else {
			return false;
		}
	}
	
	private void revealWord(String letter) {
		if(currentWord.contains(letter)) {
			outcomeText.setText("Good guess!");
			for(int i = 0; i < currentWord.length(); i++) {
				if(letter.equals(currentWord.charAt(i) + "")) {
					maskString[i] = letter;
				}
			}
			// Show word after reveal
			currentWordText.setText(displayWord());
		}else { // count as a miss;
			wrongGussesCounter++;
			wrongGuesses.add(letter);
			wrongGuessesText.setText("Wrong Guesses: "+ wrongGuesses.toString());
			wrongGuessNumberText.setText("Number of wrong guesses remaining: " + (MAX_WRONG_GUESSES - wrongGussesCounter));
			if(wrongGussesCounter < MAX_WRONG_GUESSES) {
				outcomeText.setText("Nope, try again.");
			}
		}
	}
	
	private String displayWord() {
		String maskedWord = "";
		for(String s : maskString) {
			maskedWord += s + " ";
		}
		return maskedWord;
	}
	
	private String chooseWord() {
		// YOUR CODE HERE
		Scanner fileScan = null;
		ArrayList<String> wordList = new ArrayList<String>();
		Random random = new Random();
		
		try {
			fileScan = new Scanner(new FileReader(new File("words.txt")));
			while (fileScan.hasNext()) {
				wordList.add(fileScan.nextLine());
			}
		} catch (FileNotFoundException e) {
			//Need modify
			outcomeText = new Text(e.getMessage());
			guessField.setDisable(true);
		} finally {
			if(fileScan!=null) {
				fileScan.close();
			}
		}

		return wordList.get(random.nextInt(wordList.size())).toUpperCase();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
	
